package net.hellz


import ca.atlasengine.projectiles.BowModule
import ca.atlasengine.projectiles.entities.ArrowProjectile
import ca.atlasengine.projectiles.entities.FollowProjectile
import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter
import me.lucko.luckperms.minestom.CommandRegistry
import me.lucko.luckperms.minestom.LuckPermsMinestom
import net.hellz.commands.CommandRegistrar
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block
import net.worldseed.multipart.ModelEngine
import java.nio.file.Path
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter
import me.lucko.luckperms.minestom.LPMinestomPlugin
import net.hellz.luckperms.DummyContextProvider
import net.hellz.luckperms.ExamplePlayer
import net.hellz.luckperms.HoconConfigurationAdapter
import net.hellz.events.BlockBreakEvent
import net.hellz.events.PlayerDropEvent
import net.hellz.events.PlayerPickupEvent
import net.hellz.movetek.stamina.SprintDetection
import net.kyori.adventure.text.Component
import net.luckperms.api.context.DefaultContextKeys
import net.luckperms.api.context.ImmutableContextSet
import net.luckperms.api.node.Node
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.PlayerHand
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.item.Material
import net.minestom.server.network.player.GameProfile
import net.minestom.server.network.player.PlayerConnection
import net.minestom.server.utils.chunk.ChunkSupplier

/**
 * Main entry point for the Minestom server.
 */

fun main() {
    // Initialization
    val minecraftServer = MinecraftServer.init()

    // test implement
    ModelEngine.getModelMaterial()

    BlockBreakEvent
    PlayerPickupEvent
    PlayerDropEvent

    SprintDetection

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

        // Spawn a custom entity for demonstration
        val entity = EntityCreature(EntityType.ZOMBIE)
        entity.setInstance(instanceContainer, Pos(0.0, 42.0, 0.0))
        entity.getAttribute(Attribute.MAX_HEALTH).baseValue = 500.0
        entity.heal()

        // Add item usage event handling
        player.eventNode().addListener(PlayerUseItemEvent::class.java) { e ->
            if (e.hand != PlayerHand.MAIN) return@addListener
            if (e.itemStack.material() === Material.FIRE_CHARGE) {
                FollowProjectile(
                    EntityType.SNOWBALL,
                    e.player,
                    e.player,
                    0.04f,
                    0.001f
                ).shoot(e.player.position.add(0.0, e.player.eyeHeight, 0.0).asVec(), 1.0, 1.0)
            }
        }

        // Integrate the BowModule for handling bow-related events
        BowModule(player.eventNode()) { player, i ->
            ArrowProjectile(
                EntityType.ARROW,
                player
            )
        }
    }

    // Handle player spawn event, setting their game mode and permission level.
    MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent::class.java) { event ->
        event.player.gameMode = GameMode.CREATIVE
        event.player.permissionLevel = 4
    }


    // initialize LuckPerms
    val directory = Path.of("clerk/luckperms")
    val luckPerms = LuckPermsMinestom.builder(directory)
        .commandRegistry(CommandRegistry.minestom())
        .contextProvider(DummyContextProvider())
        .configurationAdapter { plugin: LPMinestomPlugin? ->
            MultiConfigurationAdapter(
                plugin,
                EnvironmentVariableConfigAdapter(plugin),
                HoconConfigurationAdapter(plugin!!)
            )
        }.permissionSuggestions("test.permission", "test.other")
        .dependencyManager(true)
        .enable()

    // set custom player provider (optional)
    val connectionManager = MinecraftServer.getConnectionManager()
    connectionManager.setPlayerProvider { connection: PlayerConnection?, profile: GameProfile? ->
        ExamplePlayer(
            luckPerms,
            profile!!, connection!!
        )
    }

    // set custom chat handling (optional)
    globalEventHandler.addListener(PlayerChatEvent::class.java) { event ->
        if (event.player !is ExamplePlayer) return@addListener
        event.setFormattedMessage(
            Component.text().append(
                event.player.getName(),
                Component.text(": " + event.rawMessage)
            ).build()
        )
    }

    // example of adding permissions to a player via the custom player class
    globalEventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
        if (event.player !is ExamplePlayer) return@addListener
        val player = event.player as ExamplePlayer
        player.setPermission(
            Node.builder("*") //.expiry(10, TimeUnit.SECONDS)
                .context(
                    ImmutableContextSet.builder()
                        .add(DefaultContextKeys.DIMENSION_TYPE_KEY, "minecraft:overworld")
                        .add("dummy", "true")
                        .build()
                ).build(),
            true
        ).thenAccept { result ->
            player.sendMessage("Attempted to add permission: $result")
        }
    }

    // command to check if a player has a permission
    val commandManager: CommandManager = MinecraftServer.getCommandManager()
    val command = Command("test")
    val permissionArgument = ArgumentType.String("permission")
    command.addSyntax({ sender, context ->
        val permission: String = context.get(permissionArgument)
        if (sender is ExamplePlayer) sender.sendMessage(sender.getPermission(permission).toString())
        else sender.sendMessage("Sender is not a player")
    }, permissionArgument)
    commandManager.register(command)


    // Register other commands
    CommandRegistrar.registerCommands()

    // Enables online mode, adds skins, etc.
    MojangAuth.init()

    // Start the server
    minecraftServer.start("0.0.0.0", 25565)
}
