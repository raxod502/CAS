package com.apprisingsoftware.cas;


class Sgn extends SingleArgumentFunction {
	
	public Sgn(Expr arg) {
		super(arg);
	}
	public Sgn(Expr arg, int derivative) {
		super(arg, derivative);
	}
	
	@Override public Expr getAlternate() {
		if (getArgument().isZero() == YES) {
			return ZERO;
		}
		if (getArgument().isPositive() == YES) {
			return ONE;
		}
		if (getArgument().isNegative() == YES) {
			return NEG_ONE;
		}
		if (getArgument().isImag() == YES) {
			Expr texpr = new Im(getArgument()).getSimplest();
			if (texpr.isPositive() == YES) {
				return I;
			}
			if (texpr.isNegative() == YES) {
				return negI;
			}
		}
		if (getArgument().isComplex() == YES) {
			return new Cis(new ArcTan2(new Re(getArgument()), new Im(getArgument())));
		}
//		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	@Override public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new Sgn(getArgument(), 1).getSimplest();
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("sgn(f(x)) isn't even a well-defined integral! Come on!");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return getArgument().isZero();
	}
	@Override public Compare isPositive() {
		return getArgument().isPositive();
	}
	@Override public Compare isNegative() {
		return getArgument().isNegative();
	}
	@Override public Compare isReal() {
		return getArgument().isReal();
	}
	@Override public Compare isImag() {
		return getArgument().isImag();
	}
	
}
