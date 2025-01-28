package net.hellz.rayfast

import dev.emortal.rayfast.area.area3d.Area3d
import dev.emortal.rayfast.area.area3d.Area3dRectangularPrism
import dev.emortal.rayfast.casting.grid.GridCast
import dev.emortal.rayfast.vector.Vector3d
import net.minestom.server.collision.BoundingBox
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import java.util.function.Predicate

object RaycastUtil {
    private const val GRID_SIZE = 16.0

    private val boundingBoxToArea3dMap: MutableMap<BoundingBox, Area3d> = HashMap()

    fun init() {
        Area3d.CONVERTER.register<BoundingBox>(BoundingBox::class.java) { box: BoundingBox? ->
            boundingBoxToArea3dMap.computeIfAbsent(box!!) { bb: BoundingBox ->
                Area3dRectangularPrism.wrapper(
                    bb,
                    BoundingBox::minX, BoundingBox::minY, BoundingBox::minZ,
                    BoundingBox::maxX, BoundingBox::maxY, BoundingBox::maxZ
                )
            }
        }
    }

    fun hasLineOfSight(a: Entity, b: Entity): Boolean {
        return hasLineOfSight(a.instance, a.position.add(0.0, a.eyeHeight, 0.0), b.position.add(0.0, b.eyeHeight, 0.0))
    }

    fun hasLineOfSight(instance: Instance, startPoint: Point, endPoint: Point): Boolean {
        val direction = Vec.fromPoint(endPoint.sub(startPoint)).normalize()
        val maxDistance = startPoint.distance(endPoint)

        return raycastBlock(instance, startPoint, direction, maxDistance) == null
    }

    fun raycastBlock(instance: Instance, startPoint: Point, direction: Point, maxDistance: Double): Point? {
        val gridIter: Iterator<Vector3d> = GridCast.createExactGridIterator(
            startPoint.x(), startPoint.y(), startPoint.z(),
            direction.x(), direction.y(), direction.z(),
            1.0, maxDistance
        )

        while (gridIter.hasNext()) {
            val gridUnit = gridIter.next()
            val pos: Point = Vec(gridUnit.x(), gridUnit.y(), gridUnit.z())

            try {
                val hitBlock = instance.getBlock(pos)

                if (hitBlock.isSolid) {
                    return pos
                }
            } catch (ignored: NullPointerException) {
                // catches unloaded chunk errors
                break
            }
        }

        return null
    }

    fun raycastEntity(
        instance: Instance, startPoint: Point, direction: Point,
        maxDistance: Double, hitFilter: Predicate<Entity?>
    ): RaycastResult {
        val hitEntities = mutableSetOf<Entity>()
        val hitPositions = mutableSetOf<Point>()

        val grid = buildSpatialGrid(instance)

        val directionVec = Vec.fromPoint(direction).normalize()
        var currentPoint = startPoint
        val endPoint = startPoint.add(directionVec.mul(maxDistance))

        while (currentPoint.distanceSquared(startPoint) <= maxDistance * maxDistance) {
            val cell = getGridCell(currentPoint)
            val entitiesInCell = grid[cell] ?: emptyList()

            for (entity in entitiesInCell) {
                if (!hitFilter.test(entity)) continue

                val intersection = checkIntersection(entity, currentPoint, directionVec)
                if (intersection != null) {
                    hitEntities.add(entity)
                    hitPositions.add(intersection)
                }
            }

            currentPoint = currentPoint.add(directionVec.mul(GRID_SIZE))
        }

        return if (hitEntities.isEmpty()) {
            RaycastResult.HIT_NOTHING
        } else {
            RaycastResult(hitEntities.toList(), hitPositions.toList())
        }
    }

    private fun buildSpatialGrid(instance: Instance): Map<Pair<Int, Int>, List<Entity>> {
        val grid = mutableMapOf<Pair<Int, Int>, MutableList<Entity>>()

        for (entity in instance.entities) {
            val cell = getGridCell(entity.position)
            grid.computeIfAbsent(cell) { mutableListOf() }.add(entity)
        }

        return grid
    }

    private fun getGridCell(point: Point): Pair<Int, Int> {
        val x = (point.x() / GRID_SIZE).toInt()
        val z = (point.z() / GRID_SIZE).toInt()
        return Pair(x, z)
    }

    private fun checkIntersection(entity: Entity, startPoint: Point, direction: Vec): Point? {
        val area3d = Area3d.CONVERTER.from(entity.boundingBox)
        val entityPos = entity.position

        val intersection = area3d.lineIntersection(
            Vector3d.of(startPoint.x() - entityPos.x(), startPoint.y() - entityPos.y(), startPoint.z() - entityPos.z()),
            Vector3d.of(direction.x(), direction.y(), direction.z())
        )
        return intersection?.let {
            Vec(
                it.x() + entityPos.x(),
                it.y() + entityPos.y(),
                it.z() + entityPos.z()
            )
        }
    }

    fun raycast(
        instance: Instance, startPoint: Point, direction: Point, maxDistance: Double,
        hitFilter: Predicate<Entity?>
    ): RaycastResult {
        val blockRaycast = raycastBlock(instance, startPoint, direction, maxDistance)
        val entityRaycast: RaycastResult = raycastEntity(instance, startPoint, direction, maxDistance, hitFilter)

        if (entityRaycast === RaycastResult.HIT_NOTHING && blockRaycast == null) {
            return RaycastResult.HIT_NOTHING
        }

        // block raycast is always true when reached
        if (entityRaycast === RaycastResult.HIT_NOTHING) {
            return RaycastResult(emptyList(), listOf(blockRaycast!!))
        }

        // entity raycast is always true when reached
        if (blockRaycast == null) {
            return entityRaycast
        }

        // Both entity and block check have collided, time to see which is closer!
        val distanceToEntity = entityRaycast.hitPositions.firstOrNull()?.let { startPoint.distanceSquared(it) }
        val distanceToBlock = startPoint.distanceSquared(blockRaycast)

        return if (distanceToBlock > distanceToEntity!!) {
            entityRaycast
        } else {
            RaycastResult(emptyList(), listOf(blockRaycast))
        }
    }
}