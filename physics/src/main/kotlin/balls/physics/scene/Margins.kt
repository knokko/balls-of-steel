package balls.physics.scene

import fixie.*
import balls.geometry.Geometry
import balls.geometry.Position
import balls.physics.entity.Entity
import balls.physics.tile.Tile

fun createMargin(
	position: Position, radius: Displacement,
	otherEntities: List<Entity>, tiles: List<Tile>, margin: Displacement
): Boolean {

	val largeMargin = 2 * margin
	val largestMargin = 3 * margin
	val veryCloseEntities = otherEntities.filter {
		val currentDistance = position.distance(it.wipPosition)
		val currentMargin = currentDistance - radius - it.radius
		currentMargin < largestMargin
	}

	val dummyPoint = Position.origin()
	val veryCloseTiles = tiles.filter {
		val currentDistance = Geometry.distanceBetweenPointAndRectangle(
			it.collider, position, dummyPoint
		)
		val currentMargin = currentDistance - radius
		currentMargin < largestMargin
	}

	var desiredMargin = margin

	for (counter in 0 until 5) {
		var newX = position.x
		var newY = position.y
		var newZ = position.z
		for (other in veryCloseEntities) {
			val dx = other.wipPosition.x - newX
			val dy = other.wipPosition.y - newY
			val dz = other.wipPosition.z - newZ
			val currentDistance = sqrt(dx * dx + dy * dy + dz * dz)
			val currentMargin = currentDistance - radius - other.radius
			if (desiredMargin > currentMargin) {
				val pushDistance = desiredMargin - currentMargin
				newX -= dx / currentDistance * pushDistance
				newY -= dy / currentDistance * pushDistance
				newZ -= dz / currentDistance * pushDistance
			}
		}
		for (tile in veryCloseTiles) {
			val currentDistance = Geometry.distanceBetweenPointAndRectangle(
				tile.collider, Position(newX, newY, newZ), dummyPoint
			)
			val currentMargin = currentDistance - radius
			if (desiredMargin > currentMargin) {
				val pushDistance = desiredMargin - currentMargin
				newX += (newX - dummyPoint.x) / currentDistance * pushDistance
				newY += (newY - dummyPoint.y) / currentDistance * pushDistance
				newZ += (newZ - dummyPoint.z) / currentDistance * pushDistance
			}
		}

		if (position.x == newX && position.y == newY && position.z == newZ) return false

		var failed = position.distance(newX, newY, newZ) > largeMargin
		for (other in veryCloseEntities) {
			val currentDistance = other.wipPosition.distance(newX, newY, newZ)
			val currentMargin = currentDistance - radius - other.radius
			if (desiredMargin * 0.6 > currentMargin) {
				failed = true
				break
			}
		}
		for (tile in veryCloseTiles) {
			val currentDistance = Geometry.distanceBetweenPointAndRectangle(
				tile.collider, Position(newX, newY, newZ), dummyPoint
			)
			val currentMargin = currentDistance - radius
			if (desiredMargin * 0.6 > currentMargin) {
				failed = true
				break
			}
		}

		if (!failed) {
			position.x = newX
			position.y = newY
			position.z = newZ
			return true
		}

		desiredMargin /= 2
	}

	return false
}
