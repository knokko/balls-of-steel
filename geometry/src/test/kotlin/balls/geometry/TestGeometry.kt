package balls.geometry

import fixie.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class TestGeometry {

	private fun assertEquals(expectedA: Double, expectedB: Double, actual: Pair<Double, Double>, margin: Double) {
		assertEquals(expectedA, actual.first, margin)
		assertEquals(expectedB, actual.second, margin)
	}

	private fun assertEquals(expected: Displacement, actual: Displacement, margin: Displacement) {
		assertEquals(expected.toDouble(DistanceUnit.METER), actual.toDouble(DistanceUnit.METER), margin.toDouble(DistanceUnit.METER))
	}

	private fun assertEquals(expected: Position, actual: Position, margin: Displacement) {
		assertEquals(expected.x, actual.x, margin)
		assertEquals(expected.y, actual.y, margin)
		assertEquals(expected.z, actual.z, margin)
	}

	@Test
	fun testSolveClosestPointOnPlaneToPoint() {
		val margin = 0.001
		for (y in arrayOf(-2.5.m, -0.3.m, 0.m, 0.01.m, 1.m, 1.5.m)) {
			assertEquals(
				0.3, 0.4, Geometry.solveClosestPointOnPlaneToPoint(
					PlaneSegment(
						startX = 0.m, startY = 0.m, startZ = 0.m,
						lengthX1 = 1.m, lengthY1 = 0.m, lengthZ1 = 0.m,
						lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(0.3.m, y, 0.4.m)
				), margin
			)
			assertEquals(
				0.6, 0.4, Geometry.solveClosestPointOnPlaneToPoint(
					PlaneSegment(
						startX = 0.m, startY = 3.m, startZ = 0.m,
						lengthX1 = 500.mm, lengthY1 = 0.m, lengthZ1 = 0.m,
						lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(0.3.m, y, 0.4.m)
				), margin
			)
			assertEquals(
				0.6, -2.6, Geometry.solveClosestPointOnPlaneToPoint(
					PlaneSegment(
						startX = 0.m, startY = -3.m, startZ = 3.m,
						lengthX1 = 500.mm, lengthY1 = 0.m, lengthZ1 = 0.m,
						lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(0.3.m, y, 0.4.m)
				), margin
			)
			assertEquals(
				0.3, 1.5, Geometry.solveClosestPointOnPlaneToPoint(
					PlaneSegment(
						startX = 0.m, startY = -3.m, startZ = 0.m,
						lengthX1 = 5.m, lengthY1 = 0.m, lengthZ1 = -5.m,
						lengthX2 = 1.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(3.m, y, 0.m)
				), margin
			)
		}

		assertEquals(
			2.0, -2.0, Geometry.solveClosestPointOnPlaneToPoint(
				PlaneSegment(
					startX = 0.m, startY = 15.m, startZ = 0.m,
					lengthX1 = 5.m, lengthY1 = 0.m, lengthZ1 = 0.m,
					lengthX2 = 0.m, lengthY2 = 3.m, lengthZ2 = 0.m
				), Position(10.m, 9.m, 8.m)
			), margin
		)
		assertEquals(
			4.0, -2.0, Geometry.solveClosestPointOnPlaneToPoint(
				PlaneSegment(
					startX = 0.m, startY = 15.m, startZ = 0.m,
					lengthX1 = 0.m, lengthY1 = 0.m, lengthZ1 = 2.m,
					lengthX2 = 0.m, lengthY2 = 3.m, lengthZ2 = 0.m
				), Position(10.m, 9.m, 8.m)
			), margin
		)
	}

	private fun checkClosest(expected: Position, plane: PlaneSegment, point: Position) {
		val actual = Position.origin()
		Geometry.findClosestPointOnPlaneSegmentToPoint(plane, point, actual)
		assertEquals(expected, actual, 1.mm)
	}

	@Test
	fun testFindClosestPointOnPlaneSegmentToPoint() {
		val plane1 = PlaneSegment(
			startX = 0.m, startY = 0.m, startZ = 0.m,
			lengthX1 = 3.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
		)
		checkClosest(Position.origin(), plane1, Position.origin())
		checkClosest(Position.origin(), plane1, Position(-2.m, 5.m, -4.m))
		checkClosest(Position(2.m, 0.m, 1.m), plane1, Position(2.m, -5.m, 15.m))
		checkClosest(Position(3.m, 0.m, 1.m), plane1, Position(10.m, 10.m, 10.m))

		val plane2 = PlaneSegment(
			startX = 1.m, startY = 2.m, startZ = 3.m,
			lengthX1 = 0.m, lengthY1 = -5.m, lengthZ1 = 0.m,
			lengthX2 = 1.m, lengthY2 = 0.m, lengthZ2 = 0.m
		)
		checkClosest(Position(1.5.m, 2.m, 3.m), plane2, Position(1.5.m, 3.m, 100.m))
		checkClosest(Position(1.5.m, 0.m, 3.m), plane2, Position(1.5.m, 0.m, -100.m))
	}

	@Test
	fun testDistanceBetweenPointAndPlaneSegment() {
		val plane = PlaneSegment(
			startX = 1.m, startY = 2.m, startZ = 3.m,
			lengthX1 = 0.m, lengthY1 = -5.m, lengthZ1 = 0.m,
			lengthX2 = 1.m, lengthY2 = 0.m, lengthZ2 = 0.m
		)

		val dummy = Position.origin()
		assertEquals(0.m, Geometry.distanceBetweenPointAndPlaneSegment(plane, Position(1.5.m, 1.m, 3.m), dummy))
		assertEquals(2.m, Geometry.distanceBetweenPointAndPlaneSegment(plane, Position(1.5.m, 1.m, 5.m), dummy))
		assertEquals(2.m, Geometry.distanceBetweenPointAndPlaneSegment(plane, Position(2.m, -3.m, 1.m), dummy))
		assertEquals(5.m, Geometry.distanceBetweenPointAndPlaneSegment(plane, Position(2.m, -7.m, 0.m), dummy))
	}
}
