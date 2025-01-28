package net.hellz

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.*
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.metadata.PlayerMeta
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket
import net.kyori.adventure.text.Component
import java.util.List
import java.util.function.Consumer

class NPC(
    private val name: String, private val skin: PlayerSkin, instance: Instance,
    spawn: Pos, val onClick: Consumer<Player>
) : EntityCreature(EntityType.PLAYER) {
    init {
        val meta = getEntityMeta() as PlayerMeta
        meta.setNotifyAboutChanges(false)
        meta.isCapeEnabled = false
        meta.isJacketEnabled = true
        meta.isLeftSleeveEnabled = true
        meta.isRightSleeveEnabled = true
        meta.isLeftLegEnabled = true
        meta.isRightLegEnabled = true
        meta.isHatEnabled = true
        meta.setNotifyAboutChanges(true)

        setInstance(instance, spawn)

        instance.eventNode()
            .addListener(EntityAttackEvent::class.java, (Consumer { event: EntityAttackEvent -> this.handle(event) }))
            .addListener(
                PlayerEntityInteractEvent::class.java
            ) { event: PlayerEntityInteractEvent -> this.handle(event) }
    }

    fun handle(event: EntityAttackEvent) {
        if (event.target !== this) return
        if (event.entity !is Player) return
        onClick.accept(event.entity as Player)
    }

    fun handle(event: PlayerEntityInteractEvent) {
        if (event.target !== this) return
        if (event.hand != PlayerHand.MAIN) return  // Prevent duplicating event

        onClick.accept(event.entity)
    }

    override fun updateNewViewer(player: Player) {
        // Required to spawn player
        val properties = List.of(
            PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature())
        )
        player.sendPacket(
            PlayerInfoUpdatePacket(
                PlayerInfoUpdatePacket.Action.ADD_PLAYER,
                PlayerInfoUpdatePacket.Entry(
                    uuid, name, properties, false, 0, GameMode.SURVIVAL, Component.text(name), null, 0
                )
            )
        )

        super.updateNewViewer(player)
    }

    private class LookAtPlayerGoal(entityCreature: EntityCreature?) : GoalSelector(entityCreature!!) {
        private var target: Entity? = null

        override fun shouldStart(): Boolean {
            target = findTarget()
            return target != null
        }

        override fun start() {
        }

        override fun tick(time: Long) {
            if (entityCreature.getDistanceSquared(target!!) > 225 ||
                entityCreature.instance !== target!!.instance
            ) {
                target = null
                return
            }

            entityCreature.lookAt(target!!)
        }

        override fun shouldEnd(): Boolean {
            return target == null
        }

        override fun end() {
        }
    }
}