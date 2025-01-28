package net.hellz.movetek.dive

import net.hellz.movetek.prone.ProneManager
import net.hellz.movetek.prone.ProneManager.initiateProne
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerStartSneakingEvent
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import java.util.concurrent.ConcurrentHashMap

object DiveInitiator {

    private const val DOUBLE_SNEAK_THRESHOLD_MS = 500 // Time in milliseconds
    private val lastSneakTimestamps = ConcurrentHashMap<String, Long>()

    init {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerStartSneakingEvent::class.java) { event ->
            val player = event.player
            val currentTime = System.currentTimeMillis()

            val lastSneakTime = DiveInitiator.lastSneakTimestamps[player.uuid.toString()]

            if (lastSneakTime != null && currentTime - lastSneakTime <= DiveInitiator.DOUBLE_SNEAK_THRESHOLD_MS) {
                val isLookingDown = player.position.pitch >= 60.0

                if (player.isSprinting) {
                    if (!player.isOnGround) {
                        initiateDive(player)
                    }
                }

                // Reset the timestamp to avoid triggering prone repeatedly
                DiveInitiator.lastSneakTimestamps.remove(player.uuid.toString())
            } else {
                // Log the current time as the last sneak time
                DiveInitiator.lastSneakTimestamps[player.uuid.toString()] = currentTime
            }
        }
    }
    private fun initiateDive(player: Player) {

        val direction = player.position.direction()

        // Horizontal speed for the dive
        val horizontalSpeed = 20.0  // How fast the player moves forward

        // Apply initial horizontal velocity (forward motion) with no vertical velocity
        val velocity = Vec(direction.x() * horizontalSpeed, 0.0, direction.z() * horizontalSpeed)
        player.velocity = velocity

        // Set the player's pose to FALL_FLYING (gliding) to simulate the dive animation
        player.entityMeta.isFlyingWithElytra = true

        // Notify the player (optional)
        downwardGravity(player)
    }

    private fun downwardGravity(player: Player) {
        var task: Task? = null
        task = MinecraftServer.getSchedulerManager().buildTask {
            if (!player.isOnGround) {
                val downwardPull = -2.5  // Negative vertical speed to simulate gravity pulling the player down
                player.velocity = Vec(player.velocity.x, downwardPull, player.velocity.z)
            } else {
                initiateProne(player)
                task?.cancel()
            }
        }.repeat(TaskSchedule.tick(1)).schedule()
    }
}