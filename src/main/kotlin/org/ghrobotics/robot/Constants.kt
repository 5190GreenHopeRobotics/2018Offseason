/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.robot

/* ktlint-disable no-wildcard-imports */
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.nativeunits.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Constants {

    // GLOBAL CTRE TIMEOUT
    const val kCTRETimeout = 10

    // MOTOR IDS
    const val kLeftMasterId = 1
    const val kLeftSlaveId1 = 2

    const val kRightMasterId = 3
    const val kRightSlaveId1 = 4

    const val kElevatorMasterId = 5
    const val kElevatorSlaveId = 6

    const val kIntakeMasterId = 7
    const val kIntakeSlaveId = 9

    const val kArmId = 8

    const val kWinchMasterId = 10
    const val kWinchSlaveId = 59

    // CANIFIER
    const val kCANifierId = 16

    // FLOW SENSOR
    const val kFlowSensorI2CId = 0
    const val kFlowSensorTicksPerInch = 100 // TODO Find Actual Value

    // PNEUMATICS
    const val kPCMId = 41
    const val kDriveSolenoidId = 3
    const val kIntakeSolenoidId = 2

    // SERVO
    const val kLidarServoId = 0

    // ANALOG INPUT
    const val kLeftCubeSensorId = 2
    const val kRightCubeSensorId = 3

    // ROBOT
    val kRobotWidth = 27.inch
    val kRobotLength = 33.inch
    val kIntakeLength = 16.0.inch
    val kBumperLength = 2.0.inch

    val kRobotStartX = (kRobotLength / 2.0) + kBumperLength

    val kExchangeZoneBottomY = 14.5.feet
    val kPortalZoneBottomY = (27 - (29.69 / 12.0)).feet

    val kRobotSideStartY = kPortalZoneBottomY - (kRobotWidth / 2.0) - kBumperLength
    val kRobotCenterStartY = kExchangeZoneBottomY - (kRobotWidth / 2.0) - kBumperLength

    const val kRobotMass = 54.53 /* Robot */ + 5.669 /* Battery */ + 7 /* Bumpers */ // kg
    const val kRobotMomentOfInertia = 10.0 // kg m^2 // TODO Tune
    const val kRobotAngularDrag = 12.0 // N*m / (rad/sec)

    // MECHANISM TRANSFORMATIONS
    val kFrontToIntake = Pose2d(-kIntakeLength, 0.meter, 0.degree)
    val kCenterToIntake = Pose2d(-(kRobotLength / 2.0) - kIntakeLength, 0.meter, 0.degree)
    val kCenterToFrontBumper = Pose2d(-(kRobotLength / 2.0) - kBumperLength, 0.meter, 0.degree)

    // DRIVE
    val kDriveSensorUnitsPerRotation = 1440.STU
    val kWheelRadius = 2.92.inch
    val kTrackWidth = 0.634.meter

    val kDriveNativeUnitModel = NativeUnitLengthModel(
            kDriveSensorUnitsPerRotation,
            kWheelRadius
    )

    const val kPDrive = 1.7 // Talon SRX Units
    const val kDDrive = 2.0

    const val kStaticFrictionVoltage = 1.8 // Volts
    const val kVDrive = 0.115 // Volts per radians per second
    const val kADrive = 0.0716 // Volts per radians per second per second

    const val kDriveBeta = 1.4 // Inverse meters squared
    const val kDriveZeta = 0.9 // Unitless dampening co-efficient

    // ARM
    val kArmSensorUnitsPerRotation = 1024.STU
    val kArmNativeUnitModel = NativeUnitRotationModel(kArmSensorUnitsPerRotation)

    val kArmDownPosition = (-280).degree
    val kArmMiddlePosition = kArmDownPosition + 14.degree
    val kArmUpPosition = kArmDownPosition + 70.degree
    val kArmAllUpPosition = kArmDownPosition + 88.degree
    val kArmBehindPosition = kArmDownPosition + 134.degree

    const val kPArm = 4.5
    const val kVArm = 16.78 + 0.9 // 1023 units per STU (velocity)

    val kArmMotionMagicVelocity = 1000000.degree / 1.second
    val kArmMotionMagicAcceleration = 140.degree / 1.second / 1.second

    val kArmClosedLoopTolerance = 14.degree
    val kArmAutoTolerance = 35.degree

    // ELEVATOR
    const val kPElevator = 0.3
    const val kVElevator = 0.395 // 1023 units per STU (velocity)

    val elevatorNativeUnitSettings = NativeUnitLengthModel(
            1440.STU,
            1.25.inch / 2.0
    )

    val kElevatorSoftLimitFwd = 22500.STU
    val kElevatorClosedLpTolerance = 1.inch

    val kElevatorMotionMagicVelocity = 72.inch.velocity
    val kElevatorMotionMagicAcceleration = 90.inch.acceleration

    // CLIMBER
    const val kPClimber = 2.0

    val kClimberClosedLpTolerance = 1000.STU

    val kClimberMotionMagicVelocity = 1000000.STUPer100ms
    val kClimberMotionMagicAcceleration = 12000.STUPer100msPerSecond

    val kClimberHighScalePosition = 47600.STU
    val kClimberBottomPosition = 0.STU
}
