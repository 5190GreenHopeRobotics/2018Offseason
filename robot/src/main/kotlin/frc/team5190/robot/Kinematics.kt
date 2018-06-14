package frc.team5190.robot

object Kinematics {

    fun inverseKinematics(vel: Pair<Double, Double>): Pair<Double, Double> {

        val v = vel.first
        val w = vel.second

        val diff = (DriveConstants.TRACK_WIDTH * w)

        val leftRadS = ((2 * v) - diff) / (DriveConstants.WHEEL_RADIUS / 6.0)
        val rightRadS = ((2 * v) + diff) / (DriveConstants.WHEEL_RADIUS / 6.0)

        fun convertToFPS(value: Double) = value * (DriveConstants.WHEEL_RADIUS / 12.0)

        return convertToFPS(leftRadS) to convertToFPS(rightRadS)
    }
}