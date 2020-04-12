package com.apprisingsoftware.cas;

class ComplexDouble {
	
	public double real;
	public double imag;
	
	public ComplexDouble(double real) {
		this.real = real;
	}
	
	public ComplexDouble(double real, double imag) {
		this(real);
		this.imag = imag;
	}
	
	public ComplexDouble add(ComplexDouble other) {
		return new ComplexDouble(real + other.real, imag + other.imag);
	}
	public ComplexDouble add(double other) {
		return new ComplexDouble(real + other, imag);
	}
	public ComplexDouble subtract(ComplexDouble other) {
		return new ComplexDouble(real - other.real, imag - other.imag);
	}
	public ComplexDouble subtract(double other) {
		return new ComplexDouble(real - other, imag);
	}
	public ComplexDouble multiply(ComplexDouble other) {
		return new ComplexDouble(real * other.real - imag * other.imag, real * other.imag + imag * other.real);
	}
	public ComplexDouble multiply(double other) {
		return new ComplexDouble(real * other, imag  * other);
	}
	public ComplexDouble divide(ComplexDouble other) {
		double den = other.real * other.real + other.imag * other.imag;
		return new ComplexDouble((this.real * other.real + this.imag * other.imag) / den,
				(this.imag * other.real - this.real * other.imag) / den);
	}
	public ComplexDouble divide(double other) {
		return new ComplexDouble(this.real / other, this.imag / other);
	}
	
}
