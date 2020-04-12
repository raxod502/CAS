package com.apprisingsoftware.cas;


class ImaginaryExpr extends SingleArgumentFunction {
	
	public ImaginaryExpr(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public ImaginaryExpr(Expr arg) {
		super(arg);
	}
	
	@Override public Expr getAlternate() {
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getArgument();
			return new ComplexExpr(arg.getImag().negative(), arg.getReal());
		}
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return new Complex((NumNumSum)arg.imag.negative().getSimplest(), arg.real);
		}
		if (getArgument() instanceof ImaginaryExpr) {
			ImaginaryExpr arg = (ImaginaryExpr)getArgument();
			return arg.getArgument().negative();
		}
		if (getArgument() instanceof Imaginary) {
			Imaginary arg = (Imaginary)getArgument();
			return arg.value.negative();
		}
		if (getArgument() instanceof Cis) {
			Cis arg = (Cis)getArgument();
			return new Sin(arg.getArgument());
		}
		if (getArgument() instanceof Number) {
			return new Imaginary((NumNumSum)getArgument()).reduce();
		}
		return this;
	}
	@Override public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new ImaginaryExpr(getArgument());
	}
	@Override public Expr antiderivative(Variable var) {
		return new ImaginaryExpr(getArgument().antiderivative(var));
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return getArgument().isZero();
	}
	@Override public Compare isPositive() {
		return isReal().and(new Im(getArgument()).getSimplest().isPositive());
	}
	@Override public Compare isNegative() {
		return isReal().and(new Im(getArgument()).getSimplest().isNegative());
	}
	@Override public Compare isReal() {
		return getArgument().isImag();
	}
	@Override public Compare isImag() {
		return getArgument().isReal();
	}
	
	public Expr getReal() { return getArgument(); }
	
}
