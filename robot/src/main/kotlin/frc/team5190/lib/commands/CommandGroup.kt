package frc.team5190.lib.commands

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

abstract class CommandGroup(private val commands: List<Command>) : Command() {

    companion object {
        private val commandGroupContext = newFixedThreadPoolContext(2, "Command Group")
    }

    protected lateinit var commandTasks: List<GroupCommandTask>
        private set
    override val requiredSubsystems = commands.map { it.requiredSubsystems }.flatten()

    private val actorMutex = Mutex()
    private var started = false
    private lateinit var startDeferred: CompletableDeferred<Unit>
    protected val activeCommands = mutableListOf<GroupCommandTask>()

    private sealed class GroupEvent {
        object StartEvent : GroupEvent()
        class FinishEvent(val task: GroupCommandTask) : GroupEvent()
    }

    private lateinit var groupActor: SendChannel<GroupEvent>

    private suspend fun handleEvent(event: GroupEvent) {
        when (event) {
            is GroupEvent.StartEvent -> {
                if (commands.isEmpty()) {
                    started = true
                    groupCondition.invoke() // End early since there is no commands in this group
                } else {
                    handleStartEvent()
                    started = true
                }
            }
            is GroupEvent.FinishEvent -> {
                val task = event.task
                // Discard if the command somehow gets removed twice
                if (!activeCommands.contains(task)) return
                task.dispose()
                activeCommands.remove(task)
                if (activeCommands.isEmpty()) {
                    // Stop early since the command is finished
                    groupCondition.invoke()
                    return
                }
                handleFinishEvent()
            }
        }
    }

    private inner class GroupCondition : Condition() {
        fun invoke() = invokeCompletionListeners()
        override fun isMet() = started && activeCommands.isEmpty()
    }

    private val groupCondition = GroupCondition()

    init {
        updateFrequency = 0
        finishCondition += groupCondition
    }

    protected abstract suspend fun handleStartEvent()
    protected open suspend fun handleFinishEvent() {}

    override suspend fun initialize() {
        started = false
        startDeferred = CompletableDeferred()
        commandTasks = commands.map { GroupCommandTask(it) }
        groupActor = actor(context = commandGroupContext, capacity = Channel.UNLIMITED) {
            actorMutex.withLock {
                startDeferred.complete(Unit)
                for (event in channel) {
                    handleEvent(event)
                }
            }
        }
        groupActor.send(GroupEvent.StartEvent)
    }

    override suspend fun dispose() {
        groupActor.close()
        // Wait for the actor to start and gain priority (Because we don't want to stop actor before it starts)
        startDeferred.await()
        // Wait for the actor is release lock (cheat for detecting when actor finishes)
        actorMutex.withLock {
            for (activeCommand in activeCommands) {
                activeCommand.dispose()
            }
            activeCommands.clear()
        }
    }

    protected inner class GroupCommandTask(command: Command) : CommandHandler.CommandTask(command) {
        override suspend fun stop() = groupActor.send(GroupEvent.FinishEvent(this))
    }
}

open class ParallelCommandGroup(commands: List<Command>) : CommandGroup(commands) {
    override suspend fun handleStartEvent() {
        activeCommands += commandTasks
        // Start all commands so they run in parallel
        for (activeCommand in activeCommands) {
            activeCommand.initialize()
        }
    }
}

open class SequentialCommandGroup(commands: List<Command>) : CommandGroup(commands) {
    override suspend fun handleStartEvent() {
        activeCommands += commandTasks
        startNextCommand() // Start only the first command
    }

    override suspend fun handleFinishEvent() = startNextCommand() // Start next command
    /**
     * Starts only the top command (each time a command finishes it gets auto removed from this list)
     */
    private suspend fun startNextCommand() = activeCommands.first().initialize()
}