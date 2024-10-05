package balls.physics.tile

import balls.geometry.Rectangle
import balls.physics.Material
import java.util.*

class TilePlaceRequest(
	val collider: Rectangle,
	val material: Material = Material.IRON
) {
	var id: UUID? = null

	@Volatile
	var processed = false
}
