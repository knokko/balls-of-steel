package balls.geometry

import fixie.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import kotlin.math.sqrt

class TestGeometry {

	private fun assertEquals(expectedA: Double, expectedB: Double, actual: Pair<Double, Double>, margin: Double) {
		assertEquals(expectedA, actual.first, margin)
		assertEquals(expectedB, actual.second, margin)
	}

	private fun assertEquals(expected: Displacement, actual: Displacement, margin: Displacement) {
		assertEquals(
			expected.toDouble(DistanceUnit.METER),
			actual.toDouble(DistanceUnit.METER),
			margin.toDouble(DistanceUnit.METER)
		)
	}

	private fun assertEquals(expected: Position, actual: Position, margin: Displacement) {
		try {
			assertEquals(expected.x, actual.x, margin)
			assertEquals(expected.y, actual.y, margin)
			assertEquals(expected.z, actual.z, margin)
		} catch (failed: AssertionFailedError) {
			throw AssertionFailedError("Expected $expected, but got $actual")
		}
	}

	@Test
	fun testSolveClosestPointOnPlaneToPoint() {
		val margin = 0.001
		for (y in arrayOf(-2.5.m, -0.3.m, 0.m, 0.01.m, 1.m, 1.5.m)) {
			assertEquals(
				0.3, 0.4, Geometry.solveClosestPointOnPlaneToPoint(
					Rectangle(
						startX = 0.m, startY = 0.m, startZ = 0.m,
						lengthX1 = 1.m, lengthY1 = 0.m, lengthZ1 = 0.m,
						lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(0.3.m, y, 0.4.m)
				), margin
			)
			assertEquals(
				0.6, 0.4, Geometry.solveClosestPointOnPlaneToPoint(
					Rectangle(
						startX = 0.m, startY = 3.m, startZ = 0.m,
						lengthX1 = 500.mm, lengthY1 = 0.m, lengthZ1 = 0.m,
						lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(0.3.m, y, 0.4.m)
				), margin
			)
			assertEquals(
				0.6, -2.6, Geometry.solveClosestPointOnPlaneToPoint(
					Rectangle(
						startX = 0.m, startY = -3.m, startZ = 3.m,
						lengthX1 = 500.mm, lengthY1 = 0.m, lengthZ1 = 0.m,
						lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(0.3.m, y, 0.4.m)
				), margin
			)
			assertEquals(
				0.3, 1.5, Geometry.solveClosestPointOnPlaneToPoint(
					Rectangle(
						startX = 0.m, startY = -3.m, startZ = 0.m,
						lengthX1 = 5.m, lengthY1 = 0.m, lengthZ1 = -5.m,
						lengthX2 = 1.m, lengthY2 = 0.m, lengthZ2 = 1.m
					), Position(3.m, y, 0.m)
				), margin
			)
		}

		assertEquals(
			2.0, -2.0, Geometry.solveClosestPointOnPlaneToPoint(
				Rectangle(
					startX = 0.m, startY = 15.m, startZ = 0.m,
					lengthX1 = 5.m, lengthY1 = 0.m, lengthZ1 = 0.m,
					lengthX2 = 0.m, lengthY2 = 3.m, lengthZ2 = 0.m
				), Position(10.m, 9.m, 8.m)
			), margin
		)
		assertEquals(
			4.0, -2.0, Geometry.solveClosestPointOnPlaneToPoint(
				Rectangle(
					startX = 0.m, startY = 15.m, startZ = 0.m,
					lengthX1 = 0.m, lengthY1 = 0.m, lengthZ1 = 2.m,
					lengthX2 = 0.m, lengthY2 = 3.m, lengthZ2 = 0.m
				), Position(10.m, 9.m, 8.m)
			), margin
		)
	}

	private fun checkClosest(expected: Position, plane: Rectangle, point: Position) {
		val actual = Position.origin()
		Geometry.findClosestPointOnRectangleToPoint(plane, point, actual)
		assertEquals(expected, actual, 1.mm)
	}

	@Test
	fun testFindClosestPointOnRectangleToPoint() {
		val plane1 = Rectangle(
			startX = 0.m, startY = 0.m, startZ = 0.m,
			lengthX1 = 3.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
		)
		checkClosest(Position.origin(), plane1, Position.origin())
		checkClosest(Position.origin(), plane1, Position(-2.m, 5.m, -4.m))
		checkClosest(Position(2.m, 0.m, 1.m), plane1, Position(2.m, -5.m, 15.m))
		checkClosest(Position(3.m, 0.m, 1.m), plane1, Position(10.m, 10.m, 10.m))

		val plane2 = Rectangle(
			startX = 1.m, startY = 2.m, startZ = 3.m,
			lengthX1 = 0.m, lengthY1 = -5.m, lengthZ1 = 0.m,
			lengthX2 = 1.m, lengthY2 = 0.m, lengthZ2 = 0.m
		)
		checkClosest(Position(1.5.m, 2.m, 3.m), plane2, Position(1.5.m, 3.m, 100.m))
		checkClosest(Position(1.5.m, 0.m, 3.m), plane2, Position(1.5.m, 0.m, -100.m))
	}

	@Test
	fun testDistanceBetweenPointAndRectangle() {
		val plane = Rectangle(
			startX = 1.m, startY = 2.m, startZ = 3.m,
			lengthX1 = 0.m, lengthY1 = -5.m, lengthZ1 = 0.m,
			lengthX2 = 1.m, lengthY2 = 0.m, lengthZ2 = 0.m
		)

		val dummy = Position.origin()
		assertEquals(0.m, Geometry.distanceBetweenPointAndRectangle(plane, Position(1.5.m, 1.m, 3.m), dummy))
		assertEquals(2.m, Geometry.distanceBetweenPointAndRectangle(plane, Position(1.5.m, 1.m, 5.m), dummy))
		assertEquals(2.m, Geometry.distanceBetweenPointAndRectangle(plane, Position(2.m, -3.m, 1.m), dummy))
		assertEquals(5.m, Geometry.distanceBetweenPointAndRectangle(plane, Position(2.m, -7.m, 0.m), dummy))
	}

	private fun checkDistance(lineStart: Position, lineEnd: Position, point: Position, expected: Position) {
		val actual = Position.origin()
		val distance = Geometry.distanceBetweenPointAndLineSegment(lineStart, lineEnd, point, actual)
		assertEquals(expected, actual, 1.mm)
		assertEquals(distance, point.distance(expected), 1.mm)
	}

	@Test
	fun testDistanceBetweenPointAndLineSegment() {
		// Simple cases where the closest point on the line segment is the closest point on the line
		checkDistance(
			Position(1.m, 2.m, 3.m),
			Position(1.m, -8.m, 3.m),
			Position(10.m, -6.m, -2.km),
			Position(1.m, -6.m, 3.m)
		)
		checkDistance(
			Position(2.m, 3.m, 4.m),
			Position(10.m, 3.m, 4.m),
			Position(4.m, 6.m, 7.m),
			Position(4.m, 3.m, 4.m)
		)

		// The closest point on the line lies before the start of the line segment
		checkDistance(
			Position(0.m, 5.m, 10.m),
			Position(50.m, 5.m, 10.m),
			Position(-2.m, -3.m, 4.m),
			Position(0.m, 5.m, 10.m)
		)

		// The closest point on the line lies after the end of the line segment
		checkDistance(
			Position(10.m, 20.m, 30.m),
			Position(10.m, 20.m, 100.m),
			Position(3.m, 1.km, 105.m),
			Position(10.m, 20.m, 100.m)
		)

		// Last case, but the roles of start and end are reversed
		checkDistance(
			Position(10.m, 20.m, 100.m),
			Position(10.m, 20.m, 30.m),
			Position(3.m, 1.km, 105.m),
			Position(10.m, 20.m, 100.m)
		)
	}

	private fun checkIntersection(plane: Rectangle, lineStart: Position, lineEnd: Position, expected: Position?) {
		val actual = Position.origin()
		val intersected = Geometry.findIntersectionBetweenLineSegmentAndPlane(plane, lineStart, lineEnd, actual)
		if (expected != null) {
			assertTrue(intersected)
			assertEquals(expected, actual, 1.mm)
		} else assertFalse(intersected)
	}

	@Test
	fun testIntersectionBetweenLineSegmentAndPlane() {
		val plane1 = Rectangle(
			startX = 0.m, startY = 0.m, startZ = 0.m,
			lengthX1 = 3.m, lengthY1 = 0.m, lengthZ1 = 0.m,
			lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 1.m
		)

		// Parallel/overlapping lines don't intersect
		for (y in arrayOf(-5.m, 0.m, 1.m, 5.m)) {
			checkIntersection(plane1, Position(3.m, y, 5.m), Position(10.m, y, 6.m), null)
		}

		// The line is orthogonal to the plane
		checkIntersection(plane1, Position(-2.m, -5.m, -1.m), Position(1.m, 10.m, -7.m), Position(-1.m, 0.m, -3.m))
		checkIntersection(plane1, Position(-2.m, 5.m, -1.m), Position(1.m, -10.m, -7.m), Position(-1.m, 0.m, -3.m))

		// The line is orthogonal (and therefor intersects the plane), but the line SEGMENT does not
		checkIntersection(plane1, Position(-2.m, 5.m, -1.m), Position(1.m, 10.m, -7.m), null)
		checkIntersection(plane1, Position(-2.m, 10.m, -1.m), Position(1.m, 5.m, -7.m), null)
		checkIntersection(plane1, Position(-2.m, -5.m, -1.m), Position(1.m, -10.m, -7.m), null)
		checkIntersection(plane1, Position(-2.m, -10.m, -1.m), Position(1.m, -5.m, -7.m), null)

		val plane2 = Rectangle(
			startX = 1.m, startY = 2.m, startZ = 3.m,
			lengthX1 = 0.m, lengthY1 = -5.m, lengthZ1 = 0.m,
			lengthX2 = 1.m, lengthY2 = 0.m, lengthZ2 = 0.m
		)

		checkIntersection(plane2, Position(12.m, 34.m, -7.m), Position(12.m, 39.m, 43.m), Position(12.m, 35.m, 3.m))
		checkIntersection(plane2, Position(12.m, 34.m, -7.m), Position(12.m, 39.m, 2.9.m), null)
	}

	private fun checkClosest(
		rectangle: Rectangle, lineStart: Position, lineEnd: Position,
		pointOnRectangle: Position, pointOnLine: Position
	) {
		val actualPointOnRectangle = Position.origin()
		val actualPointOnLine = Position.origin()

		val actualDistance = Geometry.distanceBetweenLineSegmentAndRectangle(
			rectangle, lineStart, lineEnd, actualPointOnLine, actualPointOnRectangle
		)
		assertEquals(pointOnRectangle, actualPointOnRectangle, 1.mm)
		assertEquals(pointOnLine, actualPointOnLine, 1.mm)
		assertEquals(pointOnRectangle.distance(pointOnLine), actualDistance)
	}

	private fun checkSegments(
		start1: Position, lengthX1: Displacement, lengthY1: Displacement, lengthZ1: Displacement,
		start2: Position, lengthX2: Displacement, lengthY2: Displacement, lengthZ2: Displacement,
		expected1: Position?, expected2: Position?
	) {
		if ((expected1 == null) != (expected2 == null)) throw IllegalArgumentException()
		val fakeDistance = -12.34.m

		val actual1 = Position.origin()
		val actual2 = Position.origin()
		val actualDistance = Geometry.solveClosestPointOnLineSegmentToLineSegment(
			start1, Position(start1.x + lengthX1, start1.y + lengthY1, start1.z + lengthZ1),
			start2, lengthX2, lengthY2, lengthZ2, actual1, actual2, fakeDistance
		)

		if (expected1 == null) {
			assertEquals(fakeDistance, actualDistance)
			return
		}

		assertEquals(expected1, actual1, 1.mm)
		assertEquals(expected2!!, actual2, 1.mm)
		assertEquals(expected1.distance(expected2), actualDistance)
	}

	private fun checkParallelSegments(
		start1: Position, lengthX1: Displacement, lengthY1: Displacement, lengthZ1: Displacement,
		start2: Position, lengthX2: Displacement, lengthY2: Displacement, lengthZ2: Displacement,
		expectedDistance: Displacement?
	) {
		val fakeDistance = -12.34.m
		val actual1 = Position.origin()
		val actual2 = Position.origin()

		val end1 = Position(start1.x + lengthX1, start1.y + lengthY1, start1.z + lengthZ1)
		val end2 = Position(start2.x + lengthX2, start2.y + lengthY2, start2.z + lengthZ2)

		val actualDistance = Geometry.solveClosestPointOnLineSegmentToLineSegment(
			start1, end1, start2, lengthX2, lengthY2, lengthZ2, actual1, actual2, fakeDistance
		)

		if (expectedDistance == null) {
			assertEquals(fakeDistance, actualDistance)
			return
		}

		assertEquals(Geometry.distanceBetweenPointAndLineSegment(start1, end1, actual1, Position.origin()), 0.m, 1.mm)
		assertEquals(Geometry.distanceBetweenPointAndLineSegment(start2, end2, actual2, Position.origin()), 0.m, 1.mm)
		assertEquals(expectedDistance, actualDistance, 1.mm)
	}

	@Test
	fun testSolveClosestPointOnLineSegmentToLineSegment() {
		checkSegments(
			Position(5.m, 6.m, 7.m), 5.m, 0.m, 0.m,
			Position(7.m, 2.m, 5.m), 0.m, 100.m, 0.m,
			Position(7.m, 6.m, 7.m), Position(7.m, 6.m, 5.m)
		)
		checkSegments(
			Position(0.m, 6.m, 7.m), 5.m, 0.m, 0.m,
			Position(7.m, 2.m, 5.m), 0.m, 100.m, 0.m,
			null, null
		)
		checkSegments(
			Position(0.m, 6.m, 7.m), 5.m, 0.m, 0.m,
			Position(-7.m, 2.m, 5.m), 0.m, 100.m, 0.m,
			null, null
		)
		checkSegments(
			Position(5.m, 6.m, 7.m), 5.m, 0.m, 0.m,
			Position(7.m, 2.m, 7.m), 0.m, 1.m, 0.m,
			Position(7.m, 6.m, 7.m), Position(7.m, 3.m, 7.m)
		)

		// Parallel lines
		checkParallelSegments(
			Position(10.m, 20.m, 30.m), 0.m, 0.m, 20.m,
			Position(10.m, 30.m, -10.m), 0.m, 0.m, 100.m,
			10.m
		)
		checkParallelSegments(
			Position(10.m, 20.m, 30.m), 0.m, 0.m, 20.m,
			Position(10.m, 30.m, -10.m), 0.m, 0.m, 50.m,
			10.m
		)
		checkParallelSegments(
			Position(10.m, 20.m, 30.m), 0.m, 0.m, 20.m,
			Position(10.m, 30.m, 40.m), 0.m, 0.m, 100.m,
			10.m
		)
		checkParallelSegments(
			Position(10.m, 20.m, 30.m), 0.m, 0.m, 20.m,
			Position(10.m, 30.m, 60.m), 0.m, 0.m, 100.m,
			null
		)
		checkParallelSegments(
			Position(10.m, 20.m, 30.m), 0.m, 0.m, 20.m,
			Position(10.m, 30.m, -80.m), 0.m, 0.m, 100.m,
			null
		)
	}

	@Test
	fun testDistanceBetweenLineSegmentAndRectangle() {
		val rectangle = Rectangle(
			startX = 1.m, startY = 2.m, startZ = 3.m,
			lengthX1 = 0.m, lengthY1 = 0.m, lengthZ1 = 4.m,
			lengthX2 = 5.m, lengthY2 = 0.m, lengthZ2 = 0.m
		)

		// Closest point is line start
		checkClosest(
			rectangle, Position(2.m, 10.m, 10.m), Position(2.m, 10.m, 15.m),
			Position(2.m, 2.m, 7.m), Position(2.m, 10.m, 10.m)
		)
		checkClosest(
			rectangle, Position(2.m, 10.m, 10.m), Position(5.m, 12.m, 15.m),
			Position(2.m, 2.m, 7.m), Position(2.m, 10.m, 10.m)
		)
		checkClosest(
			rectangle, Position(0.m, 4.m, 5.m), Position(-1.m, 6.m, 4.m),
			Position(1.m, 2.m, 5.m), Position(0.m, 4.m, 5.m)
		)

		// Closest point is line end
		checkClosest(
			rectangle, Position(2.m, 10.m, 15.m), Position(2.m, 10.m, 10.m),
			Position(2.m, 2.m, 7.m), Position(2.m, 10.m, 10.m)
		)
		checkClosest(
			rectangle, Position(5.m, 12.m, 15.m), Position(2.m, 10.m, 10.m),
			Position(2.m, 2.m, 7.m), Position(2.m, 10.m, 10.m)
		)
		checkClosest(
			rectangle, Position(-1.m, 6.m, 4.m), Position(0.m, 4.m, 5.m),
			Position(1.m, 2.m, 5.m), Position(0.m, 4.m, 5.m)
		)

		// The line intersects the rectangle
		checkClosest(
			rectangle, Position(4.m, 10.m, 4.m), Position(4.m, -6.m, 4.m),
			Position(4.m, 2.m, 4.m), Position(4.m, 2.m, 4.m)
		)

		// The line is above the rectangle
		checkClosest(
			rectangle, Position(4.m, 10.m, 4.m), Position(4.m, 6.m, 4.m),
			Position(4.m, 2.m, 4.m), Position(4.m, 6.m, 4.m)
		)

		// The line is below the rectangle
		checkClosest(
			rectangle, Position(4.m, -10.m, 4.m), Position(4.m, -6.m, 4.m),
			Position(4.m, 2.m, 4.m), Position(4.m, -6.m, 4.m)
		)

		// Closest point is on the edge from (1, 2, 3) to (6, 2, 3)
		checkClosest(
			rectangle, Position(2.m, 10.m, 1.m), Position(2.m, -6.m, 1.m),
			Position(2.m, 2.m, 3.m), Position(2.m, 2.m, 1.m)
		)

		// Closest point is on the edge from (6, 2, 3) to (6, 2, 7)
		checkClosest(
			rectangle, Position(6.m, -2.m, 5.m), Position(15.m, 7.m, 5.m),
			Position(6.m, 2.m, 5.m), Position(8.m, 0.m, 5.m)
		)

		// Closest point is on the edge from (1, 2, 3) to (1, 2, 7)
		checkClosest(
			rectangle, Position(0.m, 102.m, 6.m), Position(0.m, -98.m, 4.m),
			Position(1.m, 2.m, 5.m), Position(0.m, 2.m, 5.m)
		)

		// Closest point is on the edge from (1, 2, 7) to (6, 2, 7)
		run {
			val pointOnRectangle = Position.origin()
			val pointOnLine = Position.origin()
			val distance = Geometry.distanceBetweenLineSegmentAndRectangle(
				rectangle, Position(-10.m, 3.m, 8.m), Position(10.m, 3.m, 8.m),
				pointOnLine, pointOnRectangle
			)

			assertTrue(pointOnRectangle.x >= 1.m && pointOnRectangle.x <= 7.m, "Got $pointOnRectangle")
			assertEquals(2.m, pointOnRectangle.y, 1.mm)
			assertEquals(7.m, pointOnRectangle.z, 1.mm)

			assertTrue(pointOnLine.x >= 1.m && pointOnLine.x <= 7.m, "Got $pointOnLine")
			assertEquals(3.m, pointOnLine.y, 1.mm)
			assertEquals(8.m, pointOnLine.z, 1.mm)

			assertEquals(sqrt(2.0).m, distance, 1.mm)
		}

		// Closest point is at the corner (6, 2, 7)
		checkClosest(
			rectangle, Position(2.m, 2.m, 10.m), Position(12.m, 2.m, 5.m),
			Position(6.m, 2.m, 7.m), Position(6.m, 2.m, 8.m)
		)
	}

	private fun assertSweepMiss(rectangle: Rectangle, start: Position, end: Position, radius: Displacement) {
		assertEquals(Geometry.SWEEP_RESULT_MISS, Geometry.sweepSphereToRectangle(
			start.x, start.y, start.z, end.x - start.x, end.y - start.y, end.z - start.z,
			radius, rectangle, Position.origin(), Position.origin()
		))
	}

	private fun assertSweepHit(
		rectangle: Rectangle, start: Position, end: Position, radius: Displacement,
		expectedSpherePosition: Position, expectedRectanglePosition: Position
	) {
		val actualSpherePosition = Position.origin()
		val actualRectanglePosition = Position.origin()
		assertEquals(Geometry.SWEEP_RESULT_HIT, Geometry.sweepSphereToRectangle(
			start.x, start.y, start.z, end.x - start.x, end.y - start.y, end.z - start.z,
			radius, rectangle, actualSpherePosition, actualRectanglePosition
		))
		assertEquals(expectedSpherePosition, actualSpherePosition, 1.mm)
		assertEquals(expectedRectanglePosition, actualRectanglePosition, 1.mm)
	}

	@Test
	fun testSweepSphereToRectangle() {
		val rectangle = Rectangle(
			startX = 10.m, startY = 5.m, startZ = 10.m,
			lengthX1 = 0.m, lengthY1 = 0.m, lengthZ1 = 10.m,
			lengthX2 = 20.m, lengthY2 = 0.m, lengthZ2 = 0.m
		)

		// Sweep down to the middle of the rectangle
		assertSweepMiss(rectangle, Position(15.m, 20.m, 15.m), Position(15.m, 10.m, 15.m), 1.m)
		assertSweepMiss(rectangle, Position(15.m, 20.m, 19.m), Position(15.m, 6.1.m, 19.m), 1.m)
		assertSweepHit(
			rectangle, Position(15.m, 20.m, 15.m), Position(15.m, 5.9.m, 15.m), 1.m,
			Position(15.m, 6.m, 15.m), Position(15.m, 5.m, 15.m)
		)
		assertSweepHit(
			rectangle, Position(12.m, 20.m, 15.m), Position(12.m, -15.km, 15.m), 1.m,
			Position(12.m, 6.m, 15.m), Position(12.m, 5.m, 15.m)
		)

		// Sweep up instead
		assertSweepMiss(rectangle, Position(15.m, -20.m, 15.m), Position(15.m, 0.m, 15.m), 1.m)
		assertSweepMiss(rectangle, Position(15.m, -20.m, 19.m), Position(15.m, 3.9.m, 19.m), 1.m)
		assertSweepHit(
			rectangle, Position(15.m, -20.m, 15.m), Position(15.m, 4.1.m, 15.m), 1.m,
			Position(15.m, 4.m, 15.m), Position(15.m, 5.m, 15.m)
		)
		assertSweepHit(
			rectangle, Position(12.m, -20.m, 15.m), Position(12.m, 15.km, 15.m), 1.m,
			Position(12.m, 4.m, 15.m), Position(12.m, 5.m, 15.m)
		)

		// Approach the rectangle from the negative X direction
		assertSweepMiss(rectangle, Position(0.m, 5.m, 10.m), Position(8.9.m, 5.m, 10.m), 1.m)
		assertSweepHit(
			rectangle, Position(0.m, 5.m, 10.m), Position(9.6.m, 5.m, 10.m), 410.mm,
			Position(9.59.m, 5.m, 10.m), Position(10.m, 5.m, 10.m)
		)
		assertSweepMiss(rectangle, Position(0.m, 2.m, 10.m), Position(5.9.m, 2.m, 10.m), 5.m)
		assertSweepHit(
			rectangle, Position(0.m, 2.m, 10.m), Position(6.1.m, 2.m, 10.m), 5.m,
			Position(6.m, 2.m, 10.m), Position(10.m, 5.m, 10.m)
		)
	}
}
