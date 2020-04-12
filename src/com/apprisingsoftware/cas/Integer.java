package com.apprisingsoftware.cas;


class Integer extends NumIntegerTranscendentalFunctionConstant implements Applet.NodeData {
	
	public long value;
	
	public Integer(long value) {
		this.value = value;
	}
	public Integer(int value) {
		this.value = value;
	}
	public Integer(short value) {
		this.value = value;
	}
	public Integer(byte value) {
		this.value = value;
	}
	public Integer(char value) {
		this.value = value;
	}
	
	public static boolean willAdditionOverflow(long left, long right) {
	    if (right < 0 && right != Long.MIN_VALUE) {
	        return willSubtractionOverflow(left, -right);
	    } else {
	        return (~(left ^ right) & (left ^ (left + right))) < 0;
	    }
	}
	
	public static boolean willSubtractionOverflow(long left, long right) {
	    if (right < 0) {
	        return willAdditionOverflow(left, -right);
	    } else {
	        return ((left ^ right) & (left ^ (left - right))) < 0;
	    }
	}
	
	@Override public double doubleValue() {
		return value;
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return value == 0 ? YES : NO;
	}
	@Override public Compare isPositive() {
		return value > 0 ? YES : NO;
	}
	@Override public Compare isNegative() {
		return value < 0 ? YES : NO;
	}
	
	@Override public String toString() {
		return String.valueOf(value);
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Integer other = (Integer) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
}