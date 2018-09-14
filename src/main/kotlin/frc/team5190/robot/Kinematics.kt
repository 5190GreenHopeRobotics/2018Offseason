/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.robot

import frc.team5190.lib.mathematics.epsilonEquals
import frc.team5190.lib.mathematics.twodim.geometry.Twist2d

object Kinematics {

    fun forwardKinematics(leftDelta: Double, rightDelta: Double, rotationDelta: Double): Twist2d {
        val dx = (leftDelta + rightDelta) / 2.0
        return Twist2d(dx = dx, dy = 0.0, dtheta = rotationDelta)
    }

    fun inverseKinematics(velocity: Twist2d): Pair<Double, Double> {
        if (velocity.dtheta epsilonEquals 0.0) {
            return velocity.dx to velocity.dx
        }

        val deltaV = Constants.kTrackWidth * velocity.dtheta / 2
        return velocity.dx - deltaV to velocity.dx + deltaV
    }
}
