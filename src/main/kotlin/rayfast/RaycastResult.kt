package net.hellz.rayfast

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Entity

/**
 * If hit positions is empty, nothing was hit
 * If hit entities is empty but hit positions isn't, only blocks were hit
 * If neither are empty, entities were hit
 */
@JvmRecord
data class RaycastResult(val hitEntities: List<Entity>, val hitPositions: List<Point>) {
    companion object {
        val HIT_NOTHING: RaycastResult = RaycastResult(emptyList(), emptyList())
    }
}