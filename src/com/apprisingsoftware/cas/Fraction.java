package com.apprisingsoftware.cas;

class Fraction extends NumFraction {
	
	public NumNumSum numerator, denominator;
	
	public Fraction(NumNumSum numerator, NumNumSum denominator) {
		if (denominator.isZero() == YES) throw new IllegalArgumentException("Denominator of a Fraction cannot be zero.");
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	@Override public double doubleValue() {
		return numerator.doubleValue() / denominator.doubleValue();
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return numerator.isZero().and(denominator.isZero().not());
	}
	@Override public Compare isPositive() {
		return numerator.isNegative().xor(denominator.isNegative());
	}
	@Override public Compare isNegative() {
		return numerator.isPositive().xor(denominator.isPositive());
	}
	
	@Override public String toString() {
		return "(" + numerator + "/" + denominator + ")";
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((denominator == null) ? 0 : denominator.hashCode());
		result = prime * result
				+ ((numerator == null) ? 0 : numerator.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fraction other = (Fraction) obj;
		if (denominator == null) {
			if (other.denominator != null)
				return false;
		} else if (!denominator.equals(other.denominator))
			return false;
		if (numerator == null) {
			if (other.numerator != null)
				return false;
		} else if (!numerator.equals(other.numerator))
			return false;
		return true;
	}
	
}
