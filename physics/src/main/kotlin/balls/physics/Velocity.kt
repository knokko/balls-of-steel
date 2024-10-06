package balls.physics

import fixie.*

class Velocity(
	var x: Speed,
	var y: Speed,
	var z: Speed
) {

	override fun toString() = "V($x, $y, $z)"

	fun length() = sqrt(x * x + y * y + z * z)

	fun changeTo(other: Velocity) {
		this.x = other.x
		this.y = other.y
		this.z = other.z
	}

	companion object {

		fun zero() = Velocity(0.mps, 0.mps, 0.mps)
	}
}
