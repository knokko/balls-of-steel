package balls.physics.scene

import balls.geometry.Rectangle
import balls.physics.entity.EntitySpawnRequest
import balls.physics.tile.TilePlaceRequest
import fixie.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class TestScene {

	private fun assertEquals(expected: Displacement, actual: Displacement, maxError: Displacement) {
		if (abs(expected - actual) > maxError) assertEquals(expected, actual)
	}

	private fun assertEquals(expected: Speed, actual: Speed, maxError: Speed) {
		if (abs(expected - actual) > maxError) assertEquals(expected, actual)
	}

	@Test
	fun testGravityAcceleration() {
		val scene = Scene()

		scene.spawnEntity(EntitySpawnRequest(10.m, 0.m, 123.m, radius = 1.m))

		scene.update(1.seconds)

		val query = SceneQuery()
		scene.read(query, 9.m, -10.m, 120.m, 11.m, 0.m, 125.m)

		assertEquals(1, query.entities.size)
		val subject = query.entities[0]
		assertEquals(10.m, subject.position.x)
		assertEquals(-4.9.m, subject.position.y, 100.mm)
		assertEquals(123.m, subject.position.z)
		assertEquals(0.mps, subject.velocity.x)
		assertEquals(-9.8.mps, subject.velocity.y, 0.1.mps)
		assertEquals(0.mps, subject.velocity.z)
	}

	@Test
	fun testFallOnFlatFloor() {
		val scene = Scene()
		scene.spawnEntity(EntitySpawnRequest(x = 1.m, y = 2.m, z = 3.m, radius = 100.mm))

		scene.addTile(TilePlaceRequest(collider = Rectangle(
			startX = 500.mm, startY = 1.m, startZ = 2.m,
			lengthX1 = 1.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.5.m
		)))

		scene.update(10.seconds)

		val query = SceneQuery()
		scene.read(query, 0.m, 0.m, 0.m, 5.m, 5.m, 5.m)

		assertEquals(1, query.entities.size)
		val entity = query.entities[0]
		assertEquals(1.m, entity.position.x, 1.mm)
		assertEquals(1.1.m, entity.position.y, 5.mm)
		assertEquals(1.m, entity.position.z, 3.m)
		assertEquals(0.mps, entity.velocity.x, 0.01.mps)
		assertEquals(0.mps, entity.velocity.y, 0.1.mps)
		assertEquals(0.mps, entity.velocity.z, 0.01.mps)
	}

	@Test
	fun testRollToLowestPoint() {
		val scene = Scene()

		scene.addTile(TilePlaceRequest(collider = Rectangle(
			startX = -10.m, startY = -10.m, startZ = 0.m,
			lengthX1 = 20.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 1.m, lengthZ2 = 10.m
		)))
		scene.addTile(TilePlaceRequest(collider = Rectangle(
			startX = -10.m, startY = -10.m, startZ = 0.m,
			lengthX1 = 20.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 1.m, lengthZ2 = -10.m
		)))

		scene.addTile(TilePlaceRequest(collider = Rectangle(
			startX = 0.m, startY = -10.m, startZ = -10.m,
			lengthX1 = 0.m, lengthY1 = 0.m, lengthZ1 = 20.m,
			lengthX2 = 10.m, lengthY2 = 1.m, lengthZ2 = 0.m
		)))
		scene.addTile(TilePlaceRequest(collider = Rectangle(
			startX = 0.m, startY = -10.m, startZ = -10.m,
			lengthX1 = 0.m, lengthY1 = 0.m, lengthZ1 = 20.m,
			lengthX2 = -10.m, lengthY2 = 1.m, lengthZ2 = 0.m
		)))

		scene.spawnEntity(EntitySpawnRequest(
			x = -5.m, y = -5.m, z = -5.m, radius = 200.mm
		))

		scene.update(60.seconds)

		val query = SceneQuery()
		scene.read(query, -10.m, -100.m, -10.m, 10.m, 100.m, 10.m)

		assertEquals(1, query.entities.size)
		val entity = query.entities[0]
		assertEquals(0.m, entity.position.x, 10.mm)
		assertEquals(-9.8.m, entity.position.y, 10.mm)
		assertEquals(0.m, entity.position.z, 10.mm)
		assertEquals(0.mps, entity.velocity.x, 0.1.mps)
		assertEquals(0.mps, entity.velocity.y, 0.2.mps)
		assertEquals(0.mps, entity.velocity.z, 0.1.mps)
	}
}
