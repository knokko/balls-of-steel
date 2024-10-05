package balls.physics.util

import fixie.*
import balls.geometry.Position
import balls.geometry.Rectangle
import balls.physics.Velocity
import balls.physics.entity.Entity
import balls.physics.scene.createMargin
import balls.physics.tile.Tile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class TestMargin {

	private fun assertEquals(a: Displacement, b: Displacement, maxError: Displacement) {
		if (abs(a - b) > maxError) assertEquals(a, b)
	}

	private fun assertEquals(a: Position, b: Position, maxError: Displacement) {
		if (a.distance(b) > maxError) assertEquals(a, b)
	}

	private fun dummyEntity(radius: Displacement, x: Displacement, y: Displacement, z: Displacement): Entity {
		val entity = Entity(
			radius = radius,
			position = Position(x, y, z),
			velocity = Velocity.zero()
		)
		entity.wipPosition.moveTo(x, y, z)
		return entity
	}

	@Test
	fun testWithoutOthers() {
		val position = Position(12.mm, 13.mm, 14.mm)

		assertFalse(createMargin(position, 100.mm, emptyList(), emptyList(), 1.mm))

		assertEquals(Position(12.mm, 13.mm, 14.mm), position, 0.mm)
	}

	@Test
	fun testWith1CloseEntity() {
		val position = Position(5.m, 2.m, 3000.1.mm)
		val other = dummyEntity(2.m, 5.m, 2.m, 0.m)

		createMargin(position, 1.m, listOf(other), emptyList(), 1.mm)

		assertEquals(Position(5.m, 2.m, 3001.mm), position, 0.2.mm)
	}

	@Test
	fun testWith2CloseEntitiesOnTheLeft() {
		val position = Position(700.mm, 12.m, 0.m)
		val upperLeft = dummyEntity(500.mm, 0.m, 12.m, 714.mm)
		val lowerLeft = dummyEntity(500.mm, 0.m, 12.m, -714.mm)

		createMargin(position, 500.mm, listOf(upperLeft, lowerLeft), emptyList(), 1.mm)

		assertEquals(1001.mm, position.distance(0.m, 12.m, 714.mm), 0.5.mm)
	}

	@Test
	fun testEntityStack() {
		val position = Position(0.m, 0.m, 2.km)
		val upper = dummyEntity(1.m, 0.m, 2001.mm, 2.km)
		val lower = dummyEntity(1.m, 0.m, -2.m, 2.km)

		createMargin(position, 1.m, listOf(upper, lower), emptyList(), 1.mm)

		assertEquals(Position(0.m, 0.5.mm, 2.km), position, 0.3.mm)
	}

	@Test
	fun testNarrowTunnel() {
		val position = Position(0.m, 0.m, 124.m)
		val upper = Tile(collider = Rectangle(
			startX = -10.m, startY = 1001.mm, startZ = 123.m,
			lengthX1 = 20.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 2.m
		))
		val lower = Tile(collider = Rectangle(
			startX = -10.m, startY = -1.m, startZ = 123.m,
			lengthX1 = 20.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 2.m
		))

		createMargin(position, 1.m, emptyList(), listOf(upper, lower), 1.mm)

		assertEquals(Position(0.m, 0.5.mm, 124.m), position, 0.3.mm)
	}
}
