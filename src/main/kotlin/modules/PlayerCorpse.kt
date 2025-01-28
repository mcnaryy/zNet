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
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import java.util.function.Consumer


object PlayerCorpse {
    private const val DELETION_DELAY_SECONDS = 10L
    private val corpseTag = Tag.String("corpse")

    fun spawnCorpse(player: Player, location: Pos, direction: Vec) {
        val playerSkin = player.skin
        val npcSkin = PlayerSkin(playerSkin?.textures(), playerSkin?.signature())
        val npcName = "${player.username}'s Corpse"
        val onClick: Consumer<Player> = Consumer { p: Player -> p.sendMessage("${player.username}'s corpse") }

        val npc = NPC(npcName, npcSkin, player.instance, location, onClick)
        npc.setPose(EntityPose.SWIMMING)
        npc.isInvulnerable = true
        npc.setTag(corpseTag, player.uuid.toString())


        val interactionEntity = InteractionEntity(npc)
        interactionEntity.setTag(corpseTag, player.uuid.toString())

        MinecraftServer.getSchedulerManager().buildTask {
            npc.remove()
            interactionEntity.remove()
            println("Auto-removed player corpse belonging to ${player.username}")
        }.delay(TaskSchedule.seconds(PlayerCorpse.DELETION_DELAY_SECONDS)).schedule()
    }
}

