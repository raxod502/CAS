package com.apprisingsoftware.cas;

class Transcendental extends NumIntegerTranscendentalFunctionConstant implements Applet.NodeData {
	
	public Transcendent value;
	public boolean negative;
	
	public Transcendental(Transcendent value, boolean negative) {
		this.value = value;
		this.negative = negative;
	}
	public Transcendental(Transcendent value) {
		this(value, false);
	}
	
	@Override public double doubleValue() {
		double res;
		switch (value) {
		case PI: res = Math.PI; break;
		case E: res = Math.E; break;
		default: throw new AssertionError();
		}
		if (negative) res *= -1;
		return res;
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return NO; // As of yet!
	}
	@Override public Compare isPositive() {
		return negative ? NO : YES;
	}
	@Override public Compare isNegative() {
		return negative ? YES : NO;
	}
	
	@Override public String toString() {
		return (negative ? "-" : "") + value.toString();
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (negative ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transcendental other = (Transcendental) obj;
		if (negative != other.negative)
			return false;
		if (value != other.value)
			return false;
		return true;
	}
	
}
