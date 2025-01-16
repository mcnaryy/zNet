package net.hellz.events

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.entity.ItemEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import java.time.Duration

object PlayerDropEvent {

    init {
        // Create a node that listens specifically for player-related events
        val playerNode = EventNode.type("players", EventFilter.PLAYER)

        // Register the item drop listener to handle item drops
        playerNode.addListener(ItemDropEvent::class.java) { event ->
            println("${event.player.username} has dropped an item.")

            // Get the player's eye level position
            val playerEyeLevelPosition = event.player.position.add(0.0, event.player.eyeHeight, 0.0)

            // Create an ItemEntity for the dropped item
            val itemEntity = ItemEntity(event.itemStack)

            // Set the item entity's position to be at the player's eye level
            itemEntity.setInstance(event.player.instance, playerEyeLevelPosition)

            // Apply a controlled velocity to make it drop smoothly
            itemEntity.velocity = event.player.position.add(0.0, -0.10, 0.0).direction().mul(5.0)

            // Set the pickup delay to prevent immediate pickup
            itemEntity.setPickupDelay(Duration.ofMillis(500))
        }

        // Register the event handler to the global event handler
        MinecraftServer.getGlobalEventHandler().addChild(playerNode)
    }
}
