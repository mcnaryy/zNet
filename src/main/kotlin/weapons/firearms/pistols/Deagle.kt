package net.hellz.weapons.firearms.pistols

import net.hellz.rayfast.RaycastUtil
import net.hellz.weapons.firearms.Firearm
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerStartSprintingEvent
import net.minestom.server.event.player.PlayerStopSprintingEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

enum class FirearmState {
    HIPFIRE, ADS, SPRINTING
}

object Deagle {

    private val playerStates = mutableMapOf<Player, FirearmState>()

    init {
        // Initialize RaycastUtil
        RaycastUtil.init()

        // Register the event listener to handle player hand animation (left-click)
        MinecraftServer.getGlobalEventHandler().addListener(PlayerHandAnimationEvent::class.java) { event ->
            val player = event.player
            if (!player.isSprinting) {

                // Get the current state of the player
                val currentState = playerStates[player] ?: FirearmState.HIPFIRE

                // Toggle the state
                val newState = if (currentState == FirearmState.HIPFIRE) FirearmState.ADS else FirearmState.HIPFIRE
                playerStates[player] = newState

                // Update the item model data based on the new state
                val newModelData = if (newState == FirearmState.ADS) 1009 else 9
                val newItemStack = ItemStack.builder(Material.FEATHER)
                    .customModelData(newModelData)
                    .build()
                player.setItemInMainHand(newItemStack)

                player.sendMessage("State changed to: $newState")
            }
        }

        MinecraftServer.getGlobalEventHandler().addListener(PlayerUseItemEvent::class.java) { event ->
            val player = event.player
            if (!player.isSprinting) {
                val deagle = Firearm()
                deagle.fireHitscan(event.player)
            }
        }

        // Register the event listener to handle player start sprinting
        MinecraftServer.getGlobalEventHandler().addListener(PlayerStartSprintingEvent::class.java) { event ->
            val player = event.player
            player.sendMessage("State changed to: Sprinting")

            // Set the state to SPRINTING
            playerStates[player] = FirearmState.SPRINTING

            // Update the item model data based on the new state
            val newItemStack = ItemStack.builder(Material.FEATHER)
                .customModelData(2009)
                .build()
            player.setItemInMainHand(newItemStack)
        }

        // Register the event listener to handle player stop sprinting
        MinecraftServer.getGlobalEventHandler().addListener(PlayerStopSprintingEvent::class.java) { event ->
            val player = event.player

            // Revert to the previous state (HIPFIRE or ADS)
            val previousState = playerStates[player] ?: FirearmState.HIPFIRE
            val newState = if (previousState == FirearmState.SPRINTING) FirearmState.HIPFIRE else previousState
            playerStates[player] = newState

            // Update the item model data based on the new state
            val newModelData = if (newState == FirearmState.ADS) 1009 else 9
            val newItemStack = ItemStack.builder(Material.FEATHER)
                .customModelData(newModelData)
                .build()
            player.setItemInMainHand(newItemStack)

            player.sendMessage("State changed to: $newState")
        }
    }
}