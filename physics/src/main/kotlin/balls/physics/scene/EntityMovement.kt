package balls.physics.scene

import balls.physics.tile.Tile
import balls.physics.tile.TileTree
import balls.physics.util.GrowingBuffer
import fixie.*
import balls.geometry.Geometry
import balls.geometry.Position
import balls.physics.Velocity
import balls.physics.entity.Entity
import balls.physics.entity.EntityClustering
import balls.physics.entity.Normal
import java.util.*
import kotlin.math.*
import kotlin.time.DurationUnit

internal class EntityMovement(
	private val tileTree: TileTree,
	private val entityClustering: EntityClustering
) {
	val intersections = GrowingBuffer.withMutableElements(5) { Intersection() }
	private val processedIntersections = mutableListOf<UUID>()
	private val properIntersections = mutableListOf<Intersection>()

	private val interestingTiles = mutableListOf<Tile>()
	private val queryTiles = GrowingBuffer.withImmutableElements(10, Scene.DUMMY_TILE)

	private val interestingEntities = mutableListOf<Entity>()

	private val entityIntersection = Position.origin()
	private val tileIntersection = Position.origin()

	private lateinit var entity: Entity

	private var deltaX = 0.m
	private var deltaY = 0.m
	private var deltaZ = 0.m
	var originalDelta = 0.m
	private var remainingBudget = 1.0

	private var startX = 0.m
	private var startY = 0.m
	private var startZ = 0.m

	class Intersection {
		var myX = 0.m
		var myY = 0.m
		var myZ = 0.m
		var otherX = 0.m
		var otherY = 0.m
		var otherZ = 0.m
		var radius = 0.m
		var delta = 0.m
		var bounce = 0f
		var friction = 0f
		var otherVelocity: Velocity? = null
		var otherRadius = 0.m
		var otherMass = 0.kg
		var otherID: UUID = UUID.randomUUID()

		fun validate() {
			val squaredDistance = Position.distanceSquared(otherX, otherY, otherZ, myX, myY, myZ)
			if (squaredDistance > radius * (radius * 1.1)) {
				println("invalid intersection at ($myX, $myY, $myZ) and ($otherX, $otherY, $otherZ)")
				throw RuntimeException("distance between points is $squaredDistance but radius is $radius")
			}
		}
	}

	private fun computeCurrentVelocityX() = entity.wipVelocity.x

	private fun computeCurrentVelocityY(vy: Speed = entity.wipVelocity.y) = vy - 4.9.mps2 * Scene.STEP_DURATION

	private fun computeCurrentVelocityZ() = entity.wipVelocity.z

	fun start(entity: Entity) {
		this.entity = entity
		entity.normalTracker.startTick()
		deltaX = computeCurrentVelocityX() * Scene.STEP_DURATION
		deltaY = computeCurrentVelocityY() * Scene.STEP_DURATION
		deltaZ = computeCurrentVelocityZ() * Scene.STEP_DURATION
		originalDelta = Position.distance(0.m, 0.m, 0.m, deltaX, deltaY, deltaZ)
		startX = entity.wipPosition.x
		startY = entity.wipPosition.y
		startZ = entity.wipPosition.z

		intersections.clear()
		properIntersections.clear()
		processedIntersections.clear()
		remainingBudget = 1.0
	}

	fun determineSafeRadius(entity: Entity): Displacement {
		val vx = entity.velocity.x * Scene.STEP_DURATION
		val vy = computeCurrentVelocityY(entity.velocity.y) * Scene.STEP_DURATION
		val vz = entity.velocity.z * Scene.STEP_DURATION
		val marginDistance = 1.mm
		val marginFactor = 1.1
		return marginFactor * (abs(vx) + abs(vy) + abs(vz) + marginDistance + entity.radius)
	}

	fun determineInterestingTilesAndEntities() {
		val safeRadius = determineSafeRadius(entity)
		val safeMinX = entity.position.x - safeRadius
		val safeMinY = entity.position.y - safeRadius
		val safeMinZ = entity.position.z - safeRadius
		val safeMaxX = entity.position.x + safeRadius
		val safeMaxY = entity.position.y + safeRadius
		val safeMaxZ = entity.position.z + safeRadius
		queryTiles.clear()
		interestingTiles.clear()
		tileTree.query(safeMinX, safeMinY, safeMinZ, safeMaxX, safeMaxY, safeMaxZ, queryTiles)
		for (index in 0 until queryTiles.size) {
			val tile = queryTiles[index]
			if (
				safeMinX <= tile.collider.maxX && safeMinY <= tile.collider.maxY && safeMinZ <= tile.collider.maxZ &&
				safeMaxX >= tile.collider.minX && safeMaxY >= tile.collider.minY && safeMaxZ >= tile.collider.minZ
			) {
				if (Geometry.distanceBetweenPointAndRectangle(tile.collider, entity.wipPosition, tileIntersection) < safeRadius) {
					interestingTiles.add(tile)
				}
			}
		}
		queryTiles.clear()

		entityClustering.query(entity, interestingEntities)
	}

	fun determineTileIntersections() {
		var smallDeltaX = deltaX
		var smallDeltaY = deltaY
		var smallDeltaZ = deltaZ
		var smallDeltaSquared = Position.distanceSquared(0.m, 0.m, 0.m, deltaX, deltaY, deltaZ)

		val currentInterestingTiles = ArrayList(interestingTiles)
		val nextInterestingTiles = mutableListOf<Tile>()

		while (currentInterestingTiles.isNotEmpty()) {
			val oldSmallDeltaSquared = smallDeltaSquared
			val currentDeltaX = smallDeltaX
			val currentDeltaY = smallDeltaY
			var currentDeltaZ = smallDeltaZ

			for (tile in currentInterestingTiles) {
				val sweepResult = Geometry.sweepSphereToRectangle(
					entity.wipPosition.x, entity.wipPosition.y, entity.wipPosition.z,
					currentDeltaX, currentDeltaY, currentDeltaZ, entity.radius,
					tile.collider, entityIntersection, tileIntersection
				)

				if (sweepResult == Geometry.SWEEP_RESULT_HIT) {
					val newDeltaX = entityIntersection.x - entity.wipPosition.x
					val newDeltaY = entityIntersection.y - entity.wipPosition.y
					val newDeltaZ = entityIntersection.z - entity.wipPosition.z
					val newDeltaSquared = newDeltaX * newDeltaX + newDeltaY * newDeltaY + newDeltaZ * newDeltaZ
					if (newDeltaSquared < smallDeltaSquared) {
						smallDeltaX = newDeltaX
						smallDeltaY = newDeltaY
						smallDeltaZ = newDeltaZ
						smallDeltaSquared = newDeltaSquared
					}

					val dx = entityIntersection.x - entity.wipPosition.x
					val dy = entityIntersection.y - entity.wipPosition.y
					val dz = entityIntersection.z - entity.wipPosition.z

					val intersection = intersections.add()
					intersection.myX = entityIntersection.x
					intersection.myY = entityIntersection.y
					intersection.myZ = entityIntersection.z
					intersection.otherX = tileIntersection.x
					intersection.otherY = tileIntersection.y
					intersection.otherZ = tileIntersection.z
					intersection.radius = entity.radius
					intersection.delta = sqrt(dx * dx + dy * dy + dz * dz)
					intersection.bounce = tile.material.bounceFactor
					intersection.friction = tile.material.frictionFactor
					intersection.otherVelocity = null
					intersection.otherID = tile.id

					intersection.validate()
				}
				if (sweepResult == Geometry.SWEEP_RESULT_DIRTY) nextInterestingTiles.add(tile)
			}

			if (smallDeltaSquared == oldSmallDeltaSquared) break
			currentInterestingTiles.clear()
			currentInterestingTiles.addAll(nextInterestingTiles)
			nextInterestingTiles.clear()
		}
	}

	fun determineEntityIntersections() {
		for (other in interestingEntities) {
			// TODO Entity intersections
//			if (Geometry.sweepCircleToCircle(
//					entity.wipPosition.x, entity.wipPosition.y, entity.radius, deltaX, deltaY,
//					other.wipPosition.x, other.wipPosition.y, other.radius, entityIntersection
//				)) {
//				val dx = entityIntersection.x - entity.wipPosition.x
//				val dy = entityIntersection.y - entity.wipPosition.y
//
//				val intersection = intersections.add()
//				intersection.myX = entityIntersection.x
//				intersection.myY = entityIntersection.y
//				intersection.otherX = other.wipPosition.x
//				intersection.otherY = other.wipPosition.y
//				intersection.radius = entity.radius + other.radius
//				intersection.delta = sqrt(dx * dx + dy * dy)
//				intersection.bounce = other.material.bounceFactor
//				intersection.friction = other.material.frictionFactor
//				intersection.otherVelocity = other.wipVelocity
//				intersection.otherRadius = other.radius
//				intersection.otherMass = other.mass
//				intersection.otherID = other.id
//
//				intersection.validate()
//			}
		}
	}

	fun moveSafely(allowTeleport: Boolean) {
		if (intersections.size > 0 && !allowTeleport) {
			var firstIntersection = intersections[0]
			for (index in 1 until intersections.size) {
				val otherIntersection = intersections[index]
				if (otherIntersection.delta < firstIntersection.delta) firstIntersection = otherIntersection
			}

			deltaX = firstIntersection.myX - entity.wipPosition.x
			deltaY = firstIntersection.myY - entity.wipPosition.y
			deltaZ = firstIntersection.myZ - entity.wipPosition.z
		}

		val safeDistance = determineSafeRadius(entity) - entity.radius - 1.mm
		while (true) {

			val dx = entity.wipPosition.x + deltaX - entity.position.x
			val dy = entity.wipPosition.y + deltaY - entity.position.y
			val dz = entity.wipPosition.z + deltaZ - entity.position.z
			val actualDistance = sqrt(dx * dx + dy * dy + dz * dz)

			if (actualDistance > safeDistance) {
				if (deltaX != 0.m || deltaY != 0.m || deltaZ != 0.m) {
					deltaX /= 2
					deltaY /= 2
					deltaZ /= 2
				} else throw Error("moveSafely() violated: actual distance is $actualDistance and safe distance is $safeDistance ")
			} else break
		}

		for (other in interestingEntities) {
			val dx = entity.wipPosition.x + deltaX - other.wipPosition.x
			val dy = entity.wipPosition.y + deltaY - other.wipPosition.y
			val dz = entity.wipPosition.z + deltaZ - other.wipPosition.z
			if (sqrt(dx * dx + dy * dy + dz * dz) <= entity.radius + other.radius) {
				return
			}
		}

		for (tile in interestingTiles) {
			if (Geometry.distanceBetweenPointAndRectangle(tile.collider, Position(
					entity.wipPosition.x + deltaX, entity.wipPosition.y + deltaY, entity.wipPosition.z + deltaZ
			), tileIntersection) <= entity.radius) return
		}

		entity.wipPosition.x += deltaX
		entity.wipPosition.y += deltaY
		entity.wipPosition.z += deltaZ
	}

	private fun processTileOrEntityIntersections(processTiles: Boolean) {
		val vx = computeCurrentVelocityX()
		val vy = computeCurrentVelocityY()
		val vz = computeCurrentVelocityZ()
		val speed = sqrt(vx * vx + vy * vy + vz * vz)
		val directionX = vx / speed
		val directionY = vy / speed
		val directionZ = vz / speed

		var totalIntersectionFactor = 0.0
		for (intersection in properIntersections) {
			if (intersection.otherVelocity == null == processTiles) {
				totalIntersectionFactor += determineIntersectionFactor(intersection, directionX, directionY, directionZ)
			}
		}

		if (totalIntersectionFactor > 0.0) {
			val oldVelocityX = computeCurrentVelocityX()
			val oldVelocityY = computeCurrentVelocityY()
			val oldVelocityZ = computeCurrentVelocityZ()
			for (intersection in properIntersections) {
				if (intersection.otherVelocity == null == processTiles) {
					processIntersection(
						intersection, oldVelocityX, oldVelocityY, oldVelocityZ,
						directionX, directionY, directionZ, totalIntersectionFactor
					)
				}
			}
		}
	}

	fun processIntersections() {
		if (intersections.size == 0) return

		var firstIntersection = intersections[0]
		for (index in 1 until intersections.size) {
			val otherIntersection = intersections[index]
			if (otherIntersection.delta < firstIntersection.delta) firstIntersection = otherIntersection
		}

		for (index in 0 until intersections.size) {
			val intersection = intersections[index]
			if (intersection.delta <= firstIntersection.delta + 1.mm && !processedIntersections.contains(intersection.otherID)) {
				properIntersections.add(intersection)
			}
		}

		processTileOrEntityIntersections(true)
		processTileOrEntityIntersections(false)
	}

	private fun computeEquivalentSpeed(spin: Spin): Speed {
		// momentOfInertia = 0.4 * mass * radius^2
		// applying a perpendicular clockwise force of F Newton for dt seconds at the bottom of the ball would:
		// - decrease the momentum by F * dt -> decrease the speed by F * dt / mass [m/s]
		// - increase the angular momentum by F * radius * dt
		//   -> increase the spin by F * radius * dt / moi = F * dt / (0.4 * mass * radius)
		//   = (2.5 / radius) * F * dt / mass [rad/s]
		// so a speed decrease of 1 m/s is proportional to a spin increase of 2.5 / radius rad/s
		// so a spin increase of 1 rad/s is proportional to a speed decrease of radius / 2.5 m/s
		return spin.toSpeed(entity.radius) / 2.5
	}

	fun processRotation() {
		// TODO Handle rotation
		//entity.angle += entity.spin * Scene.STEP_DURATION

		val normal = entity.normalTracker.get() ?: return

		val dx = entity.wipPosition.x - startX
		val dy = entity.wipPosition.y - startY
		val rolledDistance = -normal.y * dx + normal.x * dy

//		val expectedSpin = (rolledDistance / entity.radius / Scene.STEP_DURATION.toDouble(DurationUnit.SECONDS)).radps
//		var deltaSpin = expectedSpin - entity.spin
//
//		val maxDeltaSpin = 4000.degps * Scene.STEP_DURATION.toDouble(DurationUnit.SECONDS)
//		if (abs(deltaSpin) > maxDeltaSpin) deltaSpin = maxDeltaSpin * deltaSpin.value.sign
//
//		val consumedVelocity = computeEquivalentSpeed(deltaSpin)
//
//		entity.spin += deltaSpin
//		entity.wipVelocity.x += normal.y * consumedVelocity
//		entity.wipVelocity.y -= normal.x * consumedVelocity

		val frictionCoefficient = entity.material.frictionFactor * normal.friction
		val opposingDirectionLength = normal.x * entity.wipVelocity.x + normal.y * entity.wipVelocity.y + normal.z * entity.wipVelocity.z
		val opposingDirectionFactor = max(-1.0, min(1.0, opposingDirectionLength / entity.wipVelocity.length()))
		val frictionDirectionFactor = sqrt(1.0 - opposingDirectionFactor * opposingDirectionFactor)
		val frictionPerSecond = 0.3 * frictionCoefficient * frictionDirectionFactor
		val frictionPerTick = 1 - (1 - frictionPerSecond).pow(Scene.STEP_DURATION.toDouble(DurationUnit.SECONDS))
		entity.wipVelocity.x *= 1.0 - frictionPerTick
		entity.wipVelocity.y *= 1.0 - frictionPerTick
		entity.wipVelocity.z *= 1.0 - frictionPerTick
	}

	private fun determineIntersectionFactor(
		intersection: Intersection, directionX: Double, directionY: Double, directionZ: Double
	): Double {
		val normalX = (intersection.myX - intersection.otherX) / intersection.radius
		val normalY = (intersection.myY - intersection.otherY) / intersection.radius
		val normalZ = (intersection.myZ - intersection.otherZ) / intersection.radius

		return determineIntersectionFactor(normalX, normalY, normalZ, directionX, directionY, directionZ)
	}

	private fun determineIntersectionFactor(
		normalX: Double, normalY: Double, normalZ: Double, directionX: Double, directionY: Double, directionZ: Double
	): Double {
		return max(0.0, -directionX * normalX - directionY * normalY - directionZ * normalZ)
	}

	private fun processIntersection(
		intersection: Intersection, oldVelocityX: Speed, oldVelocityY: Speed, oldVelocityZ: Speed,
		directionX: Double, directionY: Double, directionZ: Double, totalIntersectionFactor: Double
	) {
		processedIntersections.add(intersection.otherID)

		val normalX = (intersection.myX - intersection.otherX) / intersection.radius
		val normalY = (intersection.myY - intersection.otherY) / intersection.radius
		val normalZ = (intersection.myZ - intersection.otherZ) / intersection.radius

		if (normalX * normalX + normalY * normalY + normalZ * normalZ > 1.1) {
			throw RuntimeException("No no")
		}

		val intersectionFactor = determineIntersectionFactor(
			normalX, normalY, normalZ, directionX, directionY, directionZ
		) / totalIntersectionFactor
		entity.normalTracker.registerIntersection(intersectionFactor, Normal(normalX, normalY, normalZ, intersection.friction))

		val bounceConstant = entity.material.bounceFactor + intersection.bounce + 1

		var otherVelocityX = 0.mps
		var otherVelocityY = 0.mps
		var otherVelocityZ = 0.mps
		val otherVelocity = intersection.otherVelocity
		if (otherVelocity != null) {
			otherVelocityX = otherVelocity.x
			otherVelocityY = computeCurrentVelocityY(otherVelocity.y)
			otherVelocityZ = otherVelocity.z
		}

		val relativeVelocityX = oldVelocityX - otherVelocityX
		val relativeVelocityY = oldVelocityY - otherVelocityY
		val relativeVelocityZ = oldVelocityZ - otherVelocityZ

		val opposingVelocity = bounceConstant * (normalX * oldVelocityX + normalY * oldVelocityY + normalZ * oldVelocityZ)

		val forceDirectionX = opposingVelocity * normalX
		val forceDirectionY = opposingVelocity * normalY
		val forceDirectionZ = opposingVelocity * normalZ
		var impulseX = entity.mass * intersectionFactor * forceDirectionX
		var impulseY = entity.mass * intersectionFactor * forceDirectionY
		var impulseZ = entity.mass * intersectionFactor * forceDirectionZ

		if (otherVelocity != null) {
			val thresholdFactor = 2.0
			var dimmer = 1.0

			val relativeVelocity = sqrt(
				relativeVelocityX * relativeVelocityX +
						relativeVelocityY * relativeVelocityY +
						relativeVelocityZ * relativeVelocityZ
			)
			val impulse = sqrt(impulseX * impulseX + impulseY * impulseY + impulseZ * impulseZ)

			val push = impulse / (relativeVelocity * intersection.otherMass)
			if (push > thresholdFactor) dimmer = push / thresholdFactor

			impulseX /= dimmer
			impulseY /= dimmer
			impulseZ /= dimmer

			otherVelocity.x += impulseX / intersection.otherMass
			otherVelocity.y += impulseY / intersection.otherMass
			otherVelocity.z += impulseZ / intersection.otherMass
		}

		entity.wipVelocity.x -= impulseX / entity.mass
		entity.wipVelocity.y -= impulseY / entity.mass
		entity.wipVelocity.z -= impulseZ / entity.mass
	}

	fun retry() {
		updateRetryBudget()
		retryStep()

		val oldBudget = remainingBudget
		if (oldBudget > 0.5) {
			updateRetryBudget()
			if (remainingBudget > 0.4) tryMargin()
			retryStep()
		}
	}

	private fun tryMargin() {
		entityIntersection.x = entity.wipPosition.x
		entityIntersection.y = entity.wipPosition.y
		entityIntersection.z = entity.wipPosition.z

		if (createMargin(entityIntersection, entity.radius, interestingEntities, interestingTiles, 0.2.mm)) {
			val oldTargetX = entity.wipPosition.x + deltaX
			val oldTargetY = entity.wipPosition.y + deltaY
			val oldTargetZ = entity.wipPosition.z + deltaZ
			deltaX = entityIntersection.x - entity.wipPosition.x
			deltaY = entityIntersection.y - entity.wipPosition.y
			deltaZ = entityIntersection.z - entity.wipPosition.z
			moveSafely(true)
			deltaX = oldTargetX - entity.wipPosition.x
			deltaY = oldTargetY - entity.wipPosition.y
			deltaZ = oldTargetZ - entity.wipPosition.z
		}
	}

	private fun updateRetryBudget() {
		val finalDelta = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
		remainingBudget -= finalDelta / originalDelta

		deltaX = remainingBudget * computeCurrentVelocityX() * Scene.STEP_DURATION
		deltaY = remainingBudget * computeCurrentVelocityY() * Scene.STEP_DURATION
		deltaZ = remainingBudget * computeCurrentVelocityZ() * Scene.STEP_DURATION
	}

	private fun retryStep() {
		val totalDelta = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
		if (totalDelta < 0.1.mm) return

		intersections.clear()
		properIntersections.clear()
		determineTileIntersections()
		determineEntityIntersections()
		moveSafely(false)
		processIntersections()
	}

	fun finish() {
		interestingTiles.clear()
		interestingEntities.clear()

		entity.wipVelocity.y -= 9.8.mps2 * Scene.STEP_DURATION
		entity.normalTracker.finishTick()
	}
}
