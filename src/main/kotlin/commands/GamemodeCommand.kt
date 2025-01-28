package net.hellz.commands

import net.hellz.clerk.PlayerProfile
import revxrsal.commands.annotation.Command
import net.minestom.server.entity.Player
import net.minestom.server.entity.GameMode


class GamemodeCommand {
    @Command("gamemode", "gm", "gmc")
    fun gamemode(
        sender: Player,
        gamemode: GameMode
    ) {
        val player = sender.asPlayer() as? PlayerProfile ?: return // Imports the LuckPerms player profiles

        if (!player.hasPermission("commands.gamemode")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }

        sender.setGameMode(gamemode)
        sender.sendMessage("You have switched to $gamemode mode.")
    }
}
