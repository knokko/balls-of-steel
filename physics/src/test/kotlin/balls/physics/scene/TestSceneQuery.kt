package balls.physics.scene

import balls.geometry.Rectangle
import balls.physics.Material
import balls.physics.entity.EntitySpawnRequest
import balls.physics.tile.TilePlaceRequest
import fixie.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestSceneQuery {

	private fun prepareSimple(speed: Speed): SceneQuery {
		val scene = Scene()
		scene.spawnEntity(EntitySpawnRequest(x = 100.m, y = 0.m, z = 20.m, radius = 1.m, velocityX = speed, velocityZ = -speed))
		scene.update(Scene.STEP_DURATION)

		val query = SceneQuery()
		scene.read(query, minX = 100.m, minY = -1.m, minZ = 10.m, maxX = 110.m, maxY = 1.m, maxZ = 25.m)

		assertEquals(1, query.entities.size)
		val entity = query.entities[0]

		val movedX = speed * Scene.STEP_DURATION
		assertEquals(100.m + movedX, entity.position.x)
		assertEquals(20.m - movedX, entity.position.z)

		return query
	}

	@Test
	fun testInterpolate() {
		val speed = 10.mps
		val query = prepareSimple(speed)
		val entity = query.entities[0]

		val movedX = speed * Scene.STEP_DURATION
		query.interpolate(query.lastModified + (Scene.STEP_DURATION * 0.75).inWholeNanoseconds)

		val expectedX = (100.m + 0.75 * movedX).toDouble(DistanceUnit.MILLIMETER)
		val expectedZ = (20.m - 0.75 * movedX).toDouble(DistanceUnit.MILLIMETER)
		val actualX = entity.position.x.toDouble(DistanceUnit.MILLIMETER)
		val actualZ = entity.position.z.toDouble(DistanceUnit.MILLIMETER)
		assertEquals(expectedX, actualX, 1.0) // Allow an error of 1mm
		assertEquals(expectedZ, actualZ, 1.0)
	}

	@Test
	fun testExtrapolateSimple() {
		val speed = 10.mps
		val query = prepareSimple(speed)
		val entity = query.entities[0]

		val movedX = speed * Scene.STEP_DURATION
		query.extrapolateSimple(query.lastModified + (Scene.STEP_DURATION * 0.75).inWholeNanoseconds)

		val expectedX = (100.m + 1.75 * movedX).toDouble(DistanceUnit.MILLIMETER)
		val expectedZ = (20.m - 1.75 * movedX).toDouble(DistanceUnit.MILLIMETER)
		val actualX = entity.position.x.toDouble(DistanceUnit.MILLIMETER)
		val actualZ = entity.position.z.toDouble(DistanceUnit.MILLIMETER)
		assertEquals(expectedX, actualX, 1.0) // Allow an error of 1mm
		assertEquals(expectedZ, actualZ, 1.0)
	}

	@Test
	fun testExtrapolateAccuratelyWithSimpleCase() {
		val speed = 10.mps
		val query = prepareSimple(speed)
		val entity = query.entities[0]

		val movedX = speed * Scene.STEP_DURATION
		query.extrapolateAccurately(query.lastModified + (Scene.STEP_DURATION * 0.75).inWholeNanoseconds)

		val expectedX = (100.m + 1.75 * movedX).toDouble(DistanceUnit.MILLIMETER)
		val expectedZ = (20.m - 1.75 * movedX).toDouble(DistanceUnit.MILLIMETER)
		val actualX = entity.position.x.toDouble(DistanceUnit.MILLIMETER)
		val actualZ = entity.position.z.toDouble(DistanceUnit.MILLIMETER)
		assertEquals(expectedX, actualX, 1.0) // Allow an error of 1mm
		assertEquals(expectedZ, actualZ, 1.0)
	}

	@Test
	fun testExtrapolateAccuratelyAgainstWall() {
		val speed = 100.mps
		val scene = Scene()
		scene.spawnEntity(EntitySpawnRequest(
			x = 10.m, y = 0.m, z = 100.m, radius = 1.m, velocityX = speed,
			material = Material(density = 10.kgpl, bounceFactor = 0f)
		))
		scene.addTile(TilePlaceRequest(
			collider = Rectangle(
				startX = 11.1.m + speed * Scene.STEP_DURATION, startY = -10.m, startZ = 90.m,
				lengthX1 = 0.m, lengthY1 = 20.m, lengthZ1 = 0.m,
				lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 20.m
			),
			material = Material(density = 10.kgpl, bounceFactor = 0f)
		))
		scene.update(Scene.STEP_DURATION)

		val query = SceneQuery()
		scene.read(query, minX = 10.m, minY = -1.m, minZ = 90.m, maxX = 20.m, maxY = 1.m, maxZ = 110.m)

		assertEquals(1, query.entities.size)
		assertEquals(
			10 + (speed * Scene.STEP_DURATION).toDouble(DistanceUnit.METER),
			query.entities[0].position.x.toDouble(DistanceUnit.METER), 0.001
		)

		query.extrapolateAccurately(query.lastModified + Scene.STEP_DURATION.inWholeNanoseconds)
		assertEquals(
			10.1 + (speed * Scene.STEP_DURATION).toDouble(DistanceUnit.METER),
			query.entities[0].position.x.toDouble(DistanceUnit.METER), 0.001
		)
	}
}
