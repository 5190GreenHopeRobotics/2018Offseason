package frc.team5190.lib

import com.ctre.phoenix.CANifier
import edu.wpi.first.wpilibj.command.CommandGroup
import frc.team5190.lib.units.Milliseconds
import frc.team5190.lib.units.Time
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.awt.Color
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

fun CANifier.setLEDOutput(color: Color) = setLEDOutput(color.red, color.green, color.blue)

fun CANifier.setLEDOutput(r: Int, g: Int, b: Int) {
    setLEDOutput(r * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelB)
    setLEDOutput(g * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelA)
    setLEDOutput(b * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelC)
}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest(toByteArray())
    return digested.joinToString("") {
        String.format("%02x", it)
    }
}

fun commandGroup(create: CommandGroup.() -> Unit): CommandGroup {
    val group = CommandGroup()
    create.invoke(group)
    return group
}


