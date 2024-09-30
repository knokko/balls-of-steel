package fixie

@Throws(ArithmeticException::class)
fun addExact(left: UShort, right: UShort) = if (UShort.MAX_VALUE - right >= left) (left + right).toUShort() else throw ArithmeticException()

@Throws(ArithmeticException::class)
fun subtractExact(left: UShort, right: UShort) = if (left >= right) (left - right).toUShort() else throw ArithmeticException()

@Throws(ArithmeticException::class)
fun multiplyExact(left: UInt, right: UInt) = toUIntExact(left.toLong() * right.toLong())

@Throws(ArithmeticException::class)
fun toUIntExact(value: Long): UInt {
	if (value < 0L || value > UInt.MAX_VALUE.toLong()) throw ArithmeticException("Can't convert $value to UInt")
	return value.toUInt()
}

@Throws(ArithmeticException::class)
fun multiplyExact(left: Short, right: UShort) = toUShortExact(left.toInt() * right.toInt())

@Throws(ArithmeticException::class)
fun multiplyExact(left: UShort, right: UShort) = toUShortExact(left.toInt() * right.toInt())

@Throws(ArithmeticException::class)
fun toUShortExact(value: Int): UShort {
	if (value < 0 || value > UShort.MAX_VALUE.toInt()) throw ArithmeticException("Can't convert $value to UShort")
	return value.toUShort()
}

@Throws(ArithmeticException::class)
fun toUShortExact(value: UInt): UShort {
	if (value > UShort.MAX_VALUE.toUInt()) throw ArithmeticException("Can't convert $value to UShort")
	return value.toUShort()
}

@Throws(ArithmeticException::class)
fun toUShortExact(value: Long): UShort {
	if (value < 0L || value > UShort.MAX_VALUE.toLong()) throw ArithmeticException("Can't convert $value to UShort")
	return value.toUShort()
}

@Throws(ArithmeticException::class)
fun toUShortExact(value: ULong): UShort {
	if (value > UShort.MAX_VALUE.toULong()) throw ArithmeticException("Can't convert $value to UShort")
	return value.toUShort()
}

@Throws(ArithmeticException::class)
fun multiplyExact(left: ULong, right: ULong): ULong {
	if (left == 0uL || right == 0uL) return 0uL
	val result = left * right
	if (result / left != right) throw ArithmeticException()
	return result
}

fun min(a: UShort, b: UShort) = if (a <= b) a else b

fun max(a: UShort, b: UShort) = if (a >= b) a else b
