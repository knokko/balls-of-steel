package balls.physics.tile

import balls.physics.util.GrowingBuffer
import fixie.*
import kotlin.math.max
import kotlin.math.min

private const val SPLIT_FACTOR = 10
private const val MAX_LEAFS = 50
private val MARGIN = 10.mm

class TileTree(
	private val minX: Displacement,
	private val minY: Displacement,
	private val minZ: Displacement,
	private val maxX: Displacement,
	private val maxY: Displacement,
	private val maxZ: Displacement
) {

	private val leafs = mutableListOf<Tile>()
	private var children: Array<TileTree?>? = null

	private fun determineIndexX(x: Displacement): Int {
		val indexX = (SPLIT_FACTOR * (x - minX).value.toDouble() / (maxX - minX).value.toDouble()).toInt()
		return max(min(indexX, SPLIT_FACTOR - 1), 0)
	}

	private fun determineIndexY(y: Displacement): Int {
		val indexY = (SPLIT_FACTOR * (y - minY).value.toDouble() / (maxY - minY).value.toDouble()).toInt()
		return max(min(indexY, SPLIT_FACTOR - 1), 0)
	}

	private fun determineIndexZ(z: Displacement): Int {
		val indexZ = (SPLIT_FACTOR * (z - minZ).value.toDouble() / (maxZ - minZ).value.toDouble()).toInt()
		return max(min(indexZ, SPLIT_FACTOR - 1), 0)
	}

	private fun split() {
		children = Array(SPLIT_FACTOR * SPLIT_FACTOR * SPLIT_FACTOR) { null }
		val oldLeafs = ArrayList(leafs)
		leafs.clear()
		for (leaf in oldLeafs) insertChild(leaf)
	}

	private fun insertChild(tile: Tile) {
		val minIndexX = determineIndexX(tile.collider.minX - MARGIN)
		val minIndexY = determineIndexY(tile.collider.minY - MARGIN)
		val minIndexZ = determineIndexZ(tile.collider.minZ - MARGIN)
		val maxIndexX = determineIndexX(tile.collider.maxX + MARGIN)
		val maxIndexY = determineIndexY(tile.collider.maxY + MARGIN)
		val maxIndexZ = determineIndexZ(tile.collider.maxZ + MARGIN)

		// When a tile is relatively large compared to this tree/node, it becomes a leaf
		if ((1 + maxIndexX - minIndexX) * (1 + maxIndexY - minIndexY) * (1 + maxIndexZ - minIndexZ) >
			SPLIT_FACTOR / 2 || (maxX - minX) < -1.m) {
			leafs.add(tile)
			return
		}

		// When a tile is relatively small, it is sent to all child nodes whose bounding box overlaps the tile
		for (indexZ in minIndexZ .. maxIndexZ) {
			for (indexY in minIndexY..maxIndexY) {
				for (indexX in minIndexX..maxIndexX) {
					val childIndex = indexX + SPLIT_FACTOR * (indexY + SPLIT_FACTOR * indexZ)
					if (children!![childIndex] == null) children!![childIndex] = TileTree(
						minX = this.minX + (FixDisplacement.from(indexX) / SPLIT_FACTOR) * (this.maxX - this.minX),
						minY = this.minY + (FixDisplacement.from(indexY) / SPLIT_FACTOR) * (this.maxY - this.minY),
						minZ = this.minZ + (FixDisplacement.from(indexZ) / SPLIT_FACTOR) * (this.maxZ - this.minZ),
						maxX = this.minX + (FixDisplacement.from(indexX + 1) / SPLIT_FACTOR) * (this.maxX - this.minX),
						maxY = this.minY + (FixDisplacement.from(indexY + 1) / SPLIT_FACTOR) * (this.maxY - this.minY),
						maxZ = this.minZ + (FixDisplacement.from(indexZ + 1) / SPLIT_FACTOR) * (this.maxZ - this.minZ)
					)
					children!![childIndex]!!.insert(tile)
				}
			}
		}
	}

	fun insert(tile: Tile) {
		if (leafs.size == MAX_LEAFS && children == null) split()

		if (children == null) leafs.add(tile)
		else insertChild(tile)
	}

	fun query(
		minX: Displacement, minY: Displacement, minZ: Displacement,
		maxX: Displacement, maxY: Displacement, maxZ: Displacement, outTiles: GrowingBuffer<Tile>
	) {
		val nodes = mutableListOf(this)

		while (nodes.isNotEmpty()) {
			val node = nodes.removeLast()
			for (candidate in node.leafs) {
				if (candidate.collider.overlapsBounds(minX, minY, minZ, maxX, maxY, maxZ)) outTiles.add(candidate)
			}

			if (node.children != null) {
				val minIndexX = node.determineIndexX(minX - MARGIN)
				val minIndexY = node.determineIndexY(minY - MARGIN)
				val minIndexZ = node.determineIndexZ(minZ - MARGIN)
				val maxIndexX = node.determineIndexX(maxX + MARGIN)
				val maxIndexY = node.determineIndexY(maxY + MARGIN)
				val maxIndexZ = node.determineIndexZ(maxZ + MARGIN)
				for (indexZ in minIndexZ .. maxIndexZ) {
					for (indexY in minIndexY..maxIndexY) {
						for (indexX in minIndexX..maxIndexX) {
							val child = node.children!![indexX + SPLIT_FACTOR * (indexY + SPLIT_FACTOR * indexZ)]
							if (child != null) nodes.add(child)
						}
					}
				}
			}
		}
	}
}
