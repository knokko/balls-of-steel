package balls.geometry

import fixie.*
import kotlin.math.max
import kotlin.math.min

object Geometry {

	internal fun solveClosestPointOnPlaneToPoint(plane: Rectangle, point: Position): Pair<Double, Double> {
		val rx = point.x - plane.startX
		val ry = point.y - plane.startY
		val rz = point.z - plane.startZ

		// Solve:
		// (1) plane.start + a * plane.length1 + b * plane.length2 = point
		// (2) a * plane1.length + b * plane.length2 = point - plane.start = r
		// (3) a * l1 + b * l2 = r
		// Solve with vector projection (since l1 and l2 are orthogonal):
		// a = (r dot l1) / |l1|^2
		// b = (r dot l2) / |l2|^2

		val a = (rx * plane.lengthX1 + ry * plane.lengthY1 + rz * plane.lengthZ1) / plane.length1Squared
		val b = (rx * plane.lengthX2 + ry * plane.lengthY2 + rz * plane.lengthZ2) / plane.length2Squared

		return Pair(a, b)
	}

	internal fun findClosestPointOnRectangleToPoint(plane: Rectangle, point: Position, outPointOnPlane: Position) {
		var (a, b) = solveClosestPointOnPlaneToPoint(plane, point)
		a = max(0.0, min(1.0, a))
		b = max(0.0, min(1.0, b))

		outPointOnPlane.x = plane.startX + a * plane.lengthX1 + b * plane.lengthX2
		outPointOnPlane.y = plane.startY + a * plane.lengthY1 + b * plane.lengthY2
		outPointOnPlane.z = plane.startZ + a * plane.lengthZ1 + b * plane.lengthZ2
	}

	fun distanceBetweenPointAndRectangle(
		rectangle: Rectangle, point: Position, outPointOnRectangle: Position
	): Displacement {
		findClosestPointOnRectangleToPoint(rectangle, point, outPointOnRectangle)
		return point.distance(outPointOnRectangle)
	}

	private fun solveClosestPointOnLineToPoint(lineStart: Position, lineEnd: Position, point: Position): Double {
		val dot = (lineEnd.x - lineStart.x) * (point.x - lineStart.x) +
				(lineEnd.y - lineStart.y) * (point.y - lineStart.y) +
				(lineEnd.z - lineStart.z) * (point.z - lineStart.z)

		return dot / Position.distanceSquared(lineStart, lineEnd)
	}

	internal fun distanceBetweenPointAndLineSegment(
		lineStart: Position, lineEnd: Position, point: Position, outPointOnLineSegment: Position
	): Displacement {
		val progress = max(0.0, min(1.0, solveClosestPointOnLineToPoint(lineStart, lineEnd, point)))
		outPointOnLineSegment.x = lineStart.x + progress * (lineEnd.x - lineStart.x)
		outPointOnLineSegment.y = lineStart.y + progress * (lineEnd.y - lineStart.y)
		outPointOnLineSegment.z = lineStart.z + progress * (lineEnd.z - lineStart.z)

		return point.distance(outPointOnLineSegment)
	}

	internal fun findIntersectionBetweenLineSegmentAndPlane(
		plane: Rectangle, lineStart: Position, lineEnd: Position, outIntersection: Position
	): Boolean {
		val normalX = plane.lengthY1 * plane.lengthZ2 - plane.lengthZ1 * plane.lengthY2
		val normalY = plane.lengthZ1 * plane.lengthX2 - plane.lengthX1 * plane.lengthZ2
		val normalZ = plane.lengthX1 * plane.lengthY2 - plane.lengthY1 * plane.lengthX2

		val dotStart = (lineStart.x - plane.startX) * normalX +
				(lineStart.y - plane.startY) * normalY + (lineStart.z - plane.startZ) * normalZ
		val dotEnd = (lineEnd.x - plane.startX) * normalX +
				(lineEnd.y - plane.startY) * normalY + (lineEnd.z - plane.startZ) * normalZ

		if ((dotStart.value >= 0.0 && dotEnd.value >= 0.0) || (dotStart.value <= 0.0 && dotEnd.value <= 0.0)) return false

		val progress = dotStart / (dotStart - dotEnd)
		if (progress <= 0.0 || progress >= 1.0) return false

		outIntersection.x = lineStart.x + progress * (lineEnd.x - lineStart.x)
		outIntersection.y = lineStart.y + progress * (lineEnd.y - lineStart.y)
		outIntersection.z = lineStart.z + progress * (lineEnd.z - lineStart.z)

		return true
	}

