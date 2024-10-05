package balls.physics.entity

import balls.geometry.Position
import balls.physics.Material
import balls.physics.Velocity
import fixie.Displacement
import fixie.Mass
import fixie.times
import java.util.*
import kotlin.math.PI

class Entity(
	val radius: Displacement,
	val material: Material = Material.IRON,
	val position: Position,
	val velocity: Velocity,
	// TODO What about angle and spin?
	val attachment: EntityAttachment = EntityAttachment()
) {
	val id: UUID = UUID.randomUUID()

	internal val normalTracker = NormalTracker()

	internal val wipPosition = Position.origin()
	internal val wipVelocity = Velocity.zero()
	internal val clusteringLists = mutableListOf<MutableList<Entity>>()
	internal var isAlreadyPresent = false

	internal val oldPosition = Position(position.x, position.y, position.z)

	val mass: Mass
		get() = PI * radius * radius * radius * material.density * 4.0 / 3.0

	override fun equals(other: Any?) = other is Entity && other.id == this.id

	override fun hashCode() = id.hashCode()
}
