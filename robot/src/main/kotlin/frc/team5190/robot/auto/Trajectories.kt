/*
 * FRC Team 5190
 * Green Hope Falcons
 */


package frc.team5190.robot.auto

import frc.team5190.lib.geometry.Pose2d
import frc.team5190.lib.geometry.Pose2dWithCurvature
import frc.team5190.lib.geometry.Rotation2d
import frc.team5190.lib.geometry.Translation2d
import frc.team5190.lib.trajectory.Trajectory
import frc.team5190.lib.trajectory.TrajectoryGenerator
import frc.team5190.lib.trajectory.timing.CentripetalAccelerationConstraint
import frc.team5190.lib.trajectory.timing.TimedState
import frc.team5190.lib.trajectory.timing.TimingConstraint
import kotlinx.coroutines.experimental.*

object Trajectories {

    // Constants in Feet Per Second
    private const val kMaxVelocity = 11.0
    private const val kMaxAcceleration = 6.0


    // Constraints
    private val kConstraints =
            mutableListOf(
                    CentripetalAccelerationConstraint(6.0))


    // Robot Constants
    private const val kRobotWidth = 27.0 / 12.0
    private const val kRobotLength = 33.0 / 12.0
    private const val kIntakeLength = 16.0 / 12.0
    private const val kBumperLength = 02.0 / 12.0

    private val kCenterToIntake = Pose2d(Translation2d(-(kRobotLength / 2.0) - kIntakeLength, 0.0), Rotation2d())
    private val kCenterToFrontBumper = Pose2d(Translation2d(-(kRobotLength / 2.0) - kBumperLength, 0.0), Rotation2d())


    // Field Relative Constants
    private const val kExchangeZone = 14.6

    internal val kSideStart = Pose2d(Translation2d((kRobotLength / 2.0) + kBumperLength, 23.5), Rotation2d.fromDegrees(180.0))
    internal val kCenterStart = Pose2d(Translation2d((kRobotLength / 2.0) + kBumperLength,
            kExchangeZone - (kRobotWidth / 2.0) - kBumperLength), Rotation2d())

    private val kNearScaleEmpty = Pose2d(Translation2d(22.7, 20.50), Rotation2d.fromDegrees(170.0))
    private val kNearScaleFull = Pose2d(Translation2d(22.7, 20.00), Rotation2d.fromDegrees(165.0))

    private val kFarScaleEmpty = Pose2d(Translation2d(22.7, 06.50), Rotation2d.fromDegrees(190.0))

    private val kNearCube1 = Pose2d(Translation2d(16.5, 19.5), Rotation2d.fromDegrees(190.0))
    private val kNearCube2 = Pose2d(Translation2d(16.5, 17.0), Rotation2d.fromDegrees(245.0))
    private val kNearCube3 = Pose2d(Translation2d(16.5, 14.5), Rotation2d.fromDegrees(240.0))

    private val kNearCube1Adjusted = kNearCube1.transformBy(kCenterToIntake)
    private val kNearCube2Adjusted = kNearCube2.transformBy(kCenterToIntake)
    private val kNearCube3Adjusted = kNearCube3.transformBy(kCenterToIntake)

    private val kSwitchLeft = Pose2d(Translation2d(11.5, 18.8), Rotation2d())
    private val kSwitchRight = Pose2d(Translation2d(11.5, 08.2), Rotation2d())

    private val kSwitchLeftAdjusted = kSwitchLeft.transformBy(kCenterToFrontBumper)
    private val kSwitchRightAdjusted = kSwitchRight.transformBy(kCenterToFrontBumper)

    private val kFrontPyramidCube = Pose2d(Translation2d(9.0, 13.5), Rotation2d())
    private val kFrontPyramidCubeAdjusted = kFrontPyramidCube.transformBy(kCenterToIntake)


    // Hash Map
    private val trajectories = hashMapOf<String, Deferred<Trajectory<TimedState<Pose2dWithCurvature>>>>()

