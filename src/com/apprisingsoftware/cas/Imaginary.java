package com.apprisingsoftware.cas;


class Imaginary extends NumComplexImaginary {
	
	NumNumSum value;
	
	public Imaginary(NumNumSum value) {
		this.value = value;
	}
	
	@Override public ComplexDouble complexdoubleValue() {
		return new ComplexDouble(0, value.doubleValue());
	}
	public Number conjugate() {
		return (Number)negative().getSimplest();
	}
	public Number magnitude() {
		throw new UnsupportedOperationException();
	}
	public Number argument() {
		return (Number)new Multiple(oneHalf, pi).times(new Sgn(value)).getSimplest();
	}
	
	@Override public Compare compare(Expr other) {
		if (other instanceof Number) {
			if (other instanceof Complex) {
				Complex complex = (Complex)other;
				if (complex.real.equalTo(ZERO)) {
					return value.compare(complex.imag);
				}
				else {
					return UNEQUAL;
				}
			}
			if (other instanceof Imaginary) {
				return value.compare(((Imaginary)other).value);
			}
			if (value.equalTo(ZERO) && other.equalTo(ZERO)) {
				return EQUAL;
			}
			return UNEQUAL;
		}
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return value.isZero();
	}
	@Override public Compare isPositive() {
		return NO;
	}
	@Override public Compare isNegative() {
		return NO;
	}
	@Override public Compare isReal() {
		return value.isZero();
	}
	@Override public Compare isImag() {
		return YES;
	}
	
	@Override public String toString() {
		return value + "i";
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		Imaginary other = (Imaginary) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