	internal fun solveClosestPointOnLineSegmentToLineSegment(
		start1: Position, end1: Position, start2: Position,
		lengthX2: Displacement, lengthY2: Displacement, lengthZ2: Displacement,
		outPoint1: Position, outPoint2: Position, fakeDistance: Displacement
	): Displacement {
		val crossX = (end1.y - start1.y) * lengthZ2 - (end1.z - start1.z) * lengthY2
		val crossY = (end1.z - start1.z) * lengthX2 - (end1.x - start1.x) * lengthZ2
		val crossZ = (end1.x - start1.x) * lengthY2 - (end1.y - start1.y) * lengthX2
		val crossSquared = crossX.value * crossX.value + crossY.value * crossY.value + crossZ.value * crossZ.value

		if (crossSquared < 1e-8) {
			val a1 = solveClosestPointOnLineToPoint(start1, end1, start2)
			if (a1 > 0.0 && a1 < 1.0) {
				outPoint1.moveTo(
					start1.x + a1 * (end1.x - start1.x),
					start1.y + a1 * (end1.y - start1.y),
					start1.z + a1 * (end1.z - start1.z)
				)
				outPoint2.moveTo(start2)
				return outPoint1.distance(outPoint2)
			}

			outPoint2.moveTo(start2.x + lengthX2, start2.y + lengthY2, start2.z + lengthZ2)
			val a2 = solveClosestPointOnLineToPoint(start1, end1, outPoint2)
			if (a2 > 0.0 && a2 < 1.0) {
				outPoint1.moveTo(
					start1.x + a2 * (end1.x - start1.x),
					start1.y + a2 * (end1.y - start1.y),
					start1.z + a2 * (end1.z - start1.z)
				)
				return outPoint1.distance(outPoint2)
			}

			if ((a1 >= 1.0 && a2 >= 1.0) || (a1 <= 0.0 && a2 <= 0.0)) return fakeDistance

			val b = solveClosestPointOnLineToPoint(start2, outPoint2, start1)
			outPoint2.moveTo(start2.x + b * lengthX2, start2.y + b * lengthY2, start2.z + b * lengthZ2)
			outPoint1.moveTo(start1)
			return outPoint1.distance(outPoint2)
		}

		// TODO Hm... I need a SquareArea class...
		val offsetX = (start2.x - start1.x).toDouble(DistanceUnit.METER)
		val offsetY = (start2.y - start1.y).toDouble(DistanceUnit.METER)
		val offsetZ = (start2.z - start1.z).toDouble(DistanceUnit.METER)

		val crossX2 = (lengthY2 * crossZ - lengthZ2 * crossY).toDouble(VolumeUnit.CUBIC_METER)
		val crossY2 = (lengthZ2 * crossX - lengthX2 * crossZ).toDouble(VolumeUnit.CUBIC_METER)
		val crossZ2 = (lengthX2 * crossY - lengthY2 * crossX).toDouble(VolumeUnit.CUBIC_METER)

		val a = (crossX2 * offsetX + crossY2 * offsetY + crossZ2 * offsetZ) / crossSquared
		if (a <= 0.0 || a >= 1.0) return fakeDistance

		val crossX1 = ((end1.y - start1.y) * crossZ - (end1.z - start1.z) * crossY).toDouble(VolumeUnit.CUBIC_METER)
		val crossY1 = ((end1.z - start1.z) * crossX - (end1.x - start1.x) * crossZ).toDouble(VolumeUnit.CUBIC_METER)
		val crossZ1 = ((end1.x - start1.x) * crossY - (end1.y - start1.y) * crossX).toDouble(VolumeUnit.CUBIC_METER)

		val b = max(0.0, min(1.0, (crossX1 * offsetX + crossY1 * offsetY + crossZ1 * offsetZ) / crossSquared))

		outPoint1.moveTo(
			start1.x + a * (end1.x - start1.x),
			start1.y + a * (end1.y - start1.y),
			start1.z + a * (end1.z - start1.z)
		)
		outPoint2.moveTo(start2.x + b * lengthX2, start2.y + b * lengthY2, start2.z + b * lengthZ2)
		return outPoint1.distance(outPoint2)
	}

