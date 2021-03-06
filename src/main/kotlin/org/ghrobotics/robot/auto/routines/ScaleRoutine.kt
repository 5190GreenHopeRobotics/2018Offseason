package org.ghrobotics.robot.auto.routines

/* ktlint-disable no-wildcard-imports */
import openrio.powerup.MatchData
import org.ghrobotics.lib.commands.*
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.map
import org.ghrobotics.lib.utils.not
import org.ghrobotics.lib.utils.withEquals
import org.ghrobotics.lib.utils.withMerge
import org.ghrobotics.robot.Constants
import org.ghrobotics.robot.auto.Autonomous
import org.ghrobotics.robot.auto.Autonomous.Config.scaleSide
import org.ghrobotics.robot.auto.Autonomous.Config.startingPosition
import org.ghrobotics.robot.auto.StartingPositions
import org.ghrobotics.robot.auto.Trajectories
import org.ghrobotics.robot.auto.Trajectories.cube1ToScale
import org.ghrobotics.robot.auto.Trajectories.cube2ToScale
import org.ghrobotics.robot.auto.Trajectories.leftStartToFarScale
import org.ghrobotics.robot.auto.Trajectories.leftStartToNearScale
import org.ghrobotics.robot.sensors.CubeSensors
import org.ghrobotics.robot.subsytems.SubsystemPreset
import org.ghrobotics.robot.subsytems.arm.ArmSubsystem
import org.ghrobotics.robot.subsytems.arm.ClosedLoopArmCommand
import org.ghrobotics.robot.subsytems.drive.DriveSubsystem
import org.ghrobotics.robot.subsytems.elevator.ClosedLoopElevatorCommand
import org.ghrobotics.robot.subsytems.elevator.ElevatorSubsystem
import org.ghrobotics.robot.subsytems.intake.IntakeCommand
import org.ghrobotics.robot.subsytems.intake.IntakeSubsystem

fun scaleRoutine() = autoRoutine {
    val shouldMirrorPath = scaleSide.withEquals(MatchData.OwnedSide.RIGHT)

    val stopScalePathCondition = {
        (ElevatorSubsystem.elevatorPosition > ElevatorSubsystem.kFirstStagePosition &&
            !CubeSensors.cubeIn() &&
            ArmSubsystem.armPosition > Constants.kArmBehindPosition - Constants.kArmAutoTolerance)
    }

    val firstCubePath = Autonomous.isSameSide.map(leftStartToNearScale, leftStartToFarScale)

    +sequential {
        // Place first cube in scale
        +parallel {
            +DriveSubsystem.followTrajectory(firstCubePath, startingPosition.withEquals(StartingPositions.RIGHT))
                .withExit(stopScalePathCondition)
            +sequential {
                // Start moving the arm and elevator up
                +DelayCommand(500.millisecond)

                +parallel {
                    +ClosedLoopArmCommand(Constants.kArmUpPosition)
                    +ClosedLoopElevatorCommand(ElevatorSubsystem.kFirstStagePosition)
                }

                +sequential {
                    // Finish moving arm and elevator once we are near scale
                    +DelayCommand(Autonomous.isSameSide.map(2.75.second, 2.50.second)
                        .withMerge(firstCubePath) { offset, path -> path.lastState.t - offset })
                    +sequential {
                        // Launch cube once arm is far enough back
                        +ConditionCommand { ArmSubsystem.armPosition > Constants.kArmBehindPosition - Constants.kArmAutoTolerance }
                        +ConditionalCommand(!Autonomous.isSameSide, DelayCommand(0.1.second))
                        +DelayCommand(100.millisecond)
                        +IntakeCommand(
                            IntakeSubsystem.Direction.OUT,
                            Autonomous.isSameSide.map(0.35, 0.65)
                        ).withTimeout(500.millisecond)
                    }
                }
            }
        }
        // Pick up the second cube
        +parallel {
            +SubsystemPreset.INTAKE.command
            +IntakeCommand(IntakeSubsystem.Direction.IN).withTimeout(10.second)
            +sequential {
                +DelayCommand(300.millisecond)
                +DriveSubsystem.followTrajectory(Trajectories.scaleToCube1, shouldMirrorPath)
                    .withExit(CubeSensors.cubeIn)
            }
        }
        // Place second cube in scale
        +parallel {
            +DriveSubsystem.followTrajectory(cube1ToScale, shouldMirrorPath)
                .withExit(stopScalePathCondition)
            +sequential {
                +DelayCommand(cube1ToScale.lastState.t - 2.7.second)
                +SubsystemPreset.BEHIND.command
            }
            +sequential {
                +ConditionCommand { ArmSubsystem.armPosition > Constants.kArmBehindPosition - Constants.kArmAutoTolerance }
                +IntakeCommand(IntakeSubsystem.Direction.OUT, 0.4).withTimeout(500.millisecond)
            }
        }
        // Pick up the third cube
        +parallel {
            +SubsystemPreset.INTAKE.command
            +IntakeCommand(IntakeSubsystem.Direction.IN).withTimeout(10.second)
            +DriveSubsystem.followTrajectory(
                Trajectories.scaleToCube2,
                shouldMirrorPath
            ).withExit(CubeSensors.cubeIn)
        }
        // Place third cube in scale
        +parallel {
            +DriveSubsystem.followTrajectory(cube2ToScale, shouldMirrorPath)
                .withExit(stopScalePathCondition)
            +sequential {
                +DelayCommand(cube2ToScale.lastState.t - 2.7.second)
                +SubsystemPreset.BEHIND.command
            }
            +sequential {
                +ConditionCommand { ArmSubsystem.armPosition > Constants.kArmBehindPosition - Constants.kArmAutoTolerance }
                +IntakeCommand(IntakeSubsystem.Direction.OUT, 0.4).withTimeout(500.millisecond)
            }
        }
        // Pick up the fourth cube
        +parallel {
            +SubsystemPreset.INTAKE.command
            +IntakeCommand(IntakeSubsystem.Direction.IN).withTimeout(10.second)
            +DriveSubsystem.followTrajectory(Trajectories.scaleToCube3, shouldMirrorPath)
                .withExit(CubeSensors.cubeIn)
        }
    }
}