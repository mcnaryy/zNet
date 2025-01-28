package net.hellz

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block
import net.worldseed.multipart.ModelEngine

import net.hellz.commands.*

import net.hellz.health.Health
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.utils.chunk.ChunkSupplier
import revxrsal.commands.minestom.MinestomLamp


/**
 * Main entry point for the Minestom server.
 */

fun main() {
    // Initialization
    val minecraftServer = MinecraftServer.init()

    // test implement
    ModelEngine.getModelMaterial()


    // Create the instance
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    // Set the ChunkGenerator
    instanceContainer.setGenerator { unit ->
        unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
    }


    // Set the chunk supplier with a custom implementation
    instanceContainer.setChunkSupplier(object : ChunkSupplier {
        override fun createChunk(instance: Instance, x: Int, z: Int): LightingChunk {
            return LightingChunk(instance, x, z) // Create LightingChunk with Instance, x, and z
        }
    })


    // Add an event callback to specify the spawning instance (and the spawn position)
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        val player = event.player
        event.spawningInstance = instanceContainer
        player.respawnPoint = Pos(0.0, 42.0, 0.0)

    }

    val healthManager = Health

    // Handle player spawn event, setting their game mode and permission level.
    MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent::class.java) { event ->
        event.player.gameMode = GameMode.CREATIVE
        event.player.permissionLevel = 4

        healthManager.setPlayerHealth(
            event.player.uuid.toString(),
            100.0f, // Initial health
            "Healthy", // Initial head status
            "Healthy",  // Initial legs status
            "Healthy"  // Initial infection status
        )
    }


    // Enables online mode, adds skins, etc.
    MojangAuth.init()

    // Start the server
    minecraftServer.start("0.0.0.0", 25565)

}
