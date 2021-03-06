package org.ghrobotics.robot.auto.routines

import openrio.powerup.MatchData
import org.ghrobotics.lib.commands.DelayCommand
import org.ghrobotics.lib.commands.parallel
import org.ghrobotics.lib.commands.sequential
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map
import org.ghrobotics.lib.utils.withEquals
import org.ghrobotics.robot.auto.Autonomous.Config.switchSide
import org.ghrobotics.robot.auto.Trajectories.centerStartToLeftSwitch
import org.ghrobotics.robot.auto.Trajectories.centerStartToRightSwitch
import org.ghrobotics.robot.auto.Trajectories.centerToPyramid
import org.ghrobotics.robot.auto.Trajectories.centerToSwitch
import org.ghrobotics.robot.auto.Trajectories.pyramidToCenter
import org.ghrobotics.robot.auto.Trajectories.switchToCenter
import org.ghrobotics.robot.subsytems.SubsystemPreset
import org.ghrobotics.robot.subsytems.drive.DriveSubsystem
import org.ghrobotics.robot.subsytems.intake.IntakeCommand
import org.ghrobotics.robot.subsytems.intake.IntakeSubsystem

fun switchRoutine() = autoRoutine {
    val isLeftSwitch = switchSide.withEquals(MatchData.OwnedSide.LEFT)
    val mirrored = switchSide.withEquals(MatchData.OwnedSide.RIGHT)

    val firstCubePath = isLeftSwitch.map(centerStartToLeftSwitch, centerStartToRightSwitch)

    +sequential {
        +parallel {
            +DriveSubsystem.followTrajectory(firstCubePath)
            +SubsystemPreset.SWITCH.command
            +sequential {
                +DelayCommand(firstCubePath.map { (it.lastState.t - 0.2.second) })
                +IntakeCommand(IntakeSubsystem.Direction.OUT, 0.5).withTimeout(200.millisecond)
            }
        }
        +parallel {
            +DriveSubsystem.followTrajectory(switchToCenter, mirrored)
            +sequential {
                +DelayCommand(500.millisecond)
                +SubsystemPreset.INTAKE.command
            }
        }
        +parallel {
            +DriveSubsystem.followTrajectory(centerToPyramid)
            +IntakeCommand(IntakeSubsystem.Direction.IN).withTimeout(3L.second)
        }
        +DriveSubsystem.followTrajectory(pyramidToCenter)
        +parallel {
            +DriveSubsystem.followTrajectory(centerToSwitch, mirrored)
            +SubsystemPreset.SWITCH.command
            +sequential {
                +DelayCommand(centerToSwitch.lastState.t - 0.2.second)
                +IntakeCommand(IntakeSubsystem.Direction.OUT, Source(0.5)).withTimeout(200.millisecond)
            }
        }
    }
}