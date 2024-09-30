package fixie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class TestVolume {

	private fun assertEquals(a: Volume, b: Volume, margin: Double = 0.001) {
		assertEquals(a.value, b.value, margin)
	}

	private fun assertNotEquals(a: Volume, b: Volume, margin: Double = 0.001) {
		assertNotEquals(a.value, b.value, margin)
	}

	@Test
	fun testAssertEquals() {
		assertEquals(Volume(1.23), Volume(1.23))
		assertNotEquals(Volume(1.23), Volume(1.24))
		assertEquals(Volume(1.23), Volume(1.23001))
		assertEquals(Volume(1.5), Volume(1.6), 0.2)
		assertNotEquals(Volume(1.5), Volume(1.8), 0.2)
		assertThrows<AssertionError> { assertNotEquals(Volume(1.23), Volume(1.23)) }
		assertThrows<AssertionError> { assertEquals(Volume(1.23), Volume(1.24)) }
		assertThrows<AssertionError> { assertNotEquals(Volume(1.23), Volume(1.23001)) }
		assertThrows<AssertionError> { assertNotEquals(Volume(1.5), Volume(1.6), 0.2) }
		assertThrows<AssertionError> { assertEquals(Volume(1.5), Volume(1.8), 0.2) }
	}

	@Test
	fun testToDouble() {
		assertEquals(1.0, Volume.LITER.toDouble(VolumeUnit.LITER), 0.002)
		assertEquals(0.234, (0.234 * Volume.LITER).toDouble(VolumeUnit.LITER), 0.002)
		assertEquals(1.0, Volume.CUBIC_METER.toDouble(VolumeUnit.CUBIC_METER), 0.002)
		assertEquals(0.234, (0.234 * Volume.CUBIC_METER).toDouble(VolumeUnit.CUBIC_METER), 0.002)
		assertEquals(25000.0, (25 * Volume.CUBIC_METER).toDouble(VolumeUnit.LITER), 0.1)
		assertEquals(1.0, (1000 * Volume.LITER).toDouble(VolumeUnit.CUBIC_METER), 0.001)
	}

	@Test
	fun testToString() {
		assertEquals("2.345m^3", (Volume.CUBIC_METER * 2.345).toString(VolumeUnit.CUBIC_METER))
		assertEquals("5.678l", (5.678 * Volume.LITER).toString(VolumeUnit.LITER))
		assertEquals("0.123l", (0.1234 * Volume.LITER).toString())
	}

	@Test
	fun testCompareTo() {
		assertTrue(Volume.LITER >= Volume.LITER)
		assertTrue(Volume.LITER <= Volume.LITER)
		assertFalse(Volume.LITER > Volume.LITER)
		assertFalse(Volume.LITER < Volume.LITER)
		assertTrue(Volume.LITER > Volume.LITER * 0.8f)
		assertFalse(Volume.LITER < Volume.LITER * 0.8)
		assertTrue(Volume.LITER / 2 > -Volume.LITER)
		assertTrue(-Volume.LITER / 2L > -Volume.LITER)
		assertTrue(Volume.CUBIC_METER >= Volume.CUBIC_METER)
		assertTrue(Volume.CUBIC_METER <= Volume.CUBIC_METER)
		assertFalse(Volume.CUBIC_METER > Volume.CUBIC_METER)
		assertFalse(Volume.CUBIC_METER < Volume.CUBIC_METER)
		assertTrue(Volume.CUBIC_METER > Volume.CUBIC_METER * 0.8f)
		assertFalse(Volume.CUBIC_METER < Volume.CUBIC_METER * 0.8)
		assertTrue(Volume.CUBIC_METER / 2 > -Volume.CUBIC_METER)
		assertTrue(-Volume.CUBIC_METER / 2L > -Volume.CUBIC_METER)
		assertTrue(Volume.CUBIC_METER > Volume.LITER)
		assertTrue(Volume.CUBIC_METER > Volume.LITER * 200)
		assertTrue(Volume.LITER < Volume.CUBIC_METER / 500)
		assertTrue(Volume.LITER > -Volume.CUBIC_METER)
	}

	@Test
	fun testArithmetic() {
		assertEquals(Volume.LITER / 2, Volume.LITER - 0.5f * Volume.LITER, 0.005)
		assertEquals(Volume.LITER, Volume.LITER / 4L + 0.75 * Volume.LITER, 0.005)
		assertEquals(0.25, Volume.LITER / (4 * Volume.LITER), 0.005)
		assertEquals(-Volume.LITER / 2, Volume.LITER / 2 - Volume.LITER, 0.005)
		assertEquals(Volume.CUBIC_METER / 2, Volume.CUBIC_METER - 0.5f * Volume.CUBIC_METER, 0.005)
		assertEquals(Volume.CUBIC_METER, Volume.CUBIC_METER / 4L + 0.75 * Volume.CUBIC_METER, 0.005)
		assertEquals(0.25, Volume.CUBIC_METER / (4 * Volume.CUBIC_METER), 0.005)
		assertEquals(-Volume.CUBIC_METER / 2, Volume.CUBIC_METER / 2 - Volume.CUBIC_METER, 0.005)
		assertEquals(Volume.CUBIC_METER, 1000 * Volume.LITER)
		assertNotEquals(Volume.CUBIC_METER, Volume.LITER)
		assertEquals(0.5, ((5 * Volume.CUBIC_METER) / (10 * Area.SQUARE_METER)).toDouble(DistanceUnit.METER), 0.1)
		assertEquals(6.0, ((3 * Volume.CUBIC_METER) / (0.5 * Displacement.METER)).toDouble(AreaUnit.SQUARE_METER), 1.0)
		assertEquals(17.0, (2 * Volume.CUBIC_METER * (8.5 * Density.KGPL)).toDouble(MassUnit.TON))
	}

	@Test
	fun testExtensionFunctions() {
		assertEquals(0.8 * Volume.LITER, 0.8.l)
		assertEquals(0.6f * Volume.LITER, 0.6f.l)
		assertEquals(Volume.LITER, 1.l)
		assertEquals(Volume.LITER, 1L.l)
		assertEquals(0.8 * Volume.CUBIC_METER, 0.8.m3)
		assertEquals(0.6f * Volume.CUBIC_METER, 0.6f.m3)
		assertEquals(Volume.CUBIC_METER, 1.m3)
		assertEquals(Volume.CUBIC_METER, 1L.m3)
		assertEquals(0.8 * Volume.LITER, Volume.LITER * 0.8)
		assertEquals(0.3f * Volume.LITER, Volume.LITER * 0.3f)
		assertEquals(1 * Volume.LITER, Volume.LITER * 1)
		assertEquals(2L * Volume.LITER, Volume.LITER * 2L)
		assertEquals(0.8 * Volume.CUBIC_METER, Volume.CUBIC_METER * 0.8)
		assertEquals(0.3f * Volume.CUBIC_METER, Volume.CUBIC_METER * 0.3f)
		assertEquals(1 * Volume.CUBIC_METER, Volume.CUBIC_METER * 1)
		assertEquals(2L * Volume.CUBIC_METER, Volume.CUBIC_METER * 2L)
	}

	@Test
	fun testMathFunctions() {
		assertEquals(Volume.CUBIC_METER, abs(Volume.CUBIC_METER))
		assertEquals(Volume.CUBIC_METER / 2, abs(Volume.CUBIC_METER / 2))
		assertEquals(0 * Volume.CUBIC_METER, abs(0 * Volume.CUBIC_METER))
		assertEquals(Volume.CUBIC_METER, abs(-Volume.CUBIC_METER))
		assertEquals(Volume.CUBIC_METER / 2, min(Volume.CUBIC_METER, Volume.CUBIC_METER / 2))
		assertEquals(Volume.CUBIC_METER, max(Volume.CUBIC_METER, Volume.CUBIC_METER / 2))
		assertEquals(-Volume.CUBIC_METER, min(-Volume.CUBIC_METER, -Volume.CUBIC_METER / 2))
		assertEquals(-Volume.CUBIC_METER / 2, max(-Volume.CUBIC_METER, -Volume.CUBIC_METER / 2))
		assertEquals(Volume.CUBIC_METER * 0, min(Volume.CUBIC_METER, Volume.CUBIC_METER * 0))
		assertEquals(Volume.CUBIC_METER, max(Volume.CUBIC_METER, Volume.CUBIC_METER * 0))
		assertEquals(-Volume.CUBIC_METER, min(-Volume.CUBIC_METER, -Volume.CUBIC_METER * 0))
		assertEquals(-Volume.CUBIC_METER * 0, max(-Volume.CUBIC_METER, -Volume.CUBIC_METER * 0))
		assertEquals(-Volume.CUBIC_METER, min(Volume.CUBIC_METER / 2, -Volume.CUBIC_METER))
		assertEquals(Volume.CUBIC_METER / 2, max(Volume.CUBIC_METER / 2, -Volume.CUBIC_METER))
		assertEquals(Volume.CUBIC_METER, max(800 * Volume.LITER, Volume.CUBIC_METER))
	}
}
