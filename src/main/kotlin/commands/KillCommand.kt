package net.hellz.commands


import net.hellz.clerk.PlayerProfile
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command

class KillCommand {
    @Command("kill", "suicide")
    fun kill(
        sender: Player,
    ) {
        val player = sender.asPlayer() as? PlayerProfile ?: return // Imports the LuckPerms player profiles

        if (!player.hasPermission("commands.kill")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }

        sender.kill()
        sender.sendMessage("You have committed suicide.")
    }
}
