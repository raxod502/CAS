package cas;

import java.util.HashSet;

public class Complex extends NumComplexImaginary {
	
	NumNumSum real, imag;
	
	public Complex(NumNumSum real, NumNumSum imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public ComplexDouble complexdoubleValue() {
		return new ComplexDouble(real.doubleValue(), imag.doubleValue());
	}
	public Number conjugate() {
		return new Complex(real, (NumNumSum)imag.negative().simplify(null));
	}
	public Number magnitude() {
		return (Number)new Sqrt(real.square().plus(imag.square())).simplify(null);
	}
	public Number argument() {
		return (Number)new ArcTan2(real, imag).simplify(null);
	}
	
	public Compare compare(Expr other) {
		if (other instanceof Number) {
			if (other instanceof Complex) {
				if (real.equalTo(((Complex)other).real) && imag.equalTo(((Complex)other).imag)) {
					return EQUAL;
				}
				else {
					return UNEQUAL;
				}
			}
			if (other instanceof Imaginary) {
				if (real.equalTo(ZERO) && imag.equalTo(((Imaginary)other).value)) {
					return EQUAL;
				}
				else {
					return UNEQUAL;
				}
			}
			// other must be real
			if (imag.equalTo(ZERO)) {
				return real.compare(other);
			}
			else {
				return UNEQUAL;
			}
		}
		return UNKNOWN;
	}
	public Compare isZero() {
		return real.isZero().and(imag.isZero());
	}
	public Compare isPositive() {
		return isReal().and(real.isPositive());
	}
	public Compare isNegative() {
		return isReal().and(real.isNegative());
	}
	public Compare isReal() {
		return imag.isZero();
	}
	public Compare isImag() {
		return real.isZero();
	}
	
	public String toString() {
		return real + "+" + imag + "i";
	}
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((imag == null) ? 0 : imag.hashCode());
		result = prime * result + ((real == null) ? 0 : real.hashCode());
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Complex other = (Complex) obj;
		if (imag == null) {
			if (other.imag != null)
				return false;
		} else if (!imag.equals(other.imag))
			return false;
		if (real == null) {
			if (other.real != null)
				return false;
		} else if (!real.equals(other.real))
			return false;
		return true;
	}
	
}
