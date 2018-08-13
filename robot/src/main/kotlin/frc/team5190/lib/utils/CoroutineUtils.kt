package frc.team5190.lib.utils

import kotlinx.coroutines.experimental.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

fun launchFrequency(
        frequency: Int = 50,
        context: CoroutineContext = DefaultDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        parent: Job? = null,
        onCompletion: CompletionHandler? = null,
        block: suspend CoroutineScope.() -> Unit
): Job {
    if (frequency <= 0) throw IllegalArgumentException("Frequency cannot be lower then 1!")
    return launch(context, start, parent, onCompletion) {
        val timeBetweenUpdate = TimeUnit.SECONDS.toNanos(1) / frequency
        // Stores when the next update should happen
        var nextNS = System.nanoTime() + timeBetweenUpdate
        while (isActive) {
            block(this)
            val delayNeeded = nextNS - System.nanoTime()
            nextNS += timeBetweenUpdate
            delay(delayNeeded, TimeUnit.NANOSECONDS)
        }
    }
}