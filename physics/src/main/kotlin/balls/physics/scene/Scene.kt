package balls.physics.scene

import balls.geometry.Geometry
import balls.geometry.Position
import balls.geometry.Rectangle
import balls.physics.Material
import balls.physics.Velocity
import balls.physics.entity.Entity
import balls.physics.entity.EntityClustering
import balls.physics.entity.EntitySpawnRequest
import balls.physics.entity.UpdateParameters
import balls.physics.tile.Tile
import balls.physics.tile.TilePlaceRequest
import balls.physics.tile.TileTree
import balls.physics.util.GrowingBuffer
import fixie.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Scene {

	private var remainingTime = 0.milliseconds
	private var lastUpdateTime = 0L

	private var updateThread: Thread? = null

	private val tiles = mutableListOf<Tile>()
	private val tileTree = TileTree(
		minX = -LIMIT,
		minY = -LIMIT,
		minZ = -LIMIT,
		maxX = LIMIT,
		maxY = LIMIT,
		maxZ = LIMIT
	)
	private val entities = mutableListOf<Entity>()
	private val entityClustering = EntityClustering()

	private val entitiesToSpawn = ConcurrentLinkedQueue<EntitySpawnRequest>()
	private val tilesToPlace = ConcurrentLinkedQueue<TilePlaceRequest>()

	private fun copyStateBeforeUpdate() {
		synchronized(this) {
			if (updateThread != null) throw IllegalStateException("Already updating")

			var index = 0
			for (entity in entities) {
				entity.wipPosition.moveTo(entity.position)
				entity.wipVelocity.changeTo(entity.velocity)
				// TODO Angle?
				index += 1
			}
		}
	}

	private val spawnIntersection = Position.origin()
	private val queryTiles = GrowingBuffer.withImmutableElements(50, DUMMY_TILE)

	private fun canSpawn(x: Displacement, y: Displacement, z: Displacement, radius: Displacement): Boolean {
		val safeRadius = 2 * radius
		tileTree.query(
			x - safeRadius, y - safeRadius, z - safeRadius,
			x + safeRadius, y + safeRadius, z + safeRadius, queryTiles
		)
		for (index in 0 until queryTiles.size) {
			if (Geometry.distanceBetweenPointAndRectangle(
				queryTiles[index].collider, Position(x, y, z), spawnIntersection
			) <= radius) return false
		}
		queryTiles.clear()

		for (entity in entities) {
			val combinedRadius = radius + entity.radius
			if (Position.distanceSquared(
					entity.position.x, entity.position.y, entity.position.z, x, y, z
			) <= combinedRadius * combinedRadius) return false
		}

		return true
	}

	private fun processEntitySpawnRequests() {
		do {
			val request = entitiesToSpawn.poll()
			if (request != null) {
				if (canSpawn(request.x, request.y, request.z, request.radius)) {
					val entity = Entity(
						radius = request.radius,
						material = request.material,
						position = Position(request.x, request.y, request.z),
						velocity = Velocity(request.velocityX, request.velocityY, request.velocityZ),
						attachment = request.attachment
					)
					// TODO Constraints?
					//entity.constraints.add(NotMovingConstraint(200.milliseconds))
					entities.add(entity)
					request.id = entity.id
				}
				request.processed = true
			}
		} while (request != null)
	}

	private val tileIntersection = Position.origin()

	private fun canPlace(collider: Rectangle): Boolean {
		for (entity in entities) {
			if (Geometry.distanceBetweenPointAndRectangle(
					collider, entity.position, tileIntersection
				) <= entity.radius) return false
		}

		return true
	}

	private fun processTilePlaceRequests() {
		do {
			val request = tilesToPlace.poll()
			if (request != null) {
				if (canPlace(request.collider)) {
					val tile = Tile(
						collider = request.collider,
						material = request.material
					)
					tileTree.insert(tile)
					tiles.add(tile)
					request.id = tile.id
				}
				request.processed = true
			}
		} while (request != null)
	}

	private fun processRequests() {
		synchronized(this) {
			processEntitySpawnRequests()
			processTilePlaceRequests()
			lastUpdateTime = System.nanoTime()
		}
	}

	private fun copyStateAfterUpdate() {
		synchronized(this) {
			lastUpdateTime = System.nanoTime()

			var index = 0
			for (entity in entities) {
				entity.oldPosition.moveTo(entity.position)
				entity.position.moveTo(entity.wipPosition)
				entity.velocity.changeTo(entity.wipVelocity)
				index += 1
			}

			if (entities.removeIf { abs(it.position.x) > LIMIT || abs(it.position.y) > LIMIT || abs(it.position.z) > LIMIT }) {
				println("destroyed an entity")
			}

			updateThread = null
		}
	}

	private val movement = EntityMovement(tileTree, entityClustering)

	private fun updateEntity(entity: Entity) {
		movement.start(entity)

		movement.determineInterestingTilesAndEntities()
		movement.determineTileIntersections()
		movement.determineEntityIntersections()

		movement.moveSafely(false)
		movement.processIntersections()

		if (movement.intersections.size > 0 && movement.originalDelta > 0.1.mm) movement.retry()

		movement.processRotation()
		movement.finish()
	}

	private fun updateEntities() {
		for (entity in entities) {
			entityClustering.insert(entity, movement.determineSafeRadius(entity))

			// TODO Constraints?
//			for (constraint in entity.constraints) {
//				val updateParameters = UpdateParameters(entity)
//				constraint.check(updateParameters)
//				updateParameters.finish()
//			}
		}

		for (entity in entities) {
			updateEntity(entity)
			if (entity.attachment.updateFunction != null) {
				val parameters = UpdateParameters(entity)
				entity.attachment.updateFunction.accept(parameters)
				parameters.finish()
			}
		}

		entityClustering.reset()
	}

	fun update(duration: Duration) {
		processRequests()
		remainingTime += duration

		while (remainingTime >= STEP_DURATION) {
			copyStateBeforeUpdate()
			updateEntities()
			copyStateAfterUpdate()
			remainingTime -= STEP_DURATION
		}
	}

	fun spawnEntity(request: EntitySpawnRequest) {
		entitiesToSpawn.add(request)
	}

	fun addTile(request: TilePlaceRequest) {
		tilesToPlace.add(request)
	}

	fun read(
		query: SceneQuery, minX: Displacement, minY: Displacement, minZ: Displacement,
		maxX: Displacement, maxY: Displacement, maxZ: Displacement
	) {
		synchronized(this) {
			if (
				query.lastModified == lastUpdateTime && query.minX == minX &&
				query.minY == minY && query.minZ == minZ && query.maxX == maxX && query.maxY == maxY && query.maxZ == maxZ
			) return

			query.lastModified = lastUpdateTime

			query.minX = minX
			query.minY = minY
			query.minZ = minZ
			query.maxX = maxX
			query.maxY = maxY
			query.maxZ = maxZ

			query.tiles.clear()
			tileTree.query(minX, minY, minZ, maxX, maxY, maxZ, query.tiles)

			query.entities.clear()
			for (entity in entities) {
				val p = entity.position
				val r = entity.radius
				if (
					p.x + r >= minX && p.y + r >= minY && p.z + r >= minZ &&
					p.x - r <= maxX && p.y - r <= maxY && p.z - r <= maxZ
				) {
					val qe = query.entities.add()

					qe.id = entity.id
					qe.radius = entity.radius
					qe.material = entity.material
					qe.oldPosition.moveTo(entity.oldPosition)
					qe.currentPosition.moveTo(p)
					qe.position.moveTo(p)
					qe.velocity.changeTo(entity.velocity)
					// TODO rotation
				}
			}
		}
	}

	companion object {
		val STEP_DURATION = 10.milliseconds

		internal val DUMMY_TILE = Tile(
			collider = Rectangle(0.m, 0.m, 0.m, 1.m, 0.m, 0.m, 0.m, 0.m, 1.m),
			material = Material.IRON
		)

		private val LIMIT = 10.km
	}
}
