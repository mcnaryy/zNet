package net.hellz.commands

import net.hellz.clerk.PlayerProfile
import revxrsal.commands.annotation.Command
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Optional



class LatencyCommand {
    @Command("latency", "ping", "ms")
    fun latency(
        sender: Player,
        @Optional target: Player? = null
    ) {
        val player = sender.asPlayer() as? PlayerProfile ?: return // Imports the LuckPerms player profiles

        if (!player.hasPermission("commands.latency")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }

        if (target != null) {
            val ping = target.getLatency()
            sender.sendMessage("${target.username}'s Latency: ${ping}ms")
        } else {
            val ping = sender.getLatency()
            sender.sendMessage("Latency: ${ping}ms")
        }
    }
}