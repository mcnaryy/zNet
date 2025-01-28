package net.hellz.modules

import net.hellz.NPC
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.*
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import java.util.function.Consumer

object PlayerSleeper {

    private const val DELETION_DELAY_SECONDS = 500L
    private val sleeperTag = Tag.String("sleeper")

    init {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent::class.java) { event ->
            println("Player ${event.player.username} disconnected. Spawning sleeper NPC...")
            spawnSleeper(event.player, event.player.position, event.player.position.direction())
        }

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerPreLoginEvent::class.java) { event ->
            val instance = MinecraftServer.getInstanceManager().instances.firstOrNull() ?: return@addListener
            val sleeperEntities = instance.entities.filter { it.getTag(sleeperTag) == event.playerUuid.toString() }
            sleeperEntities.forEach { it.remove() }
            println("Removed sleeper NPC and turtle for player ${event.playerUuid}")
        }
    }

    private fun spawnSleeper(player: Player, location: Pos, direction: Vec) {

        println(direction)
        val playerSkin = player.skin
        val npcSkin = PlayerSkin(playerSkin?.textures(), playerSkin?.signature())
        val npcName = "${player.username}'s Sleeper"
        val onClick: Consumer<Player> = Consumer { p: Player -> p.sendMessage("${player.username}'s sleeper") }

        val lefDirection = Vec(-direction.z(), direction.y(), direction.x())
        val npc = NPC(npcName, npcSkin, player.instance, location.withDirection(lefDirection), onClick)
        npc.setPose(EntityPose.SLEEPING)
        npc.setTag(sleeperTag, player.uuid.toString())

        println("The player is facing $direction")

        val npcDirection = npc.position.direction()
        val leftDirection = if (npcDirection.x() != 0.0) {
            Vec(-npcDirection.z(), npcDirection.y(), -npcDirection.x())
        } else {
            Vec(npcDirection.z(), npcDirection.y(), -npcDirection.x())
        }

        val interactionEntity = InteractionEntity(npc)
        interactionEntity.setTag(sleeperTag, player.uuid.toString())

        val turtlePosition = location.add(leftDirection.mul(1.0)).add(0.0, 0.5, 0.0)
        interactionEntity.setInstance(player.instance, turtlePosition)

        MinecraftServer.getSchedulerManager().buildTask {
            npc.remove()
            interactionEntity.remove()
            println("Auto-removed sleeper NPC and turtle for player ${player.username}")
        }.delay(TaskSchedule.seconds(DELETION_DELAY_SECONDS)).schedule()
    }
}

class InteractionEntity(private val linkedNpc: NPC) : Entity(EntityType.TURTLE) {

    init {
        isInvisible = false
        MinecraftServer.getGlobalEventHandler().addListener(PlayerEntityInteractEvent::class.java) { event ->
            if (event.target == this) {
                onInteract(event.player, event.hand)
            }
        }
        MinecraftServer.getGlobalEventHandler().addListener(EntityAttackEvent::class.java) { event ->
            if (event.target == this && event.entity is Player) {
                onInteract(event.entity as Player, PlayerHand.MAIN)
            }
        }
    }

    private fun onInteract(player: Player, hand: PlayerHand) {
        linkedNpc.onClick.accept(player)
    }
}