/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.robot

import frc.team5190.lib.wrappers.FalconRobotBase
import frc.team5190.robot.auto.Autonomous
import frc.team5190.robot.sensors.AHRS
import frc.team5190.robot.sensors.AHRSSensorType
import frc.team5190.robot.sensors.Lidar
import frc.team5190.robot.sensors.ahrsSensorType
import frc.team5190.robot.subsytems.arm.ArmSubsystem
import frc.team5190.robot.subsytems.climber.ClimberSubsystem
import frc.team5190.robot.subsytems.drive.DriveSubsystem
import frc.team5190.robot.subsytems.elevator.ElevatorSubsystem
import frc.team5190.robot.subsytems.intake.IntakeSubsystem
import frc.team5190.robot.subsytems.led.LEDSubsystem

class Robot : FalconRobotBase() {

    // Initialize all systems.
    override suspend fun initialize() {
        ahrsSensorType = AHRSSensorType.Pigeon

        +Controls.mainXbox

        +DriveSubsystem
        +ArmSubsystem
        +ElevatorSubsystem
        +ClimberSubsystem
        +IntakeSubsystem
        +LEDSubsystem

        Localization
        NetworkInterface
        Autonomous
        AHRS
        Lidar

        onTransition(Mode.ANY, Mode.ANY) { from, to ->
            println("Transitioned from $from to $to")
        }
    }

}