package balls.physics

import fixie.*

class Velocity(
	var x: Speed,
	var y: Speed,
	var z: Speed
) {

	override fun toString() = "V($x, $y, $z)"

	fun length() = sqrt(x * x + y * y + z * z)

	companion object {

		fun zero() = Velocity(0.mps, 0.mps, 0.mps)
	}
}
