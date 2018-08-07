package frc.team5190.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID

class HIDButtonSource(private val genericHID: GenericHID, private val buttonId: Int) : HIDSource {
    override val value
        get() = if (genericHID.getRawButton(buttonId)) 1.0 else 0.0
}

class HIDAxisSource(private val genericHID: GenericHID, private val axisId: Int) : HIDSource {
    override val value
        get() = genericHID.getRawAxis(axisId)
}

class HIDPOVSource(private val genericHID: GenericHID, private val povId: Int, private val angle: Int) : HIDSource {
    override val value
        get() = if (genericHID.getPOV(povId) == angle) 1.0 else 0.0
}

interface HIDSource {
    val value: Double
}