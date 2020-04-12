package com.apprisingsoftware.cas;


class Exponent extends NumExponent {
	
	NumExponent base, power;
	boolean negative;
	
	public Exponent(NumExponent base, NumExponent power, boolean negative) {
		this.base = base;
		this.power = power;
		this.negative = negative;
	}
	public Exponent(NumExponent base, NumExponent power) {
		this(base, power, false);
	}
	
	@Override public double doubleValue() {
		return Math.pow(base.doubleValue(), power.doubleValue()) * (negative ? -1 : 1);
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return base.isZero().and(power.isZero().not());
	}
	@Override public Compare isPositive() {
		return isReal().and(base.isPositive().or(base.isNegative().and(power instanceof Integer && ((Integer)power).value % 2 == 0 ? YES : NO)))
				.xor(negative ? YES : NO);
	}
	@Override public Compare isNegative() {
		return isZero().not().and(isPositive().not()).xor(negative ? NO : YES);
	}
	
	@Override public String toString() {
		return (negative ? "-" : "") + base + "^" + power;
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + (negative ? 1231 : 1237);
		result = prime * result + ((power == null) ? 0 : power.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Exponent other = (Exponent) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (negative != other.negative)
			return false;
		if (power == null) {
			if (other.power != null)
				return false;
		} else if (!power.equals(other.power))
			return false;
		return true;
	}
	
}
