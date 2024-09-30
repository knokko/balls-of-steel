package fixie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class TestDensity {

	private fun assertEquals(a: Density, b: Density, margin: Double = 0.001) {
		assertEquals(a.toDouble(), b.toDouble(), margin)
	}

	private fun assertNotEquals(a: Density, b: Density, margin: Double = 0.001) {
		assertNotEquals(a.toDouble(), b.toDouble(), margin)
	}

	@Test
	fun testAssertEquals() {
		val base = Density.KGPL
		assertEquals(1.23 * base, 1.23 * base)
		assertNotEquals(1.23 * base, 1.252 * base)
		assertEquals(1.23 * base, 1.23001 * base)
		assertEquals(1.5 * base, 1.6 * base, 0.2)
		assertNotEquals(1.5 * base, 1.8 * base, 0.2)
		assertThrows<AssertionError> { assertNotEquals(1.23 * base, 1.23 * base) }
		assertThrows<AssertionError> { assertEquals(1.23 * base, 1.252 * base) }
		assertThrows<AssertionError> { assertNotEquals(1.23 * base, 1.23001 * base) }
		assertThrows<AssertionError> { assertNotEquals(1.5 * base, 1.6 * base, 0.2) }
		assertThrows<AssertionError> { assertEquals(1.5 * base, 1.8 * base, 0.2) }
	}

	@Test
	fun testToDouble() {
		assertEquals(0.234, (0.234 * Density.KGPL).toDouble(), 0.002)
	}

	@Test
	fun testToString() {
		assertEquals("2.3kg/l", (Density.KGPL * 2.3).toString())
	}

	@Test
	fun testCompareTo() {
		assertTrue(Density.KGPL >= Density.KGPL)
		assertTrue(Density.KGPL <= Density.KGPL)
		assertFalse(Density.KGPL > Density.KGPL)
		assertFalse(Density.KGPL < Density.KGPL)
		assertTrue(Density.KGPL > Density.KGPL * 0.8f)
		assertFalse(Density.KGPL < Density.KGPL * 0.8)
		assertNotEquals(Density.KGPL * 0.9, Density.KGPL)
		assertTrue(Density.KGPL > Density.KGPL * 0.9)
	}

	@Test
	fun testArithmetic() {
		assertEquals(Density.KGPL / 2, Density.KGPL - 0.5f * Density.KGPL, 0.005)
		assertEquals(Density.KGPL, Density.KGPL / 4L + 0.75 * Density.KGPL, 0.005)
		assertEquals(0.25, Density.KGPL / (4 * Density.KGPL), 0.005)
		assertEquals(Density.raw(65u), Density.raw(1u) * 65.0)
		assertEquals(2.5, (0.5 * Density.KGPL * (5 * Volume.LITER)).toDouble(MassUnit.KILOGRAM), 0.005)
	}

	@Test
	fun testExtensionFunctions() {
		assertEquals(0.8 * Density.KGPL, 0.8.kgpl)
		assertEquals(0.6f * Density.KGPL, 0.6f.kgpl)
		assertEquals(Density.KGPL, 1.kgpl)
		assertEquals(Density.KGPL, 1L.kgpl)
		assertEquals(0.8 * Density.KGPL, Density.KGPL * 0.8)
		assertEquals(0.3f * Density.KGPL, Density.KGPL * 0.3f)
		assertEquals(1 * Density.KGPL, Density.KGPL * 1)
		assertEquals(2L * Density.KGPL, Density.KGPL * 2L)
	}

	@Test
	fun testMathFunctions() {
		assertEquals(Density.KGPL / 2, min(Density.KGPL, Density.KGPL / 2))
		assertEquals(Density.KGPL, max(Density.KGPL, Density.KGPL / 2))
		assertEquals(Density.KGPL * 0, min(Density.KGPL, Density.KGPL * 0))
		assertEquals(Density.KGPL, max(Density.KGPL, Density.KGPL * 0))
	}
}
