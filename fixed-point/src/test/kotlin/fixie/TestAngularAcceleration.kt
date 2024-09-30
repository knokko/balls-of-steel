package fixie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.time.Duration.Companion.seconds

class TestAngularAcceleration {

	private fun assertEquals(a: AngularAcceleration, b: AngularAcceleration, margin: Double = 0.001) {
		assertEquals(a.value, b.value, margin)
	}

	private fun assertNotEquals(a: AngularAcceleration, b: AngularAcceleration, margin: Double = 0.001) {
		assertNotEquals(a.value, b.value, margin)
	}

	@Test
	fun testAssertEquals() {
		assertEquals(AngularAcceleration(1.23), AngularAcceleration(1.23))
		assertNotEquals(AngularAcceleration(1.23), AngularAcceleration(1.24))
		assertEquals(AngularAcceleration(1.23), AngularAcceleration(1.23001))
		assertEquals(AngularAcceleration(1.5), AngularAcceleration(1.6), 0.2)
		assertNotEquals(AngularAcceleration(1.5), AngularAcceleration(1.8), 0.2)
		assertThrows<AssertionError> { assertNotEquals(AngularAcceleration(1.23), AngularAcceleration(1.23)) }
		assertThrows<AssertionError> { assertEquals(AngularAcceleration(1.23), AngularAcceleration(1.24)) }
		assertThrows<AssertionError> { assertNotEquals(AngularAcceleration(1.23), AngularAcceleration(1.23001)) }
		assertThrows<AssertionError> { assertNotEquals(AngularAcceleration(1.5), AngularAcceleration(1.6), 0.2) }
		assertThrows<AssertionError> { assertEquals(AngularAcceleration(1.5), AngularAcceleration(1.8), 0.2) }
	}

	@Test
	fun testToDouble() {
		assertEquals(0.234, (0.234 * AngularAcceleration.RADPS2).toDouble(), 2.0E-4)
	}

	@Test
	fun testToString() {
		assertEquals("2.34rad/s^2", (AngularAcceleration.RADPS2 * 2.34).toString())
	}

	@Test
	fun testCompareTo() {
		assertTrue(AngularAcceleration.RADPS2 >= AngularAcceleration.RADPS2)
		assertTrue(AngularAcceleration.RADPS2 <= AngularAcceleration.RADPS2)
		assertFalse(AngularAcceleration.RADPS2 > AngularAcceleration.RADPS2)
		assertFalse(AngularAcceleration.RADPS2 < AngularAcceleration.RADPS2)
		assertTrue(AngularAcceleration.RADPS2 > AngularAcceleration.RADPS2 * 0.8f)
		assertFalse(AngularAcceleration.RADPS2 < AngularAcceleration.RADPS2 * 0.8)
		assertTrue(AngularAcceleration.RADPS2 / 2 > -AngularAcceleration.RADPS2)
		assertTrue(-AngularAcceleration.RADPS2 / 2L > -AngularAcceleration.RADPS2)
	}

	@Test
	fun testArithmetic() {
		assertEquals(AngularAcceleration.RADPS2 / 2, AngularAcceleration.RADPS2 - 0.5f * AngularAcceleration.RADPS2, 0.001)
		assertEquals(AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 / 4L + 0.75 * AngularAcceleration.RADPS2, 0.001)
		assertEquals(0.25, AngularAcceleration.RADPS2 / (4 * AngularAcceleration.RADPS2), 0.001)
		assertEquals(-AngularAcceleration.RADPS2 / 2, AngularAcceleration.RADPS2 / 2 - AngularAcceleration.RADPS2, 0.001)
		assertEquals(0.8, ((AngularAcceleration.RADPS2 * 0.2) * 4.seconds).toDouble(SpinUnit.RADIANS_PER_SECOND), 0.01)
	}

	@Test
	fun testExtensionFunctions() {
		assertEquals(0.8 * AngularAcceleration.RADPS2, 0.8.radps2)
		assertEquals(0.6f * AngularAcceleration.RADPS2, 0.6f.radps2)
		assertEquals(AngularAcceleration.RADPS2, 1.radps2)
		assertEquals(AngularAcceleration.RADPS2, 1L.radps2)
		assertEquals(0.8 * AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 * 0.8)
		assertEquals(0.3f * AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 * 0.3f)
		assertEquals(1 * AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 * 1)
		assertEquals(2L * AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 * 2L)
		assertEquals(0.8, (4.seconds * (AngularAcceleration.RADPS2 * 0.2)).toDouble(SpinUnit.RADIANS_PER_SECOND), 0.01)
	}

	@Test
	fun testMathFunctions() {
		assertEquals(AngularAcceleration.RADPS2, abs(AngularAcceleration.RADPS2))
		assertEquals(AngularAcceleration.RADPS2 / 2, abs(AngularAcceleration.RADPS2 / 2))
		assertEquals(0 * AngularAcceleration.RADPS2, abs(0 * AngularAcceleration.RADPS2))
		assertEquals(AngularAcceleration.RADPS2, abs(-AngularAcceleration.RADPS2))
		assertEquals(AngularAcceleration.RADPS2 / 2, min(AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 / 2))
		assertEquals(AngularAcceleration.RADPS2, max(AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 / 2))
		assertEquals(-AngularAcceleration.RADPS2, min(-AngularAcceleration.RADPS2, -AngularAcceleration.RADPS2 / 2))
		assertEquals(-AngularAcceleration.RADPS2 / 2, max(-AngularAcceleration.RADPS2, -AngularAcceleration.RADPS2 / 2))
		assertEquals(AngularAcceleration.RADPS2 * 0, min(AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 * 0))
		assertEquals(AngularAcceleration.RADPS2, max(AngularAcceleration.RADPS2, AngularAcceleration.RADPS2 * 0))
		assertEquals(-AngularAcceleration.RADPS2, min(-AngularAcceleration.RADPS2, -AngularAcceleration.RADPS2 * 0))
		assertEquals(-AngularAcceleration.RADPS2 * 0, max(-AngularAcceleration.RADPS2, -AngularAcceleration.RADPS2 * 0))
		assertEquals(-AngularAcceleration.RADPS2, min(AngularAcceleration.RADPS2 / 2, -AngularAcceleration.RADPS2))
		assertEquals(AngularAcceleration.RADPS2 / 2, max(AngularAcceleration.RADPS2 / 2, -AngularAcceleration.RADPS2))
	}
}
