package balls.physics

import fixie.Density
import fixie.kgpl

class Material(
	val density: Density,
	val bounceFactor: Float = 0f,
	val frictionFactor: Float = 1f
) {

	companion object {

		val IRON = Material(7.9.kgpl)
	}
}
