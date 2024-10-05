package balls.physics.tile

import balls.geometry.Rectangle
import balls.physics.Material
import java.util.*

class Tile(
	val collider: Rectangle,
	val material: Material = Material.IRON
) {
	val id = UUID.randomUUID()
}
