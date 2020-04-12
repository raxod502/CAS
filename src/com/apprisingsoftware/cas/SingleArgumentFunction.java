package com.apprisingsoftware.cas;


abstract class SingleArgumentFunction extends Function {
	
	public SingleArgumentFunction(Expr arg) {
		super(arg);
	}
	public SingleArgumentFunction(Expr arg, int derivative) {
		super(new int[] {derivative}, arg);
	}
	
	@Override public abstract Expr getAlternate();
	// Chain rule
	@Override public Expr derivative(Variable var) {
		return new DualProduct(getArgument().derivative(var), derivativePartial(0)).getSimplest();
	}
	@Override public abstract Expr derivativePartial(int var);
	@Override public abstract Expr antiderivative(Variable var);
	
	@Override public abstract Compare compare(Expr other);
	@Override public abstract Compare isZero();
	@Override public abstract Compare isPositive();
	@Override public abstract Compare isNegative();
	@Override public abstract Compare isReal();
	@Override public abstract Compare isImag();
	
	@Override public final int getMinimumArguments() { return 1; }
	@Override public final int getMaximumArguments() { return 1; }
	public final Expr getArgument() { return args.get(0); }
	
}
