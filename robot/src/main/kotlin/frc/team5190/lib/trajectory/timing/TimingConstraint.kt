package frc.team5190.lib.trajectory.timing

import frc.team5190.lib.geometry.interfaces.State

interface TimingConstraint<S : State<S>> {

    fun getMaxVelocity(state: S): Double

    fun getMinMaxAcceleration(state: S, velocity: Double): MinMaxAcceleration

    class MinMaxAcceleration {
        val minAcceleration: Double
        val maxAcceleration: Double

        val valid
            get() = minAcceleration <= maxAcceleration

        constructor() {
            // No limits.
            minAcceleration = Double.NEGATIVE_INFINITY
            maxAcceleration = Double.POSITIVE_INFINITY
        }

        @Suppress("unused")
        constructor(min_acceleration: Double, max_acceleration: Double) {
            minAcceleration = min_acceleration
            maxAcceleration = max_acceleration
        }


        companion object {
            var NO_LIMITS = MinMaxAcceleration()
        }
    }
}
