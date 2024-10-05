package balls.physics.entity

import java.util.function.Consumer

class EntityAttachment(
	val updateFunction: Consumer<UpdateParameters>? = null
)

class UpdateParameters(
	private val entity: Entity,
) {

	var x = entity.wipPosition.x
	var y = entity.wipPosition.y
	var z = entity.wipPosition.z
	var vx = entity.wipVelocity.x
	var vy = entity.wipVelocity.y
	var vz = entity.wipVelocity.z
	// TODO Angle and spin

	fun finish() {
		entity.wipPosition.moveTo(x, y, z)
		entity.wipVelocity.x = vx
		entity.wipVelocity.y = vy
		entity.wipVelocity.z = vz
		// TODO Angle and spin
	}
}
