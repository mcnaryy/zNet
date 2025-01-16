package net.hellz.movetek

import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute

object MovementUtils {

    // Method to set a player's movement speed

    fun setMovementSpeed(player: Player, speed: Double) {
        // Set the players attribute for movement speed
        player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speed)
        player.sendMessage("Your movement speed has been updated to: $speed")
    }
}