package frc.team5190.robot

import edu.wpi.first.wpilibj.command.CommandGroup
import frc.team5190.lib.S3ND
import frc.team5190.lib.commandGroup
import frc.team5190.robot.drive.FollowPathCommand
import frc.team5190.robot.sensors.NavX
import kotlinx.coroutines.experimental.launch
import openrio.powerup.MatchData
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

object Autonomous {

    // Switch side and scale side variables
    private var switchSide = MatchData.OwnedSide.UNKNOWN
    private var scaleSide = MatchData.OwnedSide.UNKNOWN


    // Starting position
    private var startingPosition = StartingPositions.CENTER

    // Contains folder in which paths are located
    private var folder = ""

    // Is FMS Data valid
    private val fmsDataValid
        get() = switchSide != MatchData.OwnedSide.UNKNOWN && scaleSide != MatchData.OwnedSide.UNKNOWN

    // Random variable that make infix functions work
    private const val IT = ""

    init {
        // Poll for FMS Data
        launch {
            @Suppress("LocalVariableName")
            var JUST = commandGroup { }

            while (!(Robot.INSTANCE.isAutonomous &&
                            Robot.INSTANCE.isEnabled &&
                            fmsDataValid &&
                            PathGenerator.pathsGenerated)) {

                val networkStartingPosition = StartingPositions.valueOf(NetworkInterface.startingPosition.getString("Left").toUpperCase())

                if (networkStartingPosition != startingPosition ||
                        MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) != switchSide ||
                        MatchData.getOwnedSide(MatchData.GameFeature.SCALE) != scaleSide) {

                    switchSide = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR)
                    scaleSide = MatchData.getOwnedSide(MatchData.GameFeature.SCALE)
                    startingPosition = networkStartingPosition

                    folder = if (startingPosition.name.first().toUpperCase() == scaleSide.name.first().toUpperCase()) "LS-LL" else "LS-RR"

                    Localization.reset(position = startingPosition.relativePos)

                    JUST = getAutoCommand()
                }
            }
            JUST S3ND IT
        }
    }

    private fun getAutoCommand(): CommandGroup {
        NavX.reset()
        NavX.angleOffset = 0.0

        NetworkInterface.ntInstance.getEntry("Reset").setBoolean(true)

        return commandGroup {
            addSequential(FollowPathCommand("Angled Test",
                    robotReversed = false,
                    resetRobotPosition = true,
                    pathMirrored = startingPosition == StartingPositions.RIGHT))
        }
    }
}

enum class StartingPositions(val relativePos: Vector2D) {
    LEFT(Vector2D(1.75, 23.5)), CENTER(Vector2D(1.75, 13.25)), RIGHT(Vector2D(1.75, 3.5))
}