	private fun checkEdge(
		lineStart: Position, lineEnd: Position, outLine: Position, outRectangle: Position,
		closestPointLine: Position, closestPointRectangle: Position, closestDistance: Displacement,
		startX: Displacement, startY: Displacement, startZ: Displacement,
		lengthX: Displacement, lengthY: Displacement, lengthZ: Displacement
	): Displacement {
		val distance = solveClosestPointOnLineSegmentToLineSegment(
			lineStart, lineEnd, Position(startX, startY, startZ),
			lengthX, lengthY, lengthZ,
			outLine, outRectangle, closestDistance
		)
		if (distance < closestDistance) {
			closestPointLine.moveTo(outLine)
			closestPointRectangle.moveTo(outRectangle)
			return distance
		}
		return closestDistance
	}

	internal fun distanceBetweenLineSegmentAndRectangle(
		rectangle: Rectangle, lineStart: Position, lineEnd: Position,
		outPointOnLineSegment: Position, outPointOnRectangle: Position
	): Displacement {

		val closestPointRectangle = Position.origin()
		val closestPointLine = Position.origin()
		var closestDistance = distanceBetweenPointAndRectangle(rectangle, lineStart, closestPointRectangle)
		closestPointLine.moveTo(lineStart)

		val endDistance = distanceBetweenPointAndRectangle(rectangle, lineEnd, outPointOnRectangle)
		if (endDistance < closestDistance) {
			closestDistance = endDistance
			closestPointLine.moveTo(lineEnd)
			closestPointRectangle.moveTo(outPointOnRectangle)
		}

		if (findIntersectionBetweenLineSegmentAndPlane(rectangle, lineStart, lineEnd, outPointOnLineSegment)) {
			val intersectionDistance = distanceBetweenPointAndRectangle(rectangle, outPointOnLineSegment, outPointOnRectangle)
			if (intersectionDistance < closestDistance) {
				closestDistance = intersectionDistance
				closestPointLine.moveTo(outPointOnLineSegment)
				closestPointRectangle.moveTo(outPointOnRectangle)
			}
		}

		closestDistance = checkEdge(
			lineStart, lineEnd, outPointOnLineSegment, outPointOnRectangle,
			closestPointLine, closestPointRectangle, closestDistance,
			rectangle.startX, rectangle.startY, rectangle.startZ,
			rectangle.lengthX1, rectangle.lengthY1, rectangle.lengthZ1
		)
		closestDistance = checkEdge(
			lineStart, lineEnd, outPointOnLineSegment, outPointOnRectangle,
			closestPointLine, closestPointRectangle, closestDistance,
			rectangle.startX, rectangle.startY, rectangle.startZ,
			rectangle.lengthX2, rectangle.lengthY2, rectangle.lengthZ2
		)
		closestDistance = checkEdge(
			lineStart, lineEnd, outPointOnLineSegment, outPointOnRectangle,
			closestPointLine, closestPointRectangle, closestDistance,
			rectangle.startX + rectangle.lengthX1,
			rectangle.startY + rectangle.lengthY1,
			rectangle.startZ + rectangle.lengthZ1,
			rectangle.lengthX2, rectangle.lengthY2, rectangle.lengthZ2
		)
		closestDistance = checkEdge(
			lineStart, lineEnd, outPointOnLineSegment, outPointOnRectangle,
			closestPointLine, closestPointRectangle, closestDistance,
			rectangle.startX + rectangle.lengthX2,
			rectangle.startY + rectangle.lengthY2,
			rectangle.startZ + rectangle.lengthZ2,
			rectangle.lengthX1, rectangle.lengthY1, rectangle.lengthZ1
		)

		outPointOnLineSegment.moveTo(closestPointLine)
		outPointOnRectangle.moveTo(closestPointRectangle)

		return closestDistance
	}

	const val SWEEP_RESULT_MISS = 0
	const val SWEEP_RESULT_DIRTY = 1
	const val SWEEP_RESULT_HIT = 2

