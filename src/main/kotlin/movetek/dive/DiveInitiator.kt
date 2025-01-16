package net.hellz.movetek.dive

import net.minestom.server.coordinate.Vec
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerMoveEvent


object DiveInitiator {

    private val previousSneakingState = mutableMapOf<Player, Boolean>()
    private val lastSneakTime = mutableMapOf<Player, Long>()
    private val doubleSneakWindow = 500L
    private val proneThresholdPitch = 10.0
    private val momentumThreshold = 0.1

    init {

        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent::class.java) { event ->
            val player = event.player
            val isSneakingNow = player.isSneaking
            val previousSneakState = previousSneakingState[player] ?: false
            val isSprintingNow = player.isSprinting
            val isLookingStraightAhead = player.position.pitch in -proneThresholdPitch..proneThresholdPitch
        }
    }

}