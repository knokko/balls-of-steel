package balls.geometry

import fixie.Displacement
import fixie.times
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
}