    init {

        /* SOME TRAJECTORIES MAY NOT APPEAR HERE. THEY CAN BE OBTAINED BY MIRRORING
           TRAJECTORYUTIL.MIRRORTIMED(TRAJECTORY)
         */

        // Left Start to Near Scale
        mutableListOf(
                kSideStart,
                kSideStart.transformBy(Pose2d.fromTranslation(Translation2d(-10.0, 0.0))), kNearScaleEmpty
        ).also { generateTrajectory("Left Start to Near Scale", true, it) }


        // Left Start to Far Scale
        mutableListOf(
                kSideStart,
                kSideStart.transformBy(Pose2d(Translation2d(-13.0, 00.0), Rotation2d())),
                kSideStart.transformBy(Pose2d(Translation2d(-18.3, 05.0), Rotation2d.fromDegrees(-90.0))),
                kSideStart.transformBy(Pose2d(Translation2d(-18.3, 15.0), Rotation2d.fromDegrees(-90.0))),
                kFarScaleEmpty
        ).also { generateTrajectory("Left Start to Far Scale", true, it) }

        // Near Scale to Cube 1
        mutableListOf(
                kNearScaleEmpty,
                kNearCube1Adjusted
        ).also { generateTrajectory("Near Scale to Cube 1", false, it) }

        // Cube 1 to Near Scale
        mutableListOf(
                kNearCube1Adjusted,
                kNearScaleFull
        ).also { generateTrajectory("Cube 1 to Near Scale", true, it) }

        // Near Scale to Cube 2
        mutableListOf(
                kNearScaleFull,
                kNearCube2Adjusted
        ).also { generateTrajectory("Near Scale to Cube 2", false, it) }

        // Cube 2 to Near Scale
        mutableListOf(
                kNearCube2Adjusted,
                kNearScaleFull
        ).also { generateTrajectory("Cube 2 to Near Scale", true, it) }

        // Near Scale to Cube 3
        mutableListOf(
                kNearScaleFull,
                kNearCube3Adjusted
        ).also { generateTrajectory("Near Scale to Cube 3", false, it) }


        // Cube 3 to Near Scale
        mutableListOf(
                kNearCube3Adjusted,
                kNearScaleFull
        ).also { generateTrajectory("Cube 3 to Near Scale", true, it) }

        // Center Start to Left Switch
        mutableListOf(
                kCenterStart,
                kSwitchLeftAdjusted
        ).also { generateTrajectory("Center Start to Left Switch", false, it) }

        // Center Start to Right Switch
        mutableListOf(
                kCenterStart,
                kSwitchRightAdjusted
        ).also { generateTrajectory("Center Start to Right Switch", false, it) }

        // Left Switch to Center
        mutableListOf(
                kSwitchLeftAdjusted,
                kFrontPyramidCubeAdjusted.transformBy(Pose2d.fromTranslation(Translation2d(-5.0, 0.0)))
        ).also { generateTrajectory("Left Switch to Center", true, it) }

        // Center to Pyramid
        mutableListOf(
                kFrontPyramidCubeAdjusted.transformBy(Pose2d.fromTranslation(Translation2d(-5.0, 0.0))),
                kFrontPyramidCubeAdjusted
        ).also { generateTrajectory("Center to Pyramid", false, it) }

        // Pyramid to Center
        mutableListOf(
                kFrontPyramidCubeAdjusted,
                kFrontPyramidCubeAdjusted.transformBy(Pose2d.fromTranslation(Translation2d(-5.0, 0.0)))
        ).also { generateTrajectory("Pyramid to Center", true, it) }

        // Center to Left Switch
        mutableListOf(
                kFrontPyramidCubeAdjusted.transformBy(Pose2d.fromTranslation(Translation2d(-5.0, 0.0))),
                kSwitchLeftAdjusted
        ).also { generateTrajectory("Center to Left Switch", false, it) }

        // Baseline
        mutableListOf(
                kSideStart,
                kSideStart.transformBy(Pose2d(Translation2d(-10.0, 0.0), Rotation2d()))
        ).also { generateTrajectory("Baseline", true, it) }


        launch {
            val startTime = System.currentTimeMillis()
            trajectories.values.awaitAll()
            val elapsedTime = System.currentTimeMillis() - startTime

            println("Asynchronous Trajectory Generation of ${trajectories.size} trajectories took $elapsedTime milliseconds.")
        }
    }

    operator fun get(identifier: String): Trajectory<TimedState<Pose2dWithCurvature>> = runBlocking {
        trajectories[identifier]?.await()!!
    }

    private fun generateTrajectory(name: String,
                                   reversed: Boolean,
                                   waypoints: MutableList<Pose2d>,
                                   maxVelocity: Double = kMaxVelocity,
                                   maxAcceleration: Double = kMaxAcceleration,
                                   constraints: List<TimingConstraint<Pose2dWithCurvature>> = kConstraints
    ) {
        trajectories[name] = async {
            TrajectoryGenerator.generateTrajectory(reversed, waypoints, constraints, 0.0, 0.0, maxVelocity, maxAcceleration)!!
        }
    }
}