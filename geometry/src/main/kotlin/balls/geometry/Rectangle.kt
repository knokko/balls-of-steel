package balls.geometry

import fixie.*

class Rectangle(
	val startX: Displacement,
	val startY: Displacement,
	val startZ: Displacement,
	val lengthX1: Displacement,
	val lengthY1: Displacement,
	val lengthZ1: Displacement,
	val lengthX2: Displacement,
	val lengthY2: Displacement,
	val lengthZ2: Displacement
) {

	init {
		val dotProduct = abs(lengthX1 * lengthX2 + lengthY1 * lengthY2 + lengthZ1 * lengthZ2)
		val reference1 = lengthX1 * lengthX1 + lengthY1 * lengthY1 + lengthZ1 * lengthZ1
		val reference2 = lengthX2 * lengthX2 + lengthY2 * lengthY2 + lengthZ2 * lengthZ2

		if (dotProduct > 0.01 * min(reference1, reference2)) {
			throw IllegalArgumentException("length1 must be perpendicular to length2")
		}
	}

	private fun computeMin(start: Displacement, length1: Displacement, length2: Displacement) = min(
		min(start, start + length1), min(start + length2, start + length1 + length2)
	)

	private fun computeMax(start: Displacement, length1: Displacement, length2: Displacement) = max(
		max(start, start + length1), max(start + length2, start + length1 + length2)
	)

	val minX: Displacement
		get() = computeMin(startX, lengthX1, lengthX2)
	val minY: Displacement
		get() = computeMin(startY, lengthY1, lengthY2)
	val minZ: Displacement
		get() = computeMin(startZ, lengthZ1, lengthZ2)
	val maxX: Displacement
		get() = computeMax(startX, lengthX1, lengthX2)
	val maxY: Displacement
		get() = computeMax(startY, lengthY1, lengthY2)
	val maxZ: Displacement
		get() = computeMax(startZ, lengthZ1, lengthZ2)

	val length1Squared: Area
		get() = lengthX1 * lengthX1 + lengthY1 * lengthY1 + lengthZ1 * lengthZ1
	val length2Squared: Area
		get() = lengthX2 * lengthX2 + lengthY2 * lengthY2 + lengthZ2 * lengthZ2

	override fun toString() = "PlaneSegment(start=($startX, $startY, $startZ), length1=($lengthX1, $lengthY1, $lengthZ1)," +
			" length2=($lengthX2, $lengthY2, $lengthZ2))"

	fun overlapsBounds(
		minX: Displacement, minY: Displacement, minZ: Displacement,
		maxX: Displacement, maxY: Displacement, maxZ: Displacement
	) = this.minX <= maxX && this.minY <= maxY && this.minZ <= maxZ &&
			minX <= this.maxX && minY <= this.maxY && minZ <= this.maxZ
}
