package net.hellz.commands


import net.hellz.clerk.PlayerProfile
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command

class TeleportCommand {
    @Command("teleport", "tp")
    fun teleport(
        sender: Player,
        x: Double,
        y: Double,
        z: Double
    ) {
        val player = sender.asPlayer() as? PlayerProfile ?: return // Imports the LuckPerms player profiles

        if (!player.hasPermission("commands.teleport")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }



        val location = Pos(x, y, z)
        sender.teleport(location)
    }
}