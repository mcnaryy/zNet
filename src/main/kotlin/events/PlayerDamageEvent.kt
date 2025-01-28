package net.hellz.events

import net.hellz.health.Health
import net.hellz.modules.EventLogger
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PlayerDamageEvent {

    val healthManager = Health

    init {
        MinecraftServer.getGlobalEventHandler().addListener(EntityAttackEvent::class.java) { event ->
            val victim: Entity = event.target
            val attacker: Entity = event.entity
            val distance = Math.round(event.target.getDistance(attacker) * 10) / 10.0
            var damage = 10F

            // Apply damage
            if (victim is LivingEntity) {
                if (!victim.isInvulnerable) {
                    // Vanilla knockback calculation based on yaw and pitch
                    val yaw = Math.toRadians(attacker.position.yaw.toDouble())
                    val pitch = Math.toRadians(attacker.position.pitch.toDouble())

                    // Calculate knockback direction on X, Y, and Z axes (reversed)
                    val knockbackX = Math.sin(yaw).toDouble()
                    val knockbackZ = -Math.cos(yaw).toDouble()
                    val knockbackY = -Math.sin(pitch).toDouble()

                    // Apply knockback
                    victim.takeKnockback(0.4f, knockbackX, knockbackZ)

                    if (attacker is LivingEntity) {
                        val weapon: ItemStack = attacker.itemInMainHand
                        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                        if (attacker is Player) {
                            if (victim is Player) {
                                EventLogger.addLog(
                                    attacker,
                                    "[$currentTime] COMBAT: YOU ATTACKED ${victim.username} using ${weapon.material()} from $distance meters!"
                                )
                                EventLogger.addLog(
                                    victim,
                                    "[$currentTime] COMBAT: ${attacker.username} ATTACKED YOU using ${weapon.material()} from $distance meters!!"
                                )
                            } else {
                                EventLogger.addLog(
                                    attacker,
                                    "[$currentTime] COMBAT: YOU ATTACKED ${victim.entityType.name()} using ${weapon.material()} from $distance meters!"
                                )
                            }
                        } else {
                            if (victim is Player) {
                                EventLogger.addLog(
                                    victim,
                                    "[$currentTime] COMBAT: ${attacker.entityType.name()} ATTACKED YOU!"
                                )
                            }
                        }

                        victim.damage(DamageType.PLAYER_ATTACK, damage / 5)
                        if (victim is Player) {
                            healthManager.removePlayerHealthAmount(victim.uuid.toString(), damage)
                        }
                    }
                }
            }
        }
    }
}

