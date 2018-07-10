@file:Suppress("unused")

package frc.team5190.lib.trajectory.timing

import frc.team5190.lib.geometry.Translation2d
import frc.team5190.lib.geometry.interfaces.ITranslation2d

class VelocityLimitRegionConstraint<S : ITranslation2d<S>>(
        private val minCorner: Translation2d,
        private val maxCorner: Translation2d,
        private val velocityLimit: Double) : TimingConstraint<S> {

    override fun getMaxVelocity(state: S): Double {
        val translation = state.translation
        return if (translation.x <= maxCorner.x && translation.x >= minCorner.x &&
                translation.y <= maxCorner.y && translation.y >= minCorner.y) {
            velocityLimit
        } else java.lang.Double.POSITIVE_INFINITY
    }

    override fun getMinMaxAcceleration(state: S, velocity: Double): TimingConstraint.MinMaxAcceleration {
        return TimingConstraint.MinMaxAcceleration.kNoLimits
    }

}
