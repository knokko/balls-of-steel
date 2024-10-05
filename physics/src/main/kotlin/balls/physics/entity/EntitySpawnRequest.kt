package balls.physics.entity

import balls.physics.Material
import fixie.*
import java.util.*

class EntitySpawnRequest(
	val x: Displacement,
	val y: Displacement,
	val z: Displacement,
	val radius: Displacement,
	val material: Material = Material.IRON,
	val attachment: EntityAttachment = EntityAttachment(),
	val velocityX: Speed = 0.mps,
	val velocityY: Speed = 0.mps,
	val velocityZ: Speed = 0.mps
	// TODO Angle and spin
) {
	var id: UUID? = null

	@Volatile
	var processed = false
}
