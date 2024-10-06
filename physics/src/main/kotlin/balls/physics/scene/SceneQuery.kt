package balls.physics.scene

import balls.physics.entity.EntityQuery
import balls.physics.entity.EntitySpawnRequest
import balls.physics.tile.TilePlaceRequest
import balls.physics.util.GrowingBuffer
import fixie.*
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.nanoseconds

class SceneQuery {
	val tiles = GrowingBuffer.withImmutableElements(20, Scene.DUMMY_TILE)
	val entities = GrowingBuffer.withMutableElements(10) { EntityQuery() }

	internal var lastModified = 0L

	internal var minX = 0.m
	internal var maxX = 0.m
	internal var minY = 0.m
	internal var maxY = 0.m
	internal var minZ = 0.m
	internal var maxZ = 0.m

	private fun interpolateDisplacement(oldDisplacement: Displacement, newDisplacement: Displacement, mixer: Double) =
		(1.0 - mixer) * oldDisplacement + mixer * newDisplacement

	private fun interpolateAngle(oldAngle: Angle, newAngle: Angle, mixer: Double): Angle {
		val deltaAngle = (newAngle - oldAngle).toDouble(AngleUnit.DEGREES)
		return (oldAngle.toDouble(AngleUnit.DEGREES) + mixer * deltaAngle).degrees
	}

	private fun interpolateOrExtrapolateSimple(progress: Double) {
		for (index in 0 until entities.size) {
			val entity = entities[index]
			entity.position.x = interpolateDisplacement(entity.oldPosition.x, entity.currentPosition.x, progress)
			entity.position.y = interpolateDisplacement(entity.oldPosition.y, entity.currentPosition.y, progress)
			entity.position.z = interpolateDisplacement(entity.oldPosition.z, entity.currentPosition.z, progress)

			//entity.angle = interpolateAngle(entity.oldAngle, entity.currentAngle, progress)
			// TODO Rotation
		}
	}

	fun interpolate(renderTime: Long) {
		val passedTime = (renderTime - lastModified).nanoseconds
		val progress = max(0.0, min(1.0, passedTime / Scene.STEP_DURATION))

		interpolateOrExtrapolateSimple(progress)
	}

	fun extrapolateSimple(renderTime: Long, maxSteps: Double = 1.0) {
		val passedTime = (renderTime - lastModified).nanoseconds
		val progress = 1.0 + min(maxSteps, passedTime / Scene.STEP_DURATION)

		interpolateOrExtrapolateSimple(progress)
	}

	fun extrapolateAccurately(renderTime: Long) {
		val passedTime = (renderTime - lastModified).nanoseconds
		val progress = min(1.0, passedTime / Scene.STEP_DURATION)

		val miniScene = Scene()
		for (index in 0 until tiles.size) {
			miniScene.addTile(TilePlaceRequest(collider = tiles[index].collider, material = tiles[index].material))
		}

		val spawnRequests = Array(entities.size) { index ->
			val entity = entities[index]
			val request = EntitySpawnRequest(
				x = entity.currentPosition.x,
				y = entity.currentPosition.y,
				z = entity.currentPosition.z,
				radius = entity.radius,
				material = entity.material,
				velocityX = entity.velocity.x,
				velocityY = entity.velocity.y,
				velocityZ = entity.velocity.z
			)
			miniScene.spawnEntity(request)
			request
		}

		val miniQuery = SceneQuery()
		miniScene.update(Scene.STEP_DURATION)
		miniScene.read(miniQuery, minX, minY, minZ, maxX, maxY, maxZ)
		miniQuery.interpolateOrExtrapolateSimple(progress)

		val entityMap = mutableMapOf<UUID, EntityQuery>()
		for ((index, request) in spawnRequests.withIndex()) {
			entityMap[request.id!!] = entities[index]
		}

		for (index in 0 until miniQuery.entities.size) {
			val source = miniQuery.entities[index]
			val dest = entityMap[source.id]!!

			dest.position.moveTo(source.position)
			// TODO Rotation
		}
	}
}
