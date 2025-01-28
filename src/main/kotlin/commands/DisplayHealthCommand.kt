package net.hellz.commands

import net.hellz.health.Health
import net.hellz.clerk.PlayerProfile
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command

class DisplayHealthCommand(private val healthManager: Health) {

    @Command("displayhealth", "health")
    fun health(sender: Player, targetPlayer: Player) {
        val player = sender as? PlayerProfile ?: return

        if (!player.hasPermission("commands.displayhealth")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }

        val healthStatus = healthManager.getPlayerHealth(targetPlayer.uuid.toString())
        if (healthStatus != null) {
            sender.sendMessage("Health for ${targetPlayer.username}: ${healthStatus.health}, Head=${healthStatus.head}, Legs=${healthStatus.legs}, Infection=${healthStatus.infection}.")
        } else {
            sender.sendMessage("No health data found for ${targetPlayer.username}.")
        }
    }
}