package net.hellz.modules

import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object EventLogger {

    private val playerLogs = ConcurrentHashMap<UUID, MutableList<String>>()

    init {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerPreLoginEvent::class.java) { event ->
            val playerUuid = event.playerUuid
            println("Clearing $playerUuid's log data")
            playerLogs[playerUuid] = mutableListOf()
        }
    }

    fun addLog(player: Player, log: String) {
        val logs = playerLogs.computeIfAbsent(player.uuid) { mutableListOf() }
        logs.add(log)
    }

    fun getLogs(player: Player): List<String> {
        return playerLogs[player.uuid] ?: emptyList()
    }
}