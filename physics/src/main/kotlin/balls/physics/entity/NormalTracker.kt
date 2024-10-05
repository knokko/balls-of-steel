package balls.physics.entity

import balls.physics.scene.Scene
import kotlin.time.Duration.Companion.milliseconds

class Normal(val x: Double, val y: Double, val z: Double, val friction: Float)

internal class NormalTracker {

	private var normal: Normal? = null
	private var weight = 0.0
	private var age = 0.milliseconds

	fun startTick() {
		this.weight = 0.0
	}

	fun registerIntersection(weight: Double, normal: Normal) {
		if (weight > this.weight) {
			this.weight = weight
			this.normal = normal
			this.age = 0.milliseconds
		}
	}

	fun get() = normal

	fun finishTick() {
		this.age += Scene.STEP_DURATION
		if (this.age > 100.milliseconds) {
			this.normal = null
			this.age = 0.milliseconds
		}
	}
}
