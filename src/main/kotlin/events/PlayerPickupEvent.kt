package net.hellz.events

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.item.PickupItemEvent

object PlayerPickupEvent {

    init {
        // Register the event listener using the global event handler
        MinecraftServer.getGlobalEventHandler().addListener(PickupItemEvent::class.java) { event ->
            println("${event.livingEntity.uuid} has picked up an item.") // Use getUsername() for the player's name

            // Check if the living entity is a player
            if (event.livingEntity is Player) {
                val player = event.livingEntity as Player

                // Get the item stack from the event
                val itemStack = event.itemStack

                // Add the item stack to the player's inventory
                val success = player.inventory.addItemStack(itemStack)

                if (success) {
                    // Notify the player that the item was picked up and added to their inventory
                    player.sendMessage(Component.text("You have picked up: ${itemStack.material()}", NamedTextColor.GREEN))
                } else {
                    // If inventory was full, notify the player
                    player.sendMessage(Component.text("Your inventory is full! Cannot pick up: ${itemStack.material()}", NamedTextColor.RED))
                }
            }
        }
    }
}
