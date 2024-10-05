package balls.physics.tile

import balls.geometry.Rectangle
import balls.physics.scene.Scene
import fixie.*
import balls.physics.util.GrowingBuffer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.sqrt
import kotlin.random.Random

class TestTileTree {

	@Test
	fun testBigInsertionsDoNotCrash() {
		val tree = TileTree(minX = -1.m, minY = -1.m, minZ = -1.m, maxX = 1.m, maxY = 1.m, maxZ = 1.m)
		tree.insert(Tile(Rectangle(
			startX = -2.m, startY = -2.m, startZ = -2.m,
			lengthX1 = 5.m, lengthY1 = 5.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 5.m
		)))
		tree.insert(Tile(Rectangle(
			startX = 0.m, startY = -2.m, startZ = 0.m,
			lengthX1 = 0.m, lengthY1 = 5.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 5.m
		)))
		tree.insert(Tile(Rectangle(
			startX = -2.m, startY = 0.m, startZ = 2.m,
			lengthX1 = 5.m, lengthY1 = 0.m, lengthZ1 = 5.m,
			lengthX2 = 0.m, lengthY2 = -5.m, lengthZ2 = 0.m
		)))
	}

	private fun randomRectangle(next: () -> Int): Rectangle {
		val lengthX1 = next().mm
		val lengthY1 = next().mm
		val lengthZ1 = next().mm
		var testX = next().toDouble()
		var testY = next().toDouble()
		var testZ = next().toDouble()
		val testLength = sqrt(testX * testX + testY * testY + testZ * testZ)
		testX /= testLength
		testY /= testLength
		testZ /= testLength
		val lengthX2 = lengthY1 * testZ - lengthZ1 * testY
		val lengthY2 = lengthZ1 * testX - lengthX1 * testZ
		val lengthZ2 = lengthX1 * testY - lengthY1 * testX
		return Rectangle(
			startX = next().mm,
			startY = next().mm,
			startZ = next().mm,
			lengthX1 = lengthX1,
			lengthY1 = lengthY1,
			lengthZ1 = lengthZ1,
			lengthX2 = lengthX2,
			lengthY2 = lengthY2,
			lengthZ2 = lengthZ2
		)
	}

	@Test
	fun testRandomInsertionPerformance() {
		val tree = TileTree(minX = -1.m, minY = -1.m, minZ = -1.m, maxX = 1.m, maxY = 1.m, maxZ = 1.m)
		val rng = Random(1234)

		val startTime = System.nanoTime()
		for (counter in 0 until 100_000) {
			tree.insert(Tile(randomRectangle { rng.nextInt(-1000, 1000) }))
		}
		val took = System.nanoTime() - startTime
		if (took > 5_000_000_000) {
			throw AssertionError("Expected to finish within 5 seconds, but took ${took / 1_000_000_000L} seconds")
		}
	}

	@Test
	fun testRandomIntersectionCorrectness() {
		val rng = Random(1234)
		val tree = TileTree(minX = -10.km, minY = -10.km, minZ = -10.km, maxX = 10.km, maxY = 10.km, maxZ = 10.km)

		fun next() = rng.nextInt(-100, 100) * rng.nextInt(-100, 100) * rng.nextInt(-100, 100)
		val tiles = Array(1000) {
			Tile(randomRectangle(::next))
		}

		fun check(tile: Tile) {

			fun check(
				minX: Displacement, minY: Displacement, minZ: Displacement,
				maxX: Displacement, maxY: Displacement, maxZ: Displacement
			) {
				val testList = GrowingBuffer.withImmutableElements(100, Scene.DUMMY_TILE)
				tree.query(minX, minY, minZ, maxX, maxY, maxZ, testList)
				assertTrue(testList.contains(tile))
			}

			val rect = tile.collider
			check(rect.minX, rect.minY, rect.minZ, rect.maxX, rect.maxY, rect.maxZ)
			check(
				rect.minX - 1.mm, rect.minY - 1.mm, rect.minZ - 1.mm,
				rect.maxX + 1.mm, rect.maxY + 1.mm, rect.maxZ + 1.mm
			)

			val midX = (rect.minX + rect.maxX) / 2
			val midY = (rect.minY + rect.maxY) / 2
			val midZ = (rect.minZ + rect.maxZ) / 2
			check(midX, midY, midZ, rect.maxX, rect.maxY, rect.maxZ)
			check(rect.minX, rect.minY, rect.minZ, midX, midY, midZ)
			check(
				(midX + rect.minX) / 2, (midY + rect.minY) / 2, (midZ + rect.minZ) / 2,
				(midX + rect.maxX) / 2, (midY + rect.maxY) / 2, (midZ + rect.maxZ) / 2
			)

			check(rect.minX - 1.m, rect.minY - 1.m, rect.minZ - 1.m, rect.minX, rect.minY, rect.minZ)
			check(rect.maxX, rect.maxY, rect.maxZ, rect.maxX + 1.m, rect.maxY + 1.m, rect.maxZ + 1.m)
		}

		for (tile in tiles) {
			tree.insert(tile)
			check(tile)
		}

		for (tile in tiles) check(tile)
	}
}
