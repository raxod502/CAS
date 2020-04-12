package com.apprisingsoftware.cas;


abstract class Number extends Expr {
	
	@Override public final Expr getAlternate() {
		return this;
	}
	public final Expr reduce() {
		return this; // Todo if necessary
	}
	@Override public final Expr derivative(Variable var) {
		return ZERO;
	}
	@Override public final Expr antiderivative(Variable var) {
		return new DualProduct(this, var).getSimplest();
	}
	public final boolean isDivisibleBy(Number other) {
		Number res = (Number)new Quotient(this, other).getSimplest();
		return res instanceof Integer;
	}
	public abstract ComplexDouble complexdoubleValue();
	
	@Override public abstract Compare compare(Expr other);
	@Override public abstract Compare isZero();
	@Override public abstract Compare isPositive();
	@Override public abstract Compare isNegative();
	@Override public abstract Compare isReal();
	@Override public abstract Compare isImag();
	
	@Override public abstract String toString();
	
	@Override public final int nodeCount() {
		return 1;
	}
	
}
