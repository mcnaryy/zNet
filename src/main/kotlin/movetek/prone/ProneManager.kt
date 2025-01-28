package net.hellz.movetek.prone

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartSneakingEvent
import java.util.concurrent.ConcurrentHashMap

object ProneManager {
    private const val DOUBLE_SNEAK_THRESHOLD_MS = 500 // Time in milliseconds
    private val lastSneakTimestamps = ConcurrentHashMap<String, Long>()
    private val playerShulkers = ConcurrentHashMap<Player, EntityCreature>()

    init {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerStartSneakingEvent::class.java) { event ->

            val player = event.player

            val isLookingDown = player.position.pitch >= 60.0

            if (isLookingDown) {
                val currentTime = System.currentTimeMillis()
                val playerId = player.uuid.toString()

                if (player.entityMeta.isSwimming) {
                    removeShulker(player)
                    return@addListener
                }

                val lastSneakTime = lastSneakTimestamps[playerId]

                if (lastSneakTime != null && currentTime - lastSneakTime <= DOUBLE_SNEAK_THRESHOLD_MS) {
                    initiateProne(player)
                    lastSneakTimestamps.remove(playerId)
                } else {
                    lastSneakTimestamps[playerId] = currentTime
                }
            }
        }

        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent::class.java) { event ->
            val player = event.player
            updateShulkerPosition(player)

            // Check if the player has jumped
            if (player.position.y > player.previousPosition.y) {
                removeShulker(player)
            }
        }
    }

    fun initiateProne(player: Player) {
        val shulker = EntityCreature(EntityType.SHULKER)
        shulker.isInvulnerable = true
        shulker.isInvisible = true
        shulker.setNoGravity(true)
        shulker.setInstance(player.instance, player.position)
        shulker.teleport(player.position.add(0.0, 1.1, 0.0))

        playerShulkers[player] = shulker
        player.entityMeta.isSwimming = true
        player.entityMeta.isSprinting = false
    }

    fun updateShulkerPosition(player: Player) {
        val shulker = playerShulkers[player] ?: return
        shulker.teleport(player.position.add(0.0, 1.0, 0.0))
    }

    fun removeShulker(player: Player) {
        val shulker = playerShulkers.remove(player) ?: return
        shulker.remove()
        player.entityMeta.isSwimming = false
    }
}