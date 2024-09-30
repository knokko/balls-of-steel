// Generated by fixie at 30-09-2024 14:23
package fixie

import kotlin.math.min
import kotlin.math.max
import kotlin.math.abs

@JvmInline
value class Volume internal constructor(val value: Double) : Comparable<Volume> {


	fun toDouble(unit: VolumeUnit) = when (unit) {
		VolumeUnit.LITER -> value * 1000.0
		VolumeUnit.CUBIC_METER -> value
	}

	fun toString(unit: VolumeUnit) = String.format("%.3f%s", toDouble(unit), unit.abbreviation)

	override fun toString() = toString(VolumeUnit.LITER)

	override operator fun compareTo(other: Volume) = this.value.compareTo(other.value)

	operator fun unaryMinus() = Volume(-value)

	operator fun plus(right: Volume) = Volume(this.value + right.value)

	operator fun minus(right: Volume) = Volume(this.value - right.value)

	operator fun div(right: Volume) = this.value / right.value

	operator fun times(right: Int) = Volume(this.value * right)

	operator fun div(right: Int) = Volume(this.value / right)

	operator fun times(right: Long) = Volume(this.value * right)

	operator fun div(right: Long) = Volume(this.value / right)

	operator fun times(right: Float) = Volume(this.value * right)

	operator fun div(right: Float) = Volume(this.value / right)

	operator fun times(right: Double) = Volume(this.value * right)

	operator fun div(right: Double) = Volume(this.value / right)

	operator fun div(right: Displacement) = Area.SQUARE_METER * value / right.toDouble(DistanceUnit.METER)

	operator fun div(right: Area) = Displacement.METER * (value / right.toDouble(AreaUnit.SQUARE_METER))

	operator fun times(right: Density) = Mass.KILOGRAM * toDouble(VolumeUnit.LITER) * right.toDouble()

	companion object {
		fun raw(value: Double) = Volume(value)

		val LITER = Volume(0.001)
		val CUBIC_METER = Volume(1.0)
	}
}

operator fun Int.times(right: Volume) = right * this

val Int.l
	get() = Volume.LITER * this

val Int.m3
	get() = Volume.CUBIC_METER * this

operator fun Long.times(right: Volume) = right * this

val Long.l
	get() = Volume.LITER * this

val Long.m3
	get() = Volume.CUBIC_METER * this

operator fun Float.times(right: Volume) = right * this

val Float.l
	get() = Volume.LITER * this

val Float.m3
	get() = Volume.CUBIC_METER * this

operator fun Double.times(right: Volume) = right * this

val Double.l
	get() = Volume.LITER * this

val Double.m3
	get() = Volume.CUBIC_METER * this

fun abs(x: Volume) = Volume(abs(x.value))

fun min(a: Volume, b: Volume) = Volume(min(a.value, b.value))

fun max(a: Volume, b: Volume) = Volume(max(a.value, b.value))
