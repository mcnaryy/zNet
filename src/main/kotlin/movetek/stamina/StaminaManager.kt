package net.hellz.movetek.stamina

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.utils.time.TimeUnit

object SprintDetection {

    private val previousSprintingState = mutableMapOf<Player, Boolean>()

    init {
        MinecraftServer.getSchedulerManager().buildTask {
            for (player in MinecraftServer.getConnectionManager().onlinePlayers){
                if (player.isSprinting) {
                    // Decrease food when sprinting
                    if (player.food > 0){
                        player.food -= 5
                    }
                } else {
                    // Increases food when not sprinting
                    if (player.food < 20){
                        // coerceAtMost makes it so that food level doesn't go above 20
                        player.food = (player.food + 3).coerceAtMost(20)
                    }
                }
            }
        }.repeat(20, TimeUnit.SERVER_TICK).schedule()
    }
}