	fun sweepSphereToRectangle(
		sx: Displacement, sy: Displacement, sz: Displacement, svx: Displacement, svy: Displacement, svz: Displacement,
		sr: Displacement, rectangle: Rectangle, outSpherePosition: Position, outPointOnRectangle: Position
	): Int {
		val fullDistance = distanceBetweenLineSegmentAndRectangle(
			rectangle, Position(sx, sy, sz), Position(sx + svx, sy + svy, sz + svz),
			outSpherePosition, outPointOnRectangle
		)

		// When the distance is larger than the radius, there is no collision
		if (fullDistance > sr + 1.mm) return SWEEP_RESULT_MISS

		val totalMovement = sqrt(svx * svx + svy * svy + svz * svz)

		// Sanity check to avoid potential endless loops
		var largestSafeDistance = distanceBetweenPointAndRectangle(rectangle, Position(sx, sy, sz), outPointOnRectangle)
		if (largestSafeDistance <= sr) {
			throw IllegalArgumentException("sphere at ($sx, $sy, $sz) with radius $sr is already in $rectangle")
		}

		val idealDistance = distanceBetweenPointAndRectangle(
			rectangle, Position(sx + svx, sy + svy, sz + svz), outPointOnRectangle
		)

		// Dirty trick
		if (idealDistance > sr && fullDistance > sr - 0.1.mm) return SWEEP_RESULT_DIRTY

		var useBinarySearch = false
		var signumCounter = 0
		var largestSafeMovement = 0.m

		val dix = outSpherePosition.x - sx
		val diy = outSpherePosition.y - sy
		val diz = outSpherePosition.z - sz
		var smallestUnsafeMovement = sqrt(dix * dix + diy * diy + diz * diz)
		var smallestUnsafeDistance = fullDistance
		var candidateMovement = smallestUnsafeMovement

		while ((smallestUnsafeMovement - largestSafeMovement) > 0.1.mm && largestSafeDistance - sr > 0.1.mm) {
			val movementFactor = candidateMovement / totalMovement
			val newX = sx + movementFactor * svx
			val newY = sy + movementFactor * svy
			val newZ = sz + movementFactor * svz
			val distance = distanceBetweenPointAndRectangle(rectangle, Position(newX, newY, newZ), outPointOnRectangle)

			if (distance > sr) {
				if (signumCounter == -1) useBinarySearch = true
				signumCounter -= 1
				if (largestSafeMovement < candidateMovement) {
					largestSafeMovement = candidateMovement
					largestSafeDistance = distance
				}
			} else {
				if (signumCounter == 1) useBinarySearch = true
				signumCounter += 1
				if (smallestUnsafeMovement > candidateMovement) {
					smallestUnsafeMovement = candidateMovement
					smallestUnsafeDistance = distance
				}
			}

			if (useBinarySearch) candidateMovement = (largestSafeMovement + smallestUnsafeMovement) / 2
			else {
				// Example:
				// - radius = 100mm
				// - largestSafeDistance = 150mm
				// - largestSafeMovement = 600mm
				// - smallestUnsafeDistance = 40mm
				// - smallestUnsafeMovement = 800mm
				//
				// - distanceFactor = (150 - 100) / (150 - 40) = 50 / 110 = 0.45
				// - candidateMovement = 600 + 0.45 * (800 - 600) = 600 + 0.45 * 200 = 690
				var distanceFactor = (largestSafeDistance - sr) / (largestSafeDistance - smallestUnsafeDistance)

				distanceFactor *= if (largestSafeDistance - sr > sr - smallestUnsafeDistance) 1.05 else 0.95
				if (distanceFactor < 0) distanceFactor = 0.0
				if (distanceFactor > 1) distanceFactor = 1.0
				candidateMovement = largestSafeMovement + distanceFactor * (smallestUnsafeMovement - largestSafeMovement)
			}
		}

		val movementFactor = largestSafeMovement / totalMovement
		outSpherePosition.moveTo(
			sx + movementFactor * svx,
			sy + movementFactor * svy,
			sz + movementFactor * svz
		)
		distanceBetweenPointAndRectangle(rectangle, outSpherePosition, outPointOnRectangle)
		return SWEEP_RESULT_HIT
	}
}
