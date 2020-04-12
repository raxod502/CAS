package com.apprisingsoftware.cas;

abstract class NumNumSum extends NumComplexImaginary {
	
	@Override public final ComplexDouble complexdoubleValue() {
		return new ComplexDouble(doubleValue());
	}
	public abstract double doubleValue();
	
	@Override public final Compare isReal() {
		return YES;
	}
	@Override public final Compare isImag() {
		return NO;
	}
	
}
