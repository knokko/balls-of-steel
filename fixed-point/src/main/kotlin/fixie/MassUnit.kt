package fixie

enum class MassUnit(val abbreviation: String, val factor: Double) {
	MILLIGRAM("mg", 1.0E-6),
	GRAM("g", 0.001),
	OUNCE("oz", 0.028349523125),
	POUND("lbs", 0.45359237),
	KILOGRAM("kg", 1.0),
	TON("t", 1000.0);
}
