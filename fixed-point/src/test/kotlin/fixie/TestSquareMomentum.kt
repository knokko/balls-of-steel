package fixie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class TestSquareMomentum {

	private fun assertEquals(a: SquareMomentum, b: SquareMomentum, margin: Double = 0.001) {
		assertEquals(a.value, b.value, margin)
	}

	private fun assertNotEquals(a: SquareMomentum, b: SquareMomentum, margin: Double = 0.001) {
		assertNotEquals(a.value, b.value, margin)
	}

	@Test
	fun testAssertEquals() {
		assertEquals(SquareMomentum(1.23), SquareMomentum(1.23))
		assertNotEquals(SquareMomentum(1.23), SquareMomentum(1.24))
		assertEquals(SquareMomentum(1.23), SquareMomentum(1.23001))
		assertEquals(SquareMomentum(1.5), SquareMomentum(1.6), 0.2)
		assertNotEquals(SquareMomentum(1.5), SquareMomentum(1.8), 0.2)
		assertThrows<AssertionError> { assertNotEquals(SquareMomentum(1.23), SquareMomentum(1.23)) }
		assertThrows<AssertionError> { assertEquals(SquareMomentum(1.23), SquareMomentum(1.24)) }
		assertThrows<AssertionError> { assertNotEquals(SquareMomentum(1.23), SquareMomentum(1.23001)) }
		assertThrows<AssertionError> { assertNotEquals(SquareMomentum(1.5), SquareMomentum(1.6), 0.2) }
		assertThrows<AssertionError> { assertEquals(SquareMomentum(1.5), SquareMomentum(1.8), 0.2) }
	}

	@Test
	fun testToDouble() {
		assertEquals(0.234, (0.234 * SquareMomentum.SQUARE_NEWTON_SECOND).toDouble(), 2.0E-4)
	}

	@Test
	fun testToString() {
		assertEquals("2.34(Ns)^2", (SquareMomentum.SQUARE_NEWTON_SECOND * 2.34).toString())
	}

	@Test
	fun testCompareTo() {
		assertTrue(SquareMomentum.SQUARE_NEWTON_SECOND >= SquareMomentum.SQUARE_NEWTON_SECOND)
		assertTrue(SquareMomentum.SQUARE_NEWTON_SECOND <= SquareMomentum.SQUARE_NEWTON_SECOND)
		assertFalse(SquareMomentum.SQUARE_NEWTON_SECOND > SquareMomentum.SQUARE_NEWTON_SECOND)
		assertFalse(SquareMomentum.SQUARE_NEWTON_SECOND < SquareMomentum.SQUARE_NEWTON_SECOND)
		assertTrue(SquareMomentum.SQUARE_NEWTON_SECOND > SquareMomentum.SQUARE_NEWTON_SECOND * 0.8f)
		assertFalse(SquareMomentum.SQUARE_NEWTON_SECOND < SquareMomentum.SQUARE_NEWTON_SECOND * 0.8)
		assertTrue(SquareMomentum.SQUARE_NEWTON_SECOND / 2 > -SquareMomentum.SQUARE_NEWTON_SECOND)
		assertTrue(-SquareMomentum.SQUARE_NEWTON_SECOND / 2L > -SquareMomentum.SQUARE_NEWTON_SECOND)
	}

	@Test
	fun testArithmetic() {
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND / 2, SquareMomentum.SQUARE_NEWTON_SECOND - 0.5f * SquareMomentum.SQUARE_NEWTON_SECOND, 0.001)
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND / 4L + 0.75 * SquareMomentum.SQUARE_NEWTON_SECOND, 0.001)
		assertEquals(0.25, SquareMomentum.SQUARE_NEWTON_SECOND / (4 * SquareMomentum.SQUARE_NEWTON_SECOND), 0.001)
		assertEquals(-SquareMomentum.SQUARE_NEWTON_SECOND / 2, SquareMomentum.SQUARE_NEWTON_SECOND / 2 - SquareMomentum.SQUARE_NEWTON_SECOND, 0.001)
		assertEquals(2.5, ((10 * SquareMomentum.SQUARE_NEWTON_SECOND) / (4 * Momentum.NEWTON_SECOND)).toDouble(), 0.001)
	}

	@Test
	fun testExtensionFunctions() {
		assertEquals(0.8 * SquareMomentum.SQUARE_NEWTON_SECOND, 0.8.squareNs)
		assertEquals(0.6f * SquareMomentum.SQUARE_NEWTON_SECOND, 0.6f.squareNs)
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND, 1.squareNs)
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND, 1L.squareNs)
		assertEquals(0.8 * SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND * 0.8)
		assertEquals(0.3f * SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND * 0.3f)
		assertEquals(1 * SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND * 1)
		assertEquals(2L * SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND * 2L)
	}

	@Test
	fun testMathFunctions() {
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND, abs(SquareMomentum.SQUARE_NEWTON_SECOND))
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND / 2, abs(SquareMomentum.SQUARE_NEWTON_SECOND / 2))
		assertEquals(0 * SquareMomentum.SQUARE_NEWTON_SECOND, abs(0 * SquareMomentum.SQUARE_NEWTON_SECOND))
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND, abs(-SquareMomentum.SQUARE_NEWTON_SECOND))
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND / 2, min(SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND / 2))
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND, max(SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND / 2))
		assertEquals(-SquareMomentum.SQUARE_NEWTON_SECOND, min(-SquareMomentum.SQUARE_NEWTON_SECOND, -SquareMomentum.SQUARE_NEWTON_SECOND / 2))
		assertEquals(-SquareMomentum.SQUARE_NEWTON_SECOND / 2, max(-SquareMomentum.SQUARE_NEWTON_SECOND, -SquareMomentum.SQUARE_NEWTON_SECOND / 2))
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND * 0, min(SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND * 0))
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND, max(SquareMomentum.SQUARE_NEWTON_SECOND, SquareMomentum.SQUARE_NEWTON_SECOND * 0))
		assertEquals(-SquareMomentum.SQUARE_NEWTON_SECOND, min(-SquareMomentum.SQUARE_NEWTON_SECOND, -SquareMomentum.SQUARE_NEWTON_SECOND * 0))
		assertEquals(-SquareMomentum.SQUARE_NEWTON_SECOND * 0, max(-SquareMomentum.SQUARE_NEWTON_SECOND, -SquareMomentum.SQUARE_NEWTON_SECOND * 0))
		assertEquals(-SquareMomentum.SQUARE_NEWTON_SECOND, min(SquareMomentum.SQUARE_NEWTON_SECOND / 2, -SquareMomentum.SQUARE_NEWTON_SECOND))
		assertEquals(SquareMomentum.SQUARE_NEWTON_SECOND / 2, max(SquareMomentum.SQUARE_NEWTON_SECOND / 2, -SquareMomentum.SQUARE_NEWTON_SECOND))
		assertEquals(3.0, sqrt(9 * SquareMomentum.SQUARE_NEWTON_SECOND).toDouble(), 0.001)
	}
}
