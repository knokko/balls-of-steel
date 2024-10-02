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

	internal fun distanceBetweenPointAndRectangle(
		plane: Rectangle, point: Position, outPointOnPlane: Position
	): Displacement {
		findClosestPointOnRectangleToPoint(plane, point, outPointOnPlane)
		return point.distance(outPointOnPlane)
	}

	internal fun distanceBetweenPointAndLineSegment(
		lineStart: Position, lineEnd: Position, point: Position, outPointOnLineSegment: Position
	): Displacement {
		val dot = (lineEnd.x - lineStart.x) * (point.x - lineStart.x) +
				(lineEnd.y - lineStart.y) * (point.y - lineStart.y) +
				(lineEnd.z - lineStart.z) * (point.z - lineStart.z)

		val progress = max(0.0, min(1.0, dot / Position.distanceSquared(lineStart, lineEnd)))
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
}
