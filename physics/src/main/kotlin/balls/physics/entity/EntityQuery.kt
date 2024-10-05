package balls.physics.entity

import fixie.degrees
import balls.geometry.Position
import balls.physics.Material
import balls.physics.Velocity
import fixie.*
import java.util.*

class EntityQuery {
	lateinit var id: UUID
	internal val oldPosition = Position.origin()
	internal val currentPosition = Position.origin()
	val position = Position.origin()
	val velocity = Velocity.zero()
	// TODO Angle and spin
	var radius = 0.m
	var material = Material.IRON
}
