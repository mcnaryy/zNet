package net.hellz.events

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.ItemEntity
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import java.time.Duration

object BlockBreakEvent {

    init {
        // Register the event listener to handle player block breaking
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent::class.java) { event ->
            if (event.player.gameMode == GameMode.CREATIVE) {
                return@addListener
            }
            println("${event.player.username} has broken a block.")

            val material = event.block.registry().material()

            if (material != null) {
                val itemStack = ItemStack.of(material)
                val itemEntity = ItemEntity(itemStack)
                // Set the position for the dropped item
                itemEntity.setInstance(event.instance, event.blockPosition.add(0.5, 0.5, 0.5))
                // Set the delay for item pickup
                itemEntity.setPickupDelay(Duration.ofMillis(500))
            }

            // Build a message for the player
            val message = Component.text("You just broke a block", NamedTextColor.AQUA)
                .color(NamedTextColor.AQUA)
                .appendNewline()
                .append(
                    Component.text("You got: ", TextColor.fromCSSHexString("#32a852"))
                        .append(Component.text(material?.name() ?: "Unknown material", NamedTextColor.GOLD))
                        .decorate(TextDecoration.ITALIC, TextDecoration.UNDERLINED)
                        .hoverEvent(Component.text("Hovering over the item!"))
                )

            // Send the message to the player
            event.player.sendMessage(message)

        }
    }
}
