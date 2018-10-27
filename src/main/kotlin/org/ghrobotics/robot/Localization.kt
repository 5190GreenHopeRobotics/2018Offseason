/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.robot

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.utils.launchFrequency
import org.ghrobotics.robot.sensors.AHRS
import org.ghrobotics.robot.subsytems.drive.DriveSubsystem

object Localization {

    private val localizationScope = CoroutineScope(newSingleThreadContext("Localization"))
    private val localizationMutex = Mutex()

    var robotPosition = Pose2d()
        private set

    private var prevL = 0.0
    private var prevR = 0.0
    private var prevA = 0.0

    init {
        runBlocking { reset() }
        localizationScope.launchFrequency(100) { run() }
    }

    suspend fun reset(pose: Pose2d = Pose2d()) = localizationMutex.withLock {
        robotPosition = pose
        prevL = DriveSubsystem.leftPosition.meter.asDouble
        prevR = DriveSubsystem.rightPosition.meter.asDouble
        prevA = AHRS.correctedAngle.radian.asDouble
    }

    private suspend fun run() = localizationMutex.withLock {
        val posL = DriveSubsystem.leftPosition.meter.asDouble
        val posR = DriveSubsystem.rightPosition.meter.asDouble

        val angA = AHRS.correctedAngle.radian.asDouble

        val deltaL = posL - prevL
        val deltaR = posR - prevR
        val deltaA = angA - prevA

        robotPosition += forwardKinematics(deltaL, deltaR, deltaA).asPose

        prevL = posL
        prevR = posR
        prevA = angA
    }

    private fun forwardKinematics(leftDelta: Double, rightDelta: Double, rotationDelta: Double): Twist2d {
        val dx = (leftDelta + rightDelta) / 2.0
        return Twist2d(dx, 0.0, rotationDelta)
    }

}