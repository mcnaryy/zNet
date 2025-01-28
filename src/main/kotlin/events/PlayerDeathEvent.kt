package net.hellz.events

import net.hellz.events.PlayerDamageEvent.healthManager
import net.hellz.health.Health
import net.hellz.modules.EventLogger
import net.hellz.modules.PlayerCorpse.spawnCorpse
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PlayerDeathEvent {

    private val healthManager = Health

    init {
        // Register the event listener to handle player deaths
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDeathEvent::class.java) { event ->
            val victim = event.player
            val killer = event.entity
            val source = event.entity.lastDamageSource
            val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            EventLogger.addLog(
                killer,
                "[$currentTime] KILL: You killed ${victim.username}"
            )
            EventLogger.addLog(
                victim,
                "[$currentTime] DEATH: ${killer.username} Killed you!"
            )

            // Create custom death message
            val customDeathText = Component.text("${victim.username} has met their demise by ${killer.username}!", NamedTextColor.RED)
            val customChatMessage = Component.text("RIP ${victim.username}. You will be missed. Killed by ${killer.username}.", NamedTextColor.GRAY)

            // Set the custom death message
            event.deathText = customDeathText
            event.chatMessage = customChatMessage


            victim.isInvisible = true
            spawnCorpse(event.player, event.player.position, event.player.position.direction())
        }
        MinecraftServer.getGlobalEventHandler().addListener(PlayerRespawnEvent::class.java) { event ->
            event.player.isInvisible = false
            healthManager.setPlayerHealth(
                event.player.uuid.toString(),
                100.0f, // Initial health
                "Healthy", // Initial head status
                "Healthy",  // Initial legs status
                "Healthy"  // Initial infection status
            )
        }
    }
}
