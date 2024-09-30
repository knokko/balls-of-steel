package fixie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class TestMass {

	private fun assertEquals(a: Mass, b: Mass, margin: Double = 0.001) {
		assertEquals(a.value, b.value, margin)
	}

	private fun assertNotEquals(a: Mass, b: Mass, margin: Double = 0.001) {
		assertNotEquals(a.value, b.value, margin)
	}

	@Test
	fun testAssertEquals() {
		assertEquals(Mass(1.23), Mass(1.23))
		assertNotEquals(Mass(1.23), Mass(1.24))
		assertEquals(Mass(1.23), Mass(1.23001))
		assertEquals(Mass(1.5), Mass(1.6), 0.2)
		assertNotEquals(Mass(1.5), Mass(1.8), 0.2)
		assertThrows<AssertionError> { assertNotEquals(Mass(1.23), Mass(1.23)) }
		assertThrows<AssertionError> { assertEquals(Mass(1.23), Mass(1.24)) }
		assertThrows<AssertionError> { assertNotEquals(Mass(1.23), Mass(1.23001)) }
		assertThrows<AssertionError> { assertNotEquals(Mass(1.5), Mass(1.6), 0.2) }
		assertThrows<AssertionError> { assertEquals(Mass(1.5), Mass(1.8), 0.2) }
	}

	@Test
	fun testToDouble() {
		assertEquals(1.0, Mass.MILLIGRAM.toDouble(MassUnit.MILLIGRAM), 0.002)
		assertEquals(0.234, (0.234 * Mass.MILLIGRAM).toDouble(MassUnit.MILLIGRAM), 0.002)
		assertEquals(1.0, Mass.GRAM.toDouble(MassUnit.GRAM), 0.002)
		assertEquals(0.234, (0.234 * Mass.GRAM).toDouble(MassUnit.GRAM), 0.002)
		assertEquals(1.0, Mass.OUNCE.toDouble(MassUnit.OUNCE), 0.002)
		assertEquals(0.234, (0.234 * Mass.OUNCE).toDouble(MassUnit.OUNCE), 0.002)
		assertEquals(1.0, Mass.POUND.toDouble(MassUnit.POUND), 0.002)
		assertEquals(0.234, (0.234 * Mass.POUND).toDouble(MassUnit.POUND), 0.002)
		assertEquals(1.0, Mass.KILOGRAM.toDouble(MassUnit.KILOGRAM), 0.002)
		assertEquals(0.234, (0.234 * Mass.KILOGRAM).toDouble(MassUnit.KILOGRAM), 0.002)
		assertEquals(1.0, Mass.TON.toDouble(MassUnit.TON), 0.002)
		assertEquals(0.234, (0.234 * Mass.TON).toDouble(MassUnit.TON), 0.002)
		assertEquals(25000.0, (25 * Mass.KILOGRAM).toDouble(MassUnit.GRAM), 0.1)
		assertEquals(283.5, (10 * Mass.OUNCE).toDouble(MassUnit.GRAM), 0.1)
	}

	@Test
	fun testToString() {
		assertEquals("2.345t", (Mass.KILOGRAM * 2345).toString(MassUnit.TON))
		assertEquals("11.023lbs", (5.0 * Mass.KILOGRAM).toString(MassUnit.POUND))
		assertEquals("0.123kg", (0.1234 * Mass.KILOGRAM).toString())
	}

	@Test
	fun testCompareTo() {
		assertTrue(Mass.MILLIGRAM >= Mass.MILLIGRAM)
		assertTrue(Mass.MILLIGRAM <= Mass.MILLIGRAM)
		assertFalse(Mass.MILLIGRAM > Mass.MILLIGRAM)
		assertFalse(Mass.MILLIGRAM < Mass.MILLIGRAM)
		assertTrue(Mass.MILLIGRAM > Mass.MILLIGRAM * 0.8f)
		assertFalse(Mass.MILLIGRAM < Mass.MILLIGRAM * 0.8)
		assertTrue(Mass.MILLIGRAM / 2 > -Mass.MILLIGRAM)
		assertTrue(-Mass.MILLIGRAM / 2L > -Mass.MILLIGRAM)
		assertTrue(Mass.GRAM >= Mass.GRAM)
		assertTrue(Mass.GRAM <= Mass.GRAM)
		assertFalse(Mass.GRAM > Mass.GRAM)
		assertFalse(Mass.GRAM < Mass.GRAM)
		assertTrue(Mass.GRAM > Mass.GRAM * 0.8f)
		assertFalse(Mass.GRAM < Mass.GRAM * 0.8)
		assertTrue(Mass.GRAM / 2 > -Mass.GRAM)
		assertTrue(-Mass.GRAM / 2L > -Mass.GRAM)
		assertTrue(Mass.OUNCE >= Mass.OUNCE)
		assertTrue(Mass.OUNCE <= Mass.OUNCE)
		assertFalse(Mass.OUNCE > Mass.OUNCE)
		assertFalse(Mass.OUNCE < Mass.OUNCE)
		assertTrue(Mass.OUNCE > Mass.OUNCE * 0.8f)
		assertFalse(Mass.OUNCE < Mass.OUNCE * 0.8)
		assertTrue(Mass.OUNCE / 2 > -Mass.OUNCE)
		assertTrue(-Mass.OUNCE / 2L > -Mass.OUNCE)
		assertTrue(Mass.POUND >= Mass.POUND)
		assertTrue(Mass.POUND <= Mass.POUND)
		assertFalse(Mass.POUND > Mass.POUND)
		assertFalse(Mass.POUND < Mass.POUND)
		assertTrue(Mass.POUND > Mass.POUND * 0.8f)
		assertFalse(Mass.POUND < Mass.POUND * 0.8)
		assertTrue(Mass.POUND / 2 > -Mass.POUND)
		assertTrue(-Mass.POUND / 2L > -Mass.POUND)
		assertTrue(Mass.KILOGRAM >= Mass.KILOGRAM)
		assertTrue(Mass.KILOGRAM <= Mass.KILOGRAM)
		assertFalse(Mass.KILOGRAM > Mass.KILOGRAM)
		assertFalse(Mass.KILOGRAM < Mass.KILOGRAM)
		assertTrue(Mass.KILOGRAM > Mass.KILOGRAM * 0.8f)
		assertFalse(Mass.KILOGRAM < Mass.KILOGRAM * 0.8)
		assertTrue(Mass.KILOGRAM / 2 > -Mass.KILOGRAM)
		assertTrue(-Mass.KILOGRAM / 2L > -Mass.KILOGRAM)
		assertTrue(Mass.TON >= Mass.TON)
		assertTrue(Mass.TON <= Mass.TON)
		assertFalse(Mass.TON > Mass.TON)
		assertFalse(Mass.TON < Mass.TON)
		assertTrue(Mass.TON > Mass.TON * 0.8f)
		assertFalse(Mass.TON < Mass.TON * 0.8)
		assertTrue(Mass.TON / 2 > -Mass.TON)
		assertTrue(-Mass.TON / 2L > -Mass.TON)
		assertTrue(Mass.GRAM > Mass.MILLIGRAM)
		assertTrue(Mass.OUNCE > Mass.GRAM)
		assertTrue(Mass.POUND > Mass.OUNCE)
		assertTrue(Mass.KILOGRAM > Mass.POUND)
		assertTrue(Mass.TON > Mass.KILOGRAM)
		assertTrue(Mass.TON > Mass.KILOGRAM * 200)
		assertTrue(Mass.POUND < Mass.KILOGRAM / 2)
		assertFalse(Mass.POUND < Mass.KILOGRAM / 3)
	}

	@Test
	fun testArithmetic() {
		assertEquals(Mass.MILLIGRAM / 2, Mass.MILLIGRAM - 0.5f * Mass.MILLIGRAM, 0.005)
		assertEquals(Mass.MILLIGRAM, Mass.MILLIGRAM / 4L + 0.75 * Mass.MILLIGRAM, 0.005)
		assertEquals(0.25, Mass.MILLIGRAM / (4 * Mass.MILLIGRAM), 0.005)
		assertEquals(-Mass.MILLIGRAM / 2, Mass.MILLIGRAM / 2 - Mass.MILLIGRAM, 0.005)
		assertEquals(Mass.GRAM / 2, Mass.GRAM - 0.5f * Mass.GRAM, 0.005)
		assertEquals(Mass.GRAM, Mass.GRAM / 4L + 0.75 * Mass.GRAM, 0.005)
		assertEquals(0.25, Mass.GRAM / (4 * Mass.GRAM), 0.005)
		assertEquals(-Mass.GRAM / 2, Mass.GRAM / 2 - Mass.GRAM, 0.005)
		assertEquals(Mass.OUNCE / 2, Mass.OUNCE - 0.5f * Mass.OUNCE, 0.005)
		assertEquals(Mass.OUNCE, Mass.OUNCE / 4L + 0.75 * Mass.OUNCE, 0.005)
		assertEquals(0.25, Mass.OUNCE / (4 * Mass.OUNCE), 0.005)
		assertEquals(-Mass.OUNCE / 2, Mass.OUNCE / 2 - Mass.OUNCE, 0.005)
		assertEquals(Mass.POUND / 2, Mass.POUND - 0.5f * Mass.POUND, 0.005)
		assertEquals(Mass.POUND, Mass.POUND / 4L + 0.75 * Mass.POUND, 0.005)
		assertEquals(0.25, Mass.POUND / (4 * Mass.POUND), 0.005)
		assertEquals(-Mass.POUND / 2, Mass.POUND / 2 - Mass.POUND, 0.005)
		assertEquals(Mass.KILOGRAM / 2, Mass.KILOGRAM - 0.5f * Mass.KILOGRAM, 0.005)
		assertEquals(Mass.KILOGRAM, Mass.KILOGRAM / 4L + 0.75 * Mass.KILOGRAM, 0.005)
		assertEquals(0.25, Mass.KILOGRAM / (4 * Mass.KILOGRAM), 0.005)
		assertEquals(-Mass.KILOGRAM / 2, Mass.KILOGRAM / 2 - Mass.KILOGRAM, 0.005)
		assertEquals(Mass.TON / 2, Mass.TON - 0.5f * Mass.TON, 0.005)
		assertEquals(Mass.TON, Mass.TON / 4L + 0.75 * Mass.TON, 0.005)
		assertEquals(0.25, Mass.TON / (4 * Mass.TON), 0.005)
		assertEquals(-Mass.TON / 2, Mass.TON / 2 - Mass.TON, 0.005)
		assertEquals(Mass.KILOGRAM, 1000 * Mass.GRAM)
		assertNotEquals(Mass.KILOGRAM, Mass.POUND)
		assertEquals(4.0, (8000 * Mass.GRAM / (2 * Volume.LITER)).toDouble(), 0.005)
		assertEquals(2.5, (25 * Mass.KILOGRAM / (10 * Density.KGPL)).toDouble(VolumeUnit.LITER), 0.005)
		assertEquals(3.5, (7000 * Mass.GRAM * (0.5 * Speed.METERS_PER_SECOND)).toDouble(), 0.1)
	}

	@Test
	fun testExtensionFunctions() {
		assertEquals(0.8 * Mass.MILLIGRAM, 0.8.mg)
		assertEquals(0.6f * Mass.MILLIGRAM, 0.6f.mg)
		assertEquals(Mass.MILLIGRAM, 1.mg)
		assertEquals(Mass.MILLIGRAM, 1L.mg)
		assertEquals(0.8 * Mass.GRAM, 0.8.g)
		assertEquals(0.6f * Mass.GRAM, 0.6f.g)
		assertEquals(Mass.GRAM, 1.g)
		assertEquals(Mass.GRAM, 1L.g)
		assertEquals(0.8 * Mass.OUNCE, 0.8.oz)
		assertEquals(0.6f * Mass.OUNCE, 0.6f.oz)
		assertEquals(Mass.OUNCE, 1.oz)
		assertEquals(Mass.OUNCE, 1L.oz)
		assertEquals(0.8 * Mass.POUND, 0.8.lbs)
		assertEquals(0.6f * Mass.POUND, 0.6f.lbs)
		assertEquals(Mass.POUND, 1.lbs)
		assertEquals(Mass.POUND, 1L.lbs)
		assertEquals(0.8 * Mass.KILOGRAM, 0.8.kg)
		assertEquals(0.6f * Mass.KILOGRAM, 0.6f.kg)
		assertEquals(Mass.KILOGRAM, 1.kg)
		assertEquals(Mass.KILOGRAM, 1L.kg)
		assertEquals(0.8 * Mass.TON, 0.8.t)
		assertEquals(0.6f * Mass.TON, 0.6f.t)
		assertEquals(Mass.TON, 1.t)
		assertEquals(Mass.TON, 1L.t)
		assertEquals(0.8 * Mass.MILLIGRAM, Mass.MILLIGRAM * 0.8)
		assertEquals(0.3f * Mass.MILLIGRAM, Mass.MILLIGRAM * 0.3f)
		assertEquals(1 * Mass.MILLIGRAM, Mass.MILLIGRAM * 1)
		assertEquals(2L * Mass.MILLIGRAM, Mass.MILLIGRAM * 2L)
		assertEquals(0.8 * Mass.GRAM, Mass.GRAM * 0.8)
		assertEquals(0.3f * Mass.GRAM, Mass.GRAM * 0.3f)
		assertEquals(1 * Mass.GRAM, Mass.GRAM * 1)
		assertEquals(2L * Mass.GRAM, Mass.GRAM * 2L)
		assertEquals(0.8 * Mass.OUNCE, Mass.OUNCE * 0.8)
		assertEquals(0.3f * Mass.OUNCE, Mass.OUNCE * 0.3f)
		assertEquals(1 * Mass.OUNCE, Mass.OUNCE * 1)
		assertEquals(2L * Mass.OUNCE, Mass.OUNCE * 2L)
		assertEquals(0.8 * Mass.POUND, Mass.POUND * 0.8)
		assertEquals(0.3f * Mass.POUND, Mass.POUND * 0.3f)
		assertEquals(1 * Mass.POUND, Mass.POUND * 1)
		assertEquals(2L * Mass.POUND, Mass.POUND * 2L)
		assertEquals(0.8 * Mass.KILOGRAM, Mass.KILOGRAM * 0.8)
		assertEquals(0.3f * Mass.KILOGRAM, Mass.KILOGRAM * 0.3f)
		assertEquals(1 * Mass.KILOGRAM, Mass.KILOGRAM * 1)
		assertEquals(2L * Mass.KILOGRAM, Mass.KILOGRAM * 2L)
		assertEquals(0.8 * Mass.TON, Mass.TON * 0.8)
		assertEquals(0.3f * Mass.TON, Mass.TON * 0.3f)
		assertEquals(1 * Mass.TON, Mass.TON * 1)
		assertEquals(2L * Mass.TON, Mass.TON * 2L)
	}

	@Test
	fun testMathFunctions() {
		assertEquals(Mass.POUND, abs(Mass.POUND))
		assertEquals(Mass.POUND / 2, abs(Mass.POUND / 2))
		assertEquals(0 * Mass.POUND, abs(0 * Mass.POUND))
		assertEquals(Mass.POUND, abs(-Mass.POUND))
		assertEquals(Mass.POUND / 2, min(Mass.POUND, Mass.POUND / 2))
		assertEquals(Mass.POUND, max(Mass.POUND, Mass.POUND / 2))
		assertEquals(-Mass.POUND, min(-Mass.POUND, -Mass.POUND / 2))
		assertEquals(-Mass.POUND / 2, max(-Mass.POUND, -Mass.POUND / 2))
		assertEquals(Mass.POUND * 0, min(Mass.POUND, Mass.POUND * 0))
		assertEquals(Mass.POUND, max(Mass.POUND, Mass.POUND * 0))
		assertEquals(-Mass.POUND, min(-Mass.POUND, -Mass.POUND * 0))
		assertEquals(-Mass.POUND * 0, max(-Mass.POUND, -Mass.POUND * 0))
		assertEquals(-Mass.POUND, min(Mass.POUND / 2, -Mass.POUND))
		assertEquals(Mass.POUND / 2, max(Mass.POUND / 2, -Mass.POUND))
		assertEquals(Mass.TON, max(800 * Mass.KILOGRAM, Mass.TON))
	}
}
