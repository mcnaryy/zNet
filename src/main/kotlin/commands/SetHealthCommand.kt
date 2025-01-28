package net.hellz.commands


import net.hellz.health.Health
import net.hellz.clerk.PlayerProfile
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command

class SetHealthCommand(public val healthManager: Health) {
    @Command("sethealth", "heal")
    fun heal(
        sender: Player,
        newHealth: Float,
    ) {
        val player = sender.asPlayer() as? PlayerProfile ?: return // Imports the LuckPerms player profiles

        if (!player.hasPermission("commands.sethealth")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }
        if (newHealth < 1 || newHealth > 100) {
            sender.sendMessage("Health must be between 1 and 100.")
            return
        }

        val playerId = sender.uuid.toString()
        val healthStatus = healthManager.getPlayerHealth(playerId)
        if (healthStatus != null) {
            healthStatus.health = newHealth
            healthManager.setPlayerHealth(playerId, healthStatus.health, healthStatus.head, healthStatus.legs, healthStatus.infection)
            sender.sendMessage("Your health has been updated to $newHealth in the health manager.")
        } else {
            sender.sendMessage("No health data found for you.")
        }

        sender.health = newHealth / 5
        sender.sendMessage("You have set your health to $newHealth.")
    }
}