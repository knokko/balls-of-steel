package fixie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class TestSquareSpeed {

	private fun assertEquals(a: SquareSpeed, b: SquareSpeed, margin: Double = 0.001) {
		assertEquals(a.value, b.value, margin)
	}

	private fun assertNotEquals(a: SquareSpeed, b: SquareSpeed, margin: Double = 0.001) {
		assertNotEquals(a.value, b.value, margin)
	}

	@Test
	fun testAssertEquals() {
		assertEquals(SquareSpeed(1.23), SquareSpeed(1.23))
		assertNotEquals(SquareSpeed(1.23), SquareSpeed(1.24))
		assertEquals(SquareSpeed(1.23), SquareSpeed(1.23001))
		assertEquals(SquareSpeed(1.5), SquareSpeed(1.6), 0.2)
		assertNotEquals(SquareSpeed(1.5), SquareSpeed(1.8), 0.2)
		assertThrows<AssertionError> { assertNotEquals(SquareSpeed(1.23), SquareSpeed(1.23)) }
		assertThrows<AssertionError> { assertEquals(SquareSpeed(1.23), SquareSpeed(1.24)) }
		assertThrows<AssertionError> { assertNotEquals(SquareSpeed(1.23), SquareSpeed(1.23001)) }
		assertThrows<AssertionError> { assertNotEquals(SquareSpeed(1.5), SquareSpeed(1.6), 0.2) }
		assertThrows<AssertionError> { assertEquals(SquareSpeed(1.5), SquareSpeed(1.8), 0.2) }
	}

	@Test
	fun testToDouble() {
		assertEquals(0.234, (0.234 * SquareSpeed.SQUARE_METERS_PER_SECOND).toDouble(), 2.0E-4)
	}

	@Test
	fun testToString() {
		assertEquals("2.34(m/s)^2", (SquareSpeed.SQUARE_METERS_PER_SECOND * 2.34).toString())
	}

	@Test
	fun testCompareTo() {
		assertTrue(SquareSpeed.SQUARE_METERS_PER_SECOND >= SquareSpeed.SQUARE_METERS_PER_SECOND)
		assertTrue(SquareSpeed.SQUARE_METERS_PER_SECOND <= SquareSpeed.SQUARE_METERS_PER_SECOND)
		assertFalse(SquareSpeed.SQUARE_METERS_PER_SECOND > SquareSpeed.SQUARE_METERS_PER_SECOND)
		assertFalse(SquareSpeed.SQUARE_METERS_PER_SECOND < SquareSpeed.SQUARE_METERS_PER_SECOND)
		assertTrue(SquareSpeed.SQUARE_METERS_PER_SECOND > SquareSpeed.SQUARE_METERS_PER_SECOND * 0.8f)
		assertFalse(SquareSpeed.SQUARE_METERS_PER_SECOND < SquareSpeed.SQUARE_METERS_PER_SECOND * 0.8)
		assertTrue(SquareSpeed.SQUARE_METERS_PER_SECOND / 2 > -SquareSpeed.SQUARE_METERS_PER_SECOND)
		assertTrue(-SquareSpeed.SQUARE_METERS_PER_SECOND / 2L > -SquareSpeed.SQUARE_METERS_PER_SECOND)
	}

	@Test
	fun testArithmetic() {
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND / 2, SquareSpeed.SQUARE_METERS_PER_SECOND - 0.5f * SquareSpeed.SQUARE_METERS_PER_SECOND, 0.001)
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND / 4L + 0.75 * SquareSpeed.SQUARE_METERS_PER_SECOND, 0.001)
		assertEquals(0.25, SquareSpeed.SQUARE_METERS_PER_SECOND / (4 * SquareSpeed.SQUARE_METERS_PER_SECOND), 0.001)
		assertEquals(-SquareSpeed.SQUARE_METERS_PER_SECOND / 2, SquareSpeed.SQUARE_METERS_PER_SECOND / 2 - SquareSpeed.SQUARE_METERS_PER_SECOND, 0.001)
		assertEquals(0.25, (0.1 * SquareSpeed.SQUARE_METERS_PER_SECOND / (0.4 * Speed.METERS_PER_SECOND)).toDouble(SpeedUnit.METERS_PER_SECOND), 0.001)
	}

	@Test
	fun testExtensionFunctions() {
		assertEquals(0.8 * SquareSpeed.SQUARE_METERS_PER_SECOND, 0.8.squareMps)
		assertEquals(0.6f * SquareSpeed.SQUARE_METERS_PER_SECOND, 0.6f.squareMps)
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND, 1.squareMps)
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND, 1L.squareMps)
		assertEquals(0.8 * SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND * 0.8)
		assertEquals(0.3f * SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND * 0.3f)
		assertEquals(1 * SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND * 1)
		assertEquals(2L * SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND * 2L)
	}

	@Test
	fun testMathFunctions() {
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND, abs(SquareSpeed.SQUARE_METERS_PER_SECOND))
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND / 2, abs(SquareSpeed.SQUARE_METERS_PER_SECOND / 2))
		assertEquals(0 * SquareSpeed.SQUARE_METERS_PER_SECOND, abs(0 * SquareSpeed.SQUARE_METERS_PER_SECOND))
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND, abs(-SquareSpeed.SQUARE_METERS_PER_SECOND))
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND / 2, min(SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND / 2))
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND, max(SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND / 2))
		assertEquals(-SquareSpeed.SQUARE_METERS_PER_SECOND, min(-SquareSpeed.SQUARE_METERS_PER_SECOND, -SquareSpeed.SQUARE_METERS_PER_SECOND / 2))
		assertEquals(-SquareSpeed.SQUARE_METERS_PER_SECOND / 2, max(-SquareSpeed.SQUARE_METERS_PER_SECOND, -SquareSpeed.SQUARE_METERS_PER_SECOND / 2))
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND * 0, min(SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND * 0))
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND, max(SquareSpeed.SQUARE_METERS_PER_SECOND, SquareSpeed.SQUARE_METERS_PER_SECOND * 0))
		assertEquals(-SquareSpeed.SQUARE_METERS_PER_SECOND, min(-SquareSpeed.SQUARE_METERS_PER_SECOND, -SquareSpeed.SQUARE_METERS_PER_SECOND * 0))
		assertEquals(-SquareSpeed.SQUARE_METERS_PER_SECOND * 0, max(-SquareSpeed.SQUARE_METERS_PER_SECOND, -SquareSpeed.SQUARE_METERS_PER_SECOND * 0))
		assertEquals(-SquareSpeed.SQUARE_METERS_PER_SECOND, min(SquareSpeed.SQUARE_METERS_PER_SECOND / 2, -SquareSpeed.SQUARE_METERS_PER_SECOND))
		assertEquals(SquareSpeed.SQUARE_METERS_PER_SECOND / 2, max(SquareSpeed.SQUARE_METERS_PER_SECOND / 2, -SquareSpeed.SQUARE_METERS_PER_SECOND))
		assertEquals(0.5, sqrt(0.25 * SquareSpeed.SQUARE_METERS_PER_SECOND).toDouble(SpeedUnit.METERS_PER_SECOND), 0.001)
	}
}
