/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.robot.subsytems.arm

import com.ctre.phoenix.motorcontrol.ControlMode
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.utils.DoubleSource
import org.ghrobotics.lib.utils.Source

class OpenLoopArmCommand(private val percentOutput: DoubleSource) : FalconCommand(ArmSubsystem) {

    constructor(percentOutput: Double) : this(Source(percentOutput))

    override suspend fun execute() = ArmSubsystem.set(ControlMode.PercentOutput, percentOutput())
}