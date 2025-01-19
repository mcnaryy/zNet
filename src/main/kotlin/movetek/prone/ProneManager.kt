package net.hellz.movetek.prone

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityPose
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartSneakingEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.TaskSchedule
import java.util.concurrent.ConcurrentHashMap

object ProneManager {
    private const val DOUBLE_SNEAK_THRESHOLD_MS = 500 // Time in milliseconds
    private val lastSneakTimestamps = ConcurrentHashMap<String, Long>()

    var isProne = false

    init {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerStartSneakingEvent::class.java) { event ->
            val player = event.player
            val currentTime = System.currentTimeMillis()

            val lastSneakTime = lastSneakTimestamps[player.uuid.toString()]

            if (lastSneakTime != null && currentTime - lastSneakTime <= DOUBLE_SNEAK_THRESHOLD_MS) {
                val isLookingDown = player.position.pitch >= 60.0

                if (isLookingDown) {
                    player.sendMessage(Component.text("You are now in a prone position!", NamedTextColor.GOLD))

                    // Start managing the barrier above the player
                    isProne = true
                    manageBarrierAbovePlayer(player)
                    forceSwimmingPose(player)
                }

                // Reset the timestamp to avoid triggering prone repeatedly
                lastSneakTimestamps.remove(player.uuid.toString())
            } else {
                // Log the current time as the last sneak time
                lastSneakTimestamps[player.uuid.toString()] = currentTime
            }
        }

        // Listener for player movement to detect jumps and exit prone state
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent::class.java) { event ->
            val from = event.player.previousPosition
            val to = event.player.position

            // Check if the player has jumped (moved upwards) and is in prone state
            if (to.y > from.y && isProne) {
                // Check if there's a barrier above the player
                val barrierPosition = to.add(0.0, 1.0, 0.0) // Position above the player
                val blockAbove = event.player.instance?.getBlock(barrierPosition)

                // If it's a barrier, remove it
                if (blockAbove?.compare(Block.BARRIER) == true) {
                    event.player.instance?.setBlock(barrierPosition, Block.AIR)
                }

                // Reset the prone state flag
                isProne = false

                // Send message indicating the player has exited prone
                event.player.sendMessage(Component.text("You have exited the prone position.", NamedTextColor.RED))
            }
        }
    }

    private var lastPosition: Pos? = null
    private var lastBarrierPosition: Pos? = null

    public fun manageBarrierAbovePlayer(player: net.minestom.server.entity.Player) {
        MinecraftServer.getSchedulerManager().buildTask {
            if (isProne) {
                val currentPosition = player.position
                val barrierPosition = currentPosition.add(0.0, 1.0, 0.0)

                // Only update when the player's position has changed
                if (lastPosition == null || currentPosition != lastPosition) {
                    // Remove the old barrier if it exists
                    lastBarrierPosition?.let {
                        player.instance?.setBlock(it, Block.AIR)
                    }

                    // Only place the barrier if the block above is air
                    val blockAbove = player.instance?.getBlock(barrierPosition)
                    if (blockAbove == null || blockAbove.compare(Block.AIR)) {
                        player.instance?.setBlock(barrierPosition, Block.BARRIER)
                    }

                    // Update the last known position and the barrier position
                    lastPosition = currentPosition
                    lastBarrierPosition = barrierPosition
                }
            }
        }.repeat(TaskSchedule.tick(5)) // Check every 5 ticks (100ms)
            .schedule()
    }

    fun forceSwimmingPose(player: net.minestom.server.entity.Player) {
        // Set the player's pose to swimming
        player.pose = EntityPose.SWIMMING

        // Set the player's hunger to 0
        player.food = 0
    }
}
