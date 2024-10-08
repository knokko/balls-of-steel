package balls.geometry

import fixie.*

class Position(
	var x: Displacement,
	var y: Displacement,
	var z: Displacement
) {

	fun moveTo(destination: Position) {
		moveTo(destination.x, destination.y, destination.z)
	}

	fun moveTo(newX: Displacement, newY: Displacement, newZ: Displacement) {
		this.x = newX
		this.y = newY
		this.z = newZ
	}

	override fun toString() = "($x, $y, $z)"

	override fun equals(other: Any?) = other is Position && this.x == other.x && this.y == other.y && this.z == other.z

	override fun hashCode() = x.hashCode() - 13 * y.hashCode() + 127 * z.hashCode()

	fun distance(otherX: Displacement, otherY: Displacement, otherZ: Displacement) = distance(
		this.x, this.y, this.z, otherX, otherY, otherZ
	)

	fun distance(other: Position) = distance(other.x, other.y, other.z)

	companion object {

		fun origin() = Position(0.m, 0.m, 0.m)

		fun distanceSquared(p1: Position, p2: Position) = distanceSquared(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)

		fun distanceSquared(
			x1: Displacement, y1: Displacement, z1: Displacement,
			x2: Displacement, y2: Displacement, z2: Displacement
		): Area {
			val dx = x2 - x1
			val dy = y2 - y1
			val dz = z2 - z1
			return dx * dx + dy * dy + dz * dz
		}

		fun distance(
			x1: Displacement, y1: Displacement, z1: Displacement,
			x2: Displacement, y2: Displacement, z2: Displacement
		) = sqrt(distanceSquared(x1, y1, z1, x2, y2, z2))
	}
}
