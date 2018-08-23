package frc.team5190.robot.auto.routines

import frc.team5190.lib.commands.Command
import frc.team5190.lib.commands.ConditionCommand
import frc.team5190.lib.commands.DelayCommand
import frc.team5190.lib.commands.condition
import frc.team5190.lib.extensions.parallel
import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Translation2d
import frc.team5190.lib.utils.Source
import frc.team5190.lib.utils.invokeWhenTrue
import frc.team5190.lib.utils.map
import frc.team5190.lib.utils.withEquals
import frc.team5190.robot.auto.StartingPositions
import frc.team5190.robot.auto.Trajectories
import frc.team5190.robot.subsytems.SubsystemPreset
import frc.team5190.robot.subsytems.drive.FollowTrajectoryCommand
import frc.team5190.robot.subsytems.intake.IntakeCommand
import frc.team5190.robot.subsytems.intake.IntakeSubsystem
import frc.team5190.robot.subsytems.led.BlinkingLEDCommand
import openrio.powerup.MatchData
import java.awt.Color
import java.util.concurrent.TimeUnit

class RoutineSwitchFromCenter(startingPosition: Source<StartingPositions>,
                              private val switchSide: Source<MatchData.OwnedSide>) : AutoRoutine(startingPosition) {
    override fun createRoutine(): Command {
        val switch = switchSide.withEquals(MatchData.OwnedSide.LEFT)
        val mirrored = switchSide.withEquals(MatchData.OwnedSide.RIGHT)

        val drop1stCube = FollowTrajectoryCommand(switch.map(Trajectories.centerStartToLeftSwitch, Trajectories.centerStartToRightSwitch))
        val toCenter = FollowTrajectoryCommand(Trajectories.switchToCenter, mirrored)
        val toPyramid = FollowTrajectoryCommand(Trajectories.centerToPyramid)
        val toCenter2 = FollowTrajectoryCommand(Trajectories.pyramidToCenter)
        val drop2ndCube = FollowTrajectoryCommand(Trajectories.centerToSwitch, mirrored)

        val shoot1stCube = drop1stCube.addMarkerAt(
                Trajectories.kSwitchLeftAdjusted.transformBy(Pose2d.fromTranslation(Translation2d(-0.2, 0.0)))
                        .translation.let { mirrored.map(it.mirror, it) })

        val shoot2ndCube = drop2ndCube.addMarkerAt(
                Trajectories.kSwitchLeftAdjusted.transformBy(Pose2d.fromTranslation(Translation2d(-0.2, 0.0)))
                        .translation.let { mirrored.map(it.mirror, it) })

        shoot1stCube.condition.invokeWhenTrue {
            println("Shoot 1st Cube: $it")
        }

        shoot2ndCube.condition.invokeWhenTrue {
            println("Shoot 2nd Cube: $it")
        }

        return parallel {
            sequential {
                +drop1stCube
                +toCenter
                +toPyramid
                +toCenter2
                +drop2ndCube
            }
            sequential {
                +DelayCommand(250L, TimeUnit.MILLISECONDS)
                +SubsystemPreset.SWITCH.command.withExit(condition(drop1stCube))
                +BlinkingLEDCommand(Color.RED, 200).withTimeout(500L)
                +ConditionCommand(shoot1stCube.condition)
                +IntakeCommand(IntakeSubsystem.Direction.OUT).withTimeout(500L)
                +SubsystemPreset.INTAKE.command.withExit(condition(toCenter))
                +BlinkingLEDCommand(Color.BLUE, 200).withTimeout(500L)
                +ConditionCommand(condition(toCenter))
                +IntakeCommand(IntakeSubsystem.Direction.IN).withExit(condition(toCenter2))
                +SubsystemPreset.SWITCH.command.withExit(condition(drop2ndCube))
                +BlinkingLEDCommand(Color.GREEN, 200).withTimeout(500L)
                +ConditionCommand(shoot2ndCube.condition)
                +IntakeCommand(IntakeSubsystem.Direction.OUT)
            }
        }
    }
}