package net.hellz.commands

import net.hellz.clerk.PlayerProfile
import revxrsal.commands.annotation.Command
import net.minestom.server.entity.Player
import net.minestom.server.entity.GameMode

class ClearCommand {
    @Command("clear", "ci")
    fun clear(
        sender: Player,
    ) {
        val player = sender.asPlayer() as? PlayerProfile ?: return // Imports the LuckPerms player profiles

        if (!player.hasPermission("commands.gamemode")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }

        sender.inventory.clear()
        sender.sendMessage("You have cleared your inventory.")
    }
}
