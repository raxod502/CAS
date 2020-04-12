package cas;

import java.util.HashSet;

public class ComplexExpr extends DoubleArgumentFunction {
	
	public ComplexExpr(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public ComplexExpr(Expr left, Expr right) {
		super(left, right);
	}
	
	public Expr simplifyFunction() {
		// 0 + bi = bi
		if (getReal().isZero() == YES) {
			return new ImaginaryExpr(getImag()).simplify();
		}
		// a + 0i = a
		if (getImag().isZero() == YES) {
			return getReal(); // Already simplified.
		}
		// (ai) + bi = (a+b)i
		if (getReal() instanceof Imaginary) {
			return new ImaginaryExpr(new DualSum(getReal(), getImag())).simplify();
		}
		// a + (bi)i = (a-b)
		if (getImag() instanceof Imaginary) {
			return new Difference(getReal(), getImag()).simplify();
		}
		// (c + di) + bi = c + (b+d)i
		if (getReal() instanceof Complex) {
			return new ComplexExpr(((Complex)getReal()).real, new DualSum(((Complex)getReal()).imag, getImag())).simplify();
		}
		// (c + di) + bi = c + (b+d)i
		if (getReal() instanceof ComplexExpr) {
			return new ComplexExpr(((ComplexExpr)getReal()).getReal(), new DualSum(((ComplexExpr)getReal()).getImag(), getImag())).simplify();
		}
		// a + (c + di)i = (a+c) - di
		if (getImag() instanceof Complex) {
			Complex arg = (Complex)getImag();
			return new ComplexExpr(new DualSum(getReal(), arg.real), arg.imag.negative()).simplify();
		}
		// a + (c + di)i = (a+c) - di
		if (getImag() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getImag();
			return new ComplexExpr(new DualSum(getReal(), arg.getReal()), arg.getImag().negative()).simplify();
		}
		// Cannot simplify
		if (getReal() instanceof Number && getImag() instanceof Number) {
			return new Complex((NumNumSum)getReal(), (NumNumSum)getImag());
		}
		return this;
	}
	public Expr derivative(Variable var) {
		return new ComplexExpr(getReal().derivative(var), getImag().derivative(var)).simplify(null);
	}
	public Expr derivativePartial(int var) {
		if (var == 0) return ONE;
		if (var == 1) return new Imaginary(ONE);
		return ZERO;
	}
	public Expr antiderivative(Variable var) {
		return new ComplexExpr(getReal().antiderivative(var), getImag().antiderivative(var)).simplify(null);
	}
	public Expr conjugate() {
		return new ComplexExpr(getReal(), getImag().negative()).simplify(null);
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return Compare._and(getReal().isZero(), getImag().isZero());
	}
	public Compare isPositive() {
		return Compare._and(isReal(), getReal().isPositive());
	}
	public Compare isNegative() {
		return Compare._and(isReal(), getReal().isNegative());
	}
	public Compare isReal() {
		return getImag().isZero();
	}
	public Compare isImag() {
		return getReal().isZero();
	}
	
	public Expr getReal() { return getLeft(); }
	public Expr getImag() { return getRight(); }
	
}
