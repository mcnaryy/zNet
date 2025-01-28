package net.hellz.weapons.firearms

import net.hellz.modules.EventLogger
import net.hellz.rayfast.RaycastUtil
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Predicate

class Firearm {
    var name: String = "Firearm"
    var damage: Float = 20.0F
    var ammo: Int = 30
    var weight: Float = 4.4F
    var range: Double = 50.0
    var accuracy: Float = 75.0F
    var reloadTime: Float = 1.0F

    fun fireHitscan(player: Player) {
        val direction = player.position.direction()
        val startPoint = player.position.add(0.0, player.eyeHeight, 0.0).toVec()
        val raycastResult = RaycastUtil.raycast(
            player.instance,
            startPoint,
            direction,
            range,
            Predicate { it != player }
        )

        val hitPoint = raycastResult.hitPositions.firstOrNull()?.toVec() ?: startPoint.add(direction.mul(50.0))
        shootTrail(player, startPoint, hitPoint)

        if (raycastResult.hitEntities.isNotEmpty()) {
            val hitMessages = raycastResult.hitEntities.mapIndexed { index, hitEntity ->
                val hitPosition = raycastResult.hitPositions[index]
                val distance = String.format("%.1f", startPoint.distance(hitPosition.toVec()))
                val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                val toEntity = hitPosition.toVec().sub(startPoint).normalize()
                if (direction.dot(toEntity) > 0) {
                    spawnFireParticle(player, hitPosition.toVec())
                    EventLogger.addLog(player, "[$currentTime] You hit enemy ${hitEntity.entityType} in the ${getLandedShot(player, hitPosition)} from ${distance}m")

                    if (hitEntity is LivingEntity) {
                        applyDamageAndKnockback(hitEntity, direction, hitPosition)
                    }
                } else {
                    player.sendMessage("Missed")
                }
            }
        } else if (raycastResult.hitPositions.isNotEmpty()) {
            val hitPosition = raycastResult.hitPositions.first()
            val formattedPosition = String.format("(%.2f, %.2f, %.2f)", hitPosition.x(), hitPosition.y(), hitPosition.z())
            player.sendMessage("Hit a block at: $formattedPosition")
        } else {
            player.sendMessage("Missed")
        }
    }

    private fun shootTrail(player: Player, startPoint: Vec, hitPoint: Vec) {
        val step = 0.5
        var current = startPoint
        val totalSteps = (startPoint.distance(hitPoint) / step).toInt()
        val directionVec = Vec.fromPoint(hitPoint.sub(startPoint)).normalize().mul(step)

        for (i in 0..totalSteps) {
            val particlePacket = ParticlePacket(
                Particle.ASH,
                current,
                Vec(0.0, 0.0, 0.0),
                0.0f,
                1
            )
            player.instance!!.sendGroupedPacket(particlePacket)
            current = current.add(directionVec)
        }
    }

    private fun spawnFireParticle(player: Player, position: Vec) {
        val fireParticlePacket = ParticlePacket(
            Particle.LAVA,
            position,
            Vec(0.0, 0.0, 0.0),
            0.0f,
            5
        )
        player.instance!!.sendGroupedPacket(fireParticlePacket)
    }

    private fun applyDamageAndKnockback(entity: LivingEntity, direction: Vec, hitPosition: Point) {
        entity.damage(DamageType.PLAYER_ATTACK, damage / 5)
        val knockbackDirection = direction.normalize().mul(-1.0)
        entity.takeKnockback(0.05f, knockbackDirection.x(), knockbackDirection.z())
    }

    private fun getLandedShot(player: Player, hitPosition: Point): String {
        return when {
            hitPosition.y() > 41.5 -> "Head"
            hitPosition.y() > 41.0 -> "Chest"
            hitPosition.y() >= 40.0 -> "Legs"
            else -> "Invalid Position"
        }
    }


    private fun Pos.toVec(): Vec {
        return Vec(this.x(), this.y(), this.z())
    }

    private fun Point.toVec(): Vec {
        return Vec(this.x(), this.y(), this.z())
    }
}