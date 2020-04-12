package com.apprisingsoftware.cas;


class ComplexExpr extends DoubleArgumentFunction {
	
	public ComplexExpr(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public ComplexExpr(Expr left, Expr right) {
		super(left, right);
	}
	
	@Override public Expr getAlternate() {
		// 0 + bi = bi
		if (getReal().isZero() == YES) {
			return new ImaginaryExpr(getImag());
		}
		// a + 0i = a
		if (getImag().isZero() == YES) {
			return getReal(); // Already simplified.
		}
		// (ai) + bi = (a+b)i
		if (getReal() instanceof Imaginary) {
			return new ImaginaryExpr(new DualSum(getReal(), getImag()));
		}
		// a + (bi)i = (a-b)
		if (getImag() instanceof Imaginary) {
			return new Difference(getReal(), getImag());
		}
		// (c + di) + bi = c + (b+d)i
		if (getReal() instanceof Complex) {
			return new ComplexExpr(((Complex)getReal()).real, new DualSum(((Complex)getReal()).imag, getImag()));
		}
		// (c + di) + bi = c + (b+d)i
		if (getReal() instanceof ComplexExpr) {
			return new ComplexExpr(((ComplexExpr)getReal()).getReal(), new DualSum(((ComplexExpr)getReal()).getImag(), getImag()));
		}
		// a + (c + di)i = (a+c) - di
		if (getImag() instanceof Complex) {
			Complex arg = (Complex)getImag();
			return new ComplexExpr(new DualSum(getReal(), arg.real), arg.imag.negative());
		}
		// a + (c + di)i = (a+c) - di
		if (getImag() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getImag();
			return new ComplexExpr(new DualSum(getReal(), arg.getReal()), arg.getImag().negative());
		}
		// Cannot simplify
		if (getReal() instanceof Number && getImag() instanceof Number) {
			return new Complex((NumNumSum)getReal(), (NumNumSum)getImag());
		}
		return this;
	}
	@Override public Expr derivative(Variable var) {
		return new ComplexExpr(getReal().derivative(var), getImag().derivative(var)).getSimplest();
	}
	@Override public Expr derivativePartial(int var) {
		if (var == 0) return ONE;
		if (var == 1) return new Imaginary(ONE);
		return ZERO;
	}
	@Override public Expr antiderivative(Variable var) {
		return new ComplexExpr(getReal().antiderivative(var), getImag().antiderivative(var)).getSimplest();
	}
	public Expr conjugate() {
		return new ComplexExpr(getReal(), getImag().negative()).getSimplest();
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return Compare._and(getReal().isZero(), getImag().isZero());
	}
	@Override public Compare isPositive() {
		return Compare._and(isReal(), getReal().isPositive());
	}
	@Override public Compare isNegative() {
		return Compare._and(isReal(), getReal().isNegative());
	}
	@Override public Compare isReal() {
		return getImag().isZero();
	}
	@Override public Compare isImag() {
		return getReal().isZero();
	}
	
	public Expr getReal() { return getLeft(); }
	public Expr getImag() { return getRight(); }
	
}
