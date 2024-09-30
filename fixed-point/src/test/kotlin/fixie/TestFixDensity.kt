package fixie

import kotlin.math.absoluteValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TestFixDensity {

	fun assertEquals(a: FixDensity, b: FixDensity, maxDelta: FixDensity) {
		val rawDifference = a.raw.toShort() - b.raw.toShort()
		if (rawDifference.absoluteValue > maxDelta.raw.toShort()) assertEquals(a, b)
	}

	@Test
	fun testToString() {
		assertEquals("0", FixDensity.ZERO.toString())
		assertEquals("1", FixDensity.ONE.toString())
		assertTrue((FixDensity.ONE / 3).toString().startsWith("0.3"))
		assertTrue((FixDensity.from(64) + FixDensity.ONE / 3).toString().endsWith((FixDensity.ONE / 3).toString().substring(1)))
		assertEquals("0.01", (FixDensity.ONE / 100).toString())
	}

	@Test
	fun testIntConversion() {
		val one = 1
		assertEquals(FixDensity.ONE, FixDensity.from(one))

		fun testValue(value: Int) = assertEquals(value, FixDensity.from(value).toInt())
		fun testOverflow(value: Int) = assertThrows(FixedPointException::class.java) { FixDensity.from(value) }
		testValue(0)
		testValue(1)
		testValue(65)

		testOverflow(-2147483648)
		testOverflow(-978689185)
		testOverflow(-974146)
		testOverflow(-740009)
		testOverflow(-16299)
		testOverflow(-1)

		testOverflow(66)
		testOverflow(11356)
		testOverflow(2716636)
		testOverflow(132636241)
		testOverflow(2147483647)
	}

	@Test
	fun testLongConversion() {
		val one = 1L
		assertEquals(FixDensity.ONE, FixDensity.from(one))

		fun testValue(value: Long) = assertEquals(value, FixDensity.from(value).toLong())
		fun testOverflow(value: Long) = assertThrows(FixedPointException::class.java) { FixDensity.from(value) }
		testValue(0)
		testValue(1)
		testValue(65)

		testOverflow(Long.MIN_VALUE)
		testOverflow(-589153389781490615)
		testOverflow(-43117419028065905)
		testOverflow(-785861820617605)
		testOverflow(-19482330338386)
		testOverflow(-34771906616)
		testOverflow(-6550113726)
		testOverflow(-448440224)
		testOverflow(-2065398)
		testOverflow(-11740)
		testOverflow(-7239)
		testOverflow(-1)

		testOverflow(66)
		testOverflow(11356)
		testOverflow(2716636)
		testOverflow(132636241)
		testOverflow(3837660691)
		testOverflow(102037718619)
		testOverflow(1259591443114)
		testOverflow(527227515052805)
		testOverflow(12925866574619608)
		testOverflow(923512679707181018)
		testOverflow(9223372036854775807)
	}

	@Test
	fun testFloatConversion() {
		assertEquals(FixDensity.ONE, FixDensity.from(1f))
		val delta = 0.002f
		assertEquals(0.001f, FixDensity.from(0.001f).toFloat(), delta)
		assertEquals(0.06078089f, FixDensity.from(0.06078089f).toFloat(), delta)
		assertEquals(3.2344286f, FixDensity.from(3.2344286f).toFloat(), delta)
		assertEquals(65.46947f, FixDensity.from(65.46947f).toFloat(), delta)

		assertThrows(FixedPointException::class.java) { FixDensity.from(66.535f) }
		assertThrows(FixedPointException::class.java) { FixDensity.from(-66.535f) }
		assertThrows(FixedPointException::class.java) { FixDensity.from(4426.9062f) }
		assertThrows(FixedPointException::class.java) { FixDensity.from(-4426.9062f) }
	}

	@Test
	fun testDoubleConversion() {
		assertEquals(FixDensity.ONE, FixDensity.from(1.0))
		val delta = 0.002
		assertEquals(0.001, FixDensity.from(0.001).toDouble(), delta)
		assertEquals(0.06078089006529382, FixDensity.from(0.06078089006529382).toDouble(), delta)
		assertEquals(3.2344286451819104, FixDensity.from(3.2344286451819104).toDouble(), delta)
		assertEquals(65.535, FixDensity.from(65.535).toDouble(), delta)

		assertThrows(FixedPointException::class.java) { FixDensity.from(66.535) }
		assertThrows(FixedPointException::class.java) { FixDensity.from(-66.535) }
		assertThrows(FixedPointException::class.java) { FixDensity.from(4426.906225) }
		assertThrows(FixedPointException::class.java) { FixDensity.from(-4426.906225) }
	}

	@Test
	fun testAdditionAndSubtraction() {
		fun testValues(a: FixDensity, b: FixDensity, c: FixDensity) {
			assertEquals(c, a + b)
			assertEquals(c, b + a)
			assertEquals(a, c - b)
			assertEquals(b, c - a)
		}

		fun testValues(a: Long, b: Long, c: Long) {
			testValues(FixDensity.from(a), FixDensity.from(b), FixDensity.from(c))
			assertEquals(FixDensity.from(c), FixDensity.from(a) + b)
			assertEquals(FixDensity.from(c), b + FixDensity.from(a))
			assertEquals(FixDensity.from(a), FixDensity.from(c) - b)
			assertEquals(FixDensity.from(b), c - FixDensity.from(a))
		}

		testValues(FixDensity.raw(UShort.MIN_VALUE), FixDensity.ONE, FixDensity.raw((UShort.MIN_VALUE + 1000u).toUShort()))
		testValues(0, 26, 26)
		testValues(1, 54, 55)
		testValues(65, 0, 65)
		testValues(FixDensity.raw((UShort.MAX_VALUE - 1000u).toUShort()), FixDensity.ONE, FixDensity.raw(UShort.MAX_VALUE))

		fun testOverflowPlus(a: Int, b: Int) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + b }
			assertThrows(FixedPointException::class.java) { a + FixDensity.from(b) }
		}

		fun testOverflowMinus(a: Int, b: Int) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - b }
			assertThrows(FixedPointException::class.java) { a - FixDensity.from(b) }
		}

		fun testOverflowPlus(a: Long, b: Long) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + b }
			assertThrows(FixedPointException::class.java) { a + FixDensity.from(b) }
		}

		fun testOverflowMinus(a: Long, b: Long) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - b }
			assertThrows(FixedPointException::class.java) { a - FixDensity.from(b) }
		}

		fun testOverflowPlus(a: Float, b: Float) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + b }
			assertThrows(FixedPointException::class.java) { a + FixDensity.from(b) }
		}

		fun testOverflowMinus(a: Float, b: Float) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - b }
			assertThrows(FixedPointException::class.java) { a - FixDensity.from(b) }
		}

		fun testOverflowPlus(a: Double, b: Double) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) + b }
			assertThrows(FixedPointException::class.java) { a + FixDensity.from(b) }
		}

		fun testOverflowMinus(a: Double, b: Double) {
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - FixDensity.from(b) }
			assertThrows(FixedPointException::class.java) { FixDensity.from(a) - b }
			assertThrows(FixedPointException::class.java) { a - FixDensity.from(b) }
		}

		testOverflowPlus(0, 66)
		testOverflowPlus(0L, 66L)
		testOverflowPlus(0, 4489)
		testOverflowPlus(0L, 4489L)
		testOverflowMinus(0, 1)
		testOverflowMinus(0L, 1L)
		testOverflowMinus(0, 4)
		testOverflowMinus(0L, 4L)
		testOverflowPlus(1, 65)
		testOverflowPlus(1L, 65L)
		testOverflowPlus(1.0f, 65.0f)
		testOverflowPlus(1.0, 65.0)
		testOverflowPlus(1, 4356)
		testOverflowPlus(1L, 4356L)
		testOverflowPlus(1.0, 4356.0)
		testOverflowMinus(1, 2)
		testOverflowMinus(1L, 2L)
		testOverflowMinus(1.0f, 2.0f)
		testOverflowMinus(1.0, 2.0)
		testOverflowMinus(1, 9)
		testOverflowMinus(1L, 9L)
		testOverflowMinus(1.0f, 9.0f)
		testOverflowMinus(1.0, 9.0)
		testOverflowPlus(65, 1)
		testOverflowPlus(65L, 1L)
		testOverflowPlus(65.0f, 1.0f)
		testOverflowPlus(65.0, 1.0)
		testOverflowPlus(65, 4)
		testOverflowPlus(65L, 4L)
		testOverflowPlus(65.0f, 4.0f)
		testOverflowPlus(65.0, 4.0)
		testOverflowMinus(65, 66)
		testOverflowMinus(65L, 66L)
		testOverflowMinus(65.0f, 66.0f)
		testOverflowMinus(65.0, 66.0)
		testOverflowMinus(65, 4489)
		testOverflowMinus(65L, 4489L)
		testOverflowMinus(65.0f, 4489.0f)
		testOverflowMinus(65.0, 4489.0)
		assertEquals(FixDensity.raw(64440u), FixDensity.raw(440u) + 64)
	}

	@Test
	fun testMultiplicationAndDivision() {
		assertEquals(FixDensity.raw(UShort.MAX_VALUE), 1 * FixDensity.raw(UShort.MAX_VALUE))
		assertEquals(FixDensity.raw(UShort.MAX_VALUE), FixDensity.raw(UShort.MAX_VALUE) / 1)
		assertThrows(FixedPointException::class.java) { -1 * FixDensity.ONE }
		assertThrows(FixedPointException::class.java) { FixDensity.ONE / -1 }
		assertThrows(FixedPointException::class.java) { -1 * FixDensity.raw(UShort.MAX_VALUE)}
		assertThrows(FixedPointException::class.java) { FixDensity.raw(UShort.MAX_VALUE) / -1 }

		fun testValues(a: Long, b: Long) {
			assertEquals(FixDensity.from(a * b), FixDensity.from(a) * FixDensity.from(b))
			assertEquals(FixDensity.from(a * b), FixDensity.from(a) * b)
			assertEquals(FixDensity.from(a * b), b * FixDensity.from(a))
			if (b != 0L) assertEquals(FixDensity.from(a), FixDensity.from(a * b) / b)
			if (a != 0L) assertEquals(FixDensity.from(b), FixDensity.from(a * b) / a)
			if (a != 0L && a.toInt().toLong() == a) {
				assertEquals(FixDensity.from(b), FixDensity.from(a * b) / a.toInt())
			}
			if (b.toInt().toLong() == b) {
				assertEquals(FixDensity.from(a * b), FixDensity.from(a) * b.toInt())
				assertEquals(FixDensity.from(a * b), b.toInt() * FixDensity.from(a))
			}
		}
		testValues(0, 0)
		testValues(1, 65)
		testValues(65, 0)
		assertEquals(FixDensity.raw(634u), (FixDensity.raw(634u) * FixDensity.raw(701u)) / FixDensity.raw(701u), FixDensity.raw(10u))
	}

	@Test
	fun testCompareTo() {
		assertTrue(FixDensity.ZERO < FixDensity.ONE)
		assertTrue(0 < FixDensity.ONE)
		assertFalse(FixDensity.ZERO > FixDensity.ONE)
		assertFalse(0 > FixDensity.ONE)
		assertFalse(FixDensity.ONE < FixDensity.ONE)
		assertFalse(FixDensity.ONE < 1)
		assertFalse(FixDensity.ONE > FixDensity.ONE)
		assertTrue(FixDensity.ONE <= FixDensity.ONE)
		assertTrue(FixDensity.ONE >= FixDensity.ONE)
		assertTrue(FixDensity.raw(UShort.MIN_VALUE) < FixDensity.raw(UShort.MAX_VALUE))

		val minDelta = FixDensity.raw(1u)
		assertEquals(FixDensity.from(12), FixDensity.from(12))
		assertNotEquals(FixDensity.from(12), FixDensity.from(12) - minDelta)
		assertTrue(FixDensity.from(0.001) < FixDensity.from(0.001) + minDelta)
		assertTrue(0.001 < FixDensity.from(0.001) + minDelta)
		assertFalse(FixDensity.from(0.41012452706744895) < FixDensity.from(0.41012452706744895) - minDelta)
		assertFalse(0.41012452706744895 < FixDensity.from(0.41012452706744895) - minDelta)
		assertTrue(FixDensity.from(0.41012452706744895) < FixDensity.from(0.41012452706744895) + minDelta)
		assertTrue(0.41012452706744895 < FixDensity.from(0.41012452706744895) + minDelta)
		assertFalse(FixDensity.from(1.4602674387652097) < FixDensity.from(1.4602674387652097) - minDelta)
		assertFalse(1.4602674387652097 < FixDensity.from(1.4602674387652097) - minDelta)
		assertTrue(FixDensity.from(1.4602674387652097) < FixDensity.from(1.4602674387652097) + minDelta)
		assertTrue(1.4602674387652097 < FixDensity.from(1.4602674387652097) + minDelta)
		assertFalse(FixDensity.from(65.535) < FixDensity.from(65.535) - minDelta)
		assertFalse(65.535 < FixDensity.from(65.535) - minDelta)
		assertTrue(FixDensity.raw(UShort.MAX_VALUE) >= 65)
		assertTrue(FixDensity.raw(UShort.MAX_VALUE) > 65)
		assertTrue(FixDensity.raw(UShort.MAX_VALUE) < 66u)
		assertTrue(FixDensity.raw(UShort.MAX_VALUE) < 66.06599999999999)
		assertTrue(FixDensity.raw(UShort.MAX_VALUE) < UShort.MAX_VALUE)
		assertTrue(FixDensity.raw(UShort.MAX_VALUE) < UShort.MAX_VALUE.toFloat())
		assertTrue(FixDensity.raw(UShort.MAX_VALUE) < UShort.MAX_VALUE.toDouble())
		assertTrue(FixDensity.ZERO > -1)
		assertTrue(FixDensity.ZERO > -0.001f)
		assertTrue(FixDensity.ZERO > -0.001)
		assertTrue(FixDensity.ZERO > Long.MIN_VALUE)
		assertTrue(FixDensity.ZERO > Long.MIN_VALUE.toFloat())
		assertTrue(FixDensity.ZERO > Long.MIN_VALUE.toDouble())
	}

	@Test
	fun testArrayClass() {
		val testArray = FixDensity.Array(2) { FixDensity.ONE }
		assertEquals(2, testArray.size)
		assertEquals(FixDensity.ONE, testArray[0])
		assertEquals(FixDensity.ONE, testArray[1])
		testArray[1] = FixDensity.ZERO
		assertEquals(FixDensity.ONE, testArray[0])
		assertEquals(FixDensity.ZERO, testArray[1])
		testArray.fill(FixDensity.ZERO)
		assertEquals(FixDensity.ZERO, testArray[0])
		assertEquals(FixDensity.ZERO, testArray[1])
	}

	@Test
	fun testMinMax() {
		assertEquals(FixDensity.ZERO, min(FixDensity.ZERO, FixDensity.ZERO))
		assertEquals(FixDensity.ZERO, max(FixDensity.ZERO, FixDensity.ZERO))
		assertEquals(FixDensity.ZERO, min(FixDensity.ONE, FixDensity.ZERO))
		assertEquals(FixDensity.ONE, max(FixDensity.ONE, FixDensity.ZERO))
		assertEquals(FixDensity.ZERO, min(FixDensity.ZERO, FixDensity.ONE))
		assertEquals(FixDensity.ONE, max(FixDensity.ZERO, FixDensity.ONE))
		assertEquals(FixDensity.ZERO, min(FixDensity.ZERO, FixDensity.raw(UShort.MAX_VALUE)))
		assertEquals(FixDensity.raw(UShort.MAX_VALUE), max(FixDensity.ZERO, FixDensity.raw(UShort.MAX_VALUE)))
	}
}
