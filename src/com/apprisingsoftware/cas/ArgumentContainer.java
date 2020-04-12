package com.apprisingsoftware.cas;

import java.util.ArrayList;

class ArgumentContainer extends Function {
	
	public ArgumentContainer(ArrayList<Expr> args) {
		super(args);
	}
	public ArgumentContainer(Expr... args) {
		super(args);
	}
	public ArgumentContainer(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public ArgumentContainer(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	
	@Override public Expr getAlternate() {
		return this;
	}
	@Override public Expr derivativePartial(int var)  {
		throw new UnsupportedOperationException("Cannot take the derivative of an ArgumentContainer. Even if it is the partial derivative.");
	}
	@Override public Expr antiderivative(Variable var)  {
		throw new UnsupportedOperationException("Cannot take the antiderivative of an ArgumentContainer.");
	}
	
	@Override public Compare compare(Expr other) {
		if (!(other instanceof ArgumentContainer)) return UNEQUAL;
		ArgumentContainer otherAC = (ArgumentContainer)other;
		if (args.size() != otherAC.args.size()) return UNEQUAL;
		for (int i=0; i<args.size(); i++) {
			Compare compare = args.get(i).compare(otherAC.args.get(i));
			if (compare != EQUAL) return UNEQUAL;
		}
		return EQUAL;
	}
	@Override public Compare isZero() { return NO; }
	@Override public Compare isPositive() { return NO; }
	@Override public Compare isNegative() { return NO; }
	@Override public Compare isReal() { return NO; }
	@Override public Compare isImag() { return NO; }
	
	@Override public int getMinimumArguments() { return -1; }
	@Override public int getMaximumArguments() { return -1; }
	
}
