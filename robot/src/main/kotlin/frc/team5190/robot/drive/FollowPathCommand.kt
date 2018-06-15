package frc.team5190.robot.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.command.Command
import frc.team5190.lib.control.PathFollower
import frc.team5190.lib.units.FeetPerSecond
import frc.team5190.lib.util.Pathreader
import frc.team5190.robot.Kinematics
import frc.team5190.robot.Localization
import frc.team5190.robot.sensors.NavX
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

class FollowPathCommand(folder: String, file: String,
                        robotReversed: Boolean = false,
                        private val pathMirrored: Boolean = false,
                        pathReversed: Boolean = false,
                        private val resetRobotPosition: Boolean = false) : Command() {

    companion object {
        const val DT = 0.02

        var pathX = 0.0
            private set
        var pathY = 0.0
            private set
        var pathHdg = 0.0
            private set

        var lookaheadX = 0.0
            private set
        var lookaheadY = 0.0
            private set
    }

    // Notifier objects
    private val synchronousNotifier = Object()
    private val notifier: Notifier
    private var stopNotifier = false

    // Left, right, and source trajectory
    private val trajectory = Pathreader.getPath(folder, file)

    // Path follower
    private val pathFollower: PathFollower

    init {
        requires(DriveSubsystem)

        // Modify trajectory if reversed or mirrored

        if (pathReversed) {
            val reversedTrajectory = trajectory.copy()
            val distance = reversedTrajectory.segments.last().position

            reversedTrajectory.segments.reverse()
            reversedTrajectory.segments.forEach { it.position = distance - it.position }

            trajectory.segments = reversedTrajectory.segments
        }
        trajectory.segments.forEach { segment ->
            if (pathMirrored) {
                segment.heading = -segment.heading + (2 * Math.PI)
                segment.y = 27 - segment.y
            }
            if (robotReversed xor pathReversed) {
                var newHeading = segment.heading + Math.PI
                if (newHeading > 2 * Math.PI) newHeading -= 2 * Math.PI

                segment.heading = newHeading
            }
        }

        // Initialize path follower
        pathFollower = PathFollower(trajectory = trajectory)

        // Initialize notifier
        notifier = Notifier {
            synchronized(synchronousNotifier) {
                if (stopNotifier) {
                    return@Notifier
                }

                val output = pathFollower.getLinAndAngVelocities(
                        pose = Localization.robotPosition,
                        θ = Math.toRadians(NavX.correctedAngle))

                val adjustedVelocities = Kinematics.inverseKinematics(output)
                DriveSubsystem.set(controlMode = ControlMode.Velocity,
                        leftOutput = FeetPerSecond(adjustedVelocities.first).STU.value.toDouble(),
                        rightOutput = FeetPerSecond(adjustedVelocities.second).STU.value.toDouble())


                pathX = pathFollower.currentSegment.x
                pathY = pathFollower.currentSegment.y
                pathHdg = pathFollower.currentSegment.heading
            }
        }
    }

    override fun initialize() {
        DriveSubsystem.resetEncoders()
        if (resetRobotPosition) {
            Localization.reset(position = Vector2D(trajectory.segments[0].x, trajectory.segments[0].y))
        }
        notifier.startPeriodic(trajectory.segments[0].dt)
    }

    override fun end() {
        synchronized(synchronousNotifier) {
            stopNotifier = true
            notifier.stop()
            DriveSubsystem.set(controlMode = ControlMode.PercentOutput, leftOutput = 0.0, rightOutput = 0.0)
        }
    }

    override fun isFinished() = pathFollower.isFinished
}
