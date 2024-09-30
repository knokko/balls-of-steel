package fixie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class TestMomentum {

	private fun assertEquals(a: Momentum, b: Momentum, margin: Double = 0.001) {
		assertEquals(a.value, b.value, margin)
	}

	private fun assertNotEquals(a: Momentum, b: Momentum, margin: Double = 0.001) {
		assertNotEquals(a.value, b.value, margin)
	}

	@Test
	fun testAssertEquals() {
		assertEquals(Momentum(1.23), Momentum(1.23))
		assertNotEquals(Momentum(1.23), Momentum(1.24))
		assertEquals(Momentum(1.23), Momentum(1.23001))
		assertEquals(Momentum(1.5), Momentum(1.6), 0.2)
		assertNotEquals(Momentum(1.5), Momentum(1.8), 0.2)
		assertThrows<AssertionError> { assertNotEquals(Momentum(1.23), Momentum(1.23)) }
		assertThrows<AssertionError> { assertEquals(Momentum(1.23), Momentum(1.24)) }
		assertThrows<AssertionError> { assertNotEquals(Momentum(1.23), Momentum(1.23001)) }
		assertThrows<AssertionError> { assertNotEquals(Momentum(1.5), Momentum(1.6), 0.2) }
		assertThrows<AssertionError> { assertEquals(Momentum(1.5), Momentum(1.8), 0.2) }
	}

	@Test
	fun testToDouble() {
		assertEquals(0.234, (0.234 * Momentum.NEWTON_SECOND).toDouble(), 2.0E-4)
	}

	@Test
	fun testToString() {
		assertEquals("2.34Ns", (Momentum.NEWTON_SECOND * 2.34).toString())
	}

	@Test
	fun testCompareTo() {
		assertTrue(Momentum.NEWTON_SECOND >= Momentum.NEWTON_SECOND)
		assertTrue(Momentum.NEWTON_SECOND <= Momentum.NEWTON_SECOND)
		assertFalse(Momentum.NEWTON_SECOND > Momentum.NEWTON_SECOND)
		assertFalse(Momentum.NEWTON_SECOND < Momentum.NEWTON_SECOND)
		assertTrue(Momentum.NEWTON_SECOND > Momentum.NEWTON_SECOND * 0.8f)
		assertFalse(Momentum.NEWTON_SECOND < Momentum.NEWTON_SECOND * 0.8)
		assertTrue(Momentum.NEWTON_SECOND / 2 > -Momentum.NEWTON_SECOND)
		assertTrue(-Momentum.NEWTON_SECOND / 2L > -Momentum.NEWTON_SECOND)
	}

	@Test
	fun testArithmetic() {
		assertEquals(Momentum.NEWTON_SECOND / 2, Momentum.NEWTON_SECOND - 0.5f * Momentum.NEWTON_SECOND, 0.001)
		assertEquals(Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND / 4L + 0.75 * Momentum.NEWTON_SECOND, 0.001)
		assertEquals(0.25, Momentum.NEWTON_SECOND / (4 * Momentum.NEWTON_SECOND), 0.001)
		assertEquals(-Momentum.NEWTON_SECOND / 2, Momentum.NEWTON_SECOND / 2 - Momentum.NEWTON_SECOND, 0.001)
		assertEquals(0.8, ((Momentum.NEWTON_SECOND * 1.6) / (2 * Mass.KILOGRAM)).toDouble(SpeedUnit.METERS_PER_SECOND), 0.001)
		assertEquals(1.5, (0.75 * Momentum.NEWTON_SECOND / (0.5 * Speed.METERS_PER_SECOND)).toDouble(MassUnit.KILOGRAM), 0.001)
		assertEquals(2.4, ((Momentum.NEWTON_SECOND * 1.2) * (Momentum.NEWTON_SECOND * 2)).toDouble(), 0.001)
	}

	@Test
	fun testExtensionFunctions() {
		assertEquals(0.8 * Momentum.NEWTON_SECOND, 0.8.newSec)
		assertEquals(0.6f * Momentum.NEWTON_SECOND, 0.6f.newSec)
		assertEquals(Momentum.NEWTON_SECOND, 1.newSec)
		assertEquals(Momentum.NEWTON_SECOND, 1L.newSec)
		assertEquals(0.8 * Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND * 0.8)
		assertEquals(0.3f * Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND * 0.3f)
		assertEquals(1 * Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND * 1)
		assertEquals(2L * Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND * 2L)
	}

	@Test
	fun testMathFunctions() {
		assertEquals(Momentum.NEWTON_SECOND, abs(Momentum.NEWTON_SECOND))
		assertEquals(Momentum.NEWTON_SECOND / 2, abs(Momentum.NEWTON_SECOND / 2))
		assertEquals(0 * Momentum.NEWTON_SECOND, abs(0 * Momentum.NEWTON_SECOND))
		assertEquals(Momentum.NEWTON_SECOND, abs(-Momentum.NEWTON_SECOND))
		assertEquals(Momentum.NEWTON_SECOND / 2, min(Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND / 2))
		assertEquals(Momentum.NEWTON_SECOND, max(Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND / 2))
		assertEquals(-Momentum.NEWTON_SECOND, min(-Momentum.NEWTON_SECOND, -Momentum.NEWTON_SECOND / 2))
		assertEquals(-Momentum.NEWTON_SECOND / 2, max(-Momentum.NEWTON_SECOND, -Momentum.NEWTON_SECOND / 2))
		assertEquals(Momentum.NEWTON_SECOND * 0, min(Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND * 0))
		assertEquals(Momentum.NEWTON_SECOND, max(Momentum.NEWTON_SECOND, Momentum.NEWTON_SECOND * 0))
		assertEquals(-Momentum.NEWTON_SECOND, min(-Momentum.NEWTON_SECOND, -Momentum.NEWTON_SECOND * 0))
		assertEquals(-Momentum.NEWTON_SECOND * 0, max(-Momentum.NEWTON_SECOND, -Momentum.NEWTON_SECOND * 0))
		assertEquals(-Momentum.NEWTON_SECOND, min(Momentum.NEWTON_SECOND / 2, -Momentum.NEWTON_SECOND))
		assertEquals(Momentum.NEWTON_SECOND / 2, max(Momentum.NEWTON_SECOND / 2, -Momentum.NEWTON_SECOND))
	}
}
