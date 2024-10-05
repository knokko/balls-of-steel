package balls.physics.scene

import balls.geometry.Rectangle
import balls.physics.Material
import balls.physics.tile.Tile
import fixie.m
import kotlin.time.Duration.Companion.milliseconds

class Scene {

	companion object {
		val STEP_DURATION = 10.milliseconds

		internal val DUMMY_TILE = Tile(
			collider = Rectangle(0.m, 0.m, 0.m, 1.m, 0.m, 0.m, 0.m, 0.m, 1.m),
			material = Material.IRON
		)
	}
}
