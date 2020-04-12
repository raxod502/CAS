package com.apprisingsoftware.cas;


abstract class DoubleArgumentFunction extends Function {
	
	public DoubleArgumentFunction(Expr left, Expr right) {
		super(left, right);
	}
	public DoubleArgumentFunction(int derivativeLeft, int derivativeRight, Expr left, Expr right) {
		super(new int[] {derivativeLeft, derivativeRight}, left, right);
	}
	
	@Override public abstract Expr getAlternate();
	
	@Override public Expr derivative(Variable var) {
		if (args.get(0) instanceof Number && args.get(1) instanceof Number) {
			return ZERO;
		}
		// d/dz f(x, y) = dx/dz df/dx + dy/dz df/dy
		return new DualSum(new DualProduct(getLeft().derivative(var), derivativePartial(0)),
				new DualProduct(getRight().derivative(var), derivativePartial(1))).getSimplest();
	}
	@Override public abstract Expr derivativePartial(int var);
	@Override public abstract Expr antiderivative(Variable var);
	
	@Override public abstract Compare compare(Expr other);
	@Override public abstract Compare isZero();
	@Override public abstract Compare isPositive();
	@Override public abstract Compare isNegative();
	@Override public abstract Compare isReal();
	@Override public abstract Compare isImag();
	
	@Override public final int getMinimumArguments() { return 2; }
	@Override public final int getMaximumArguments() { return 2; }
	public final Expr getLeft() { return args.get(0); }
	public final Expr getRight() { return args.get(1); }
	
}
