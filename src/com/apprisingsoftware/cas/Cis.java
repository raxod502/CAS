package com.apprisingsoftware.cas;


class Cis extends SingleArgumentFunction {
	
	public Cis(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Cis(Expr arg) {
		super(arg);
	}
	
	@Override public Expr getAlternate() {
		// Cis[x] = Cos[x] + i Sin[x]
		return new ComplexExpr(new Cos(getArgument()), new Sin(getArgument()));
	}
	@Override public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new ComplexExpr(new Sin(getArgument()).negative(), new Cos(getArgument())).getSimplest();
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Cannot take the antiderivative of an arbitrary Cis[x] (yet).");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return NO;
	}
	@Override public Compare isPositive() {
		if (getArgument() instanceof Number) {
			return Cos.isDivisible((Number)getArgument(), ZERO) ? YES : NO;
		}
		return getArgument().isZero();
	}
	@Override public Compare isNegative() {
		if (getArgument() instanceof Number) {
			return Cos.isDivisible((Number)getArgument(), pi) ? YES : NO;
		}
		return MAYBE;
	}
	@Override public Compare isReal() {
		return isPositive().or(isNegative());
	}
	@Override public Compare isImag() {
		if (getArgument() instanceof Number) {
			Compare cond1 = Cos.isDivisible((Number)getArgument(), new Multiple(oneHalf, pi)) ? YES : NO;
			Compare cond2 = Cos.isDivisible((Number)getArgument(), new Multiple(negOneHalf, pi)) ? YES : NO;
			return cond1.and(cond2);
		}
		return MAYBE;
	}
	
}
