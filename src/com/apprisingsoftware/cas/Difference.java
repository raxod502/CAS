package com.apprisingsoftware.cas;


public final class Difference extends DoubleArgumentFunction {
	
	public Difference(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public Difference(Expr left, Expr right) {
		super(left, right);
	}
	
	@Override public Expr getAlternate()  {
		// a - a = 0
		if (getLeft().equalTo(getRight())) {
			return ZERO;
		}
		// x - 0 = x
		if (getRight().isZero() == YES) {
			return getLeft();
		}
		// 0 - x = -x
		if (getLeft().isZero() == YES) {
			return getRight().negative().getSimplest();
		}
		// a - b = a + (-b)
		return new DualSum(getLeft(), getRight().negative());
	}
	@Override public Expr derivative(Variable var)  {
		return new Difference(getLeft().derivative(var), getRight().derivative(var)).getSimplest();
	}
	@Override public Expr derivativePartial(int var) {
		if (var == 0) return ONE;
		if (var == 1) return NEG_ONE;
		return ZERO;
	}
	@Override public Expr antiderivative(Variable var)  {
		return new Difference(getLeft().antiderivative(var), getRight().derivative(var)).getSimplest();
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		Expr left = getLeft(), right = getRight();
		if (left.isZero() == YES && right.isZero() == YES) return YES;
		if (left.isPositive() == YES && right.isNegative() == YES ||
				left.isNegative() == YES && right.isPositive() == YES) return NO;
		return MAYBE;
	}
	@Override public Compare isPositive() {
		Expr left = getLeft(), right = getRight();
		if ((left.isZero() == YES || left.isNegative() == YES) &&
				(right.isZero() == YES || right.isPositive() == YES)) return NO;
		if (left.isPositive() == YES && right.isNegative() == YES) return YES;
		return MAYBE;
	}
	@Override public Compare isNegative() {
		Expr left = getLeft(), right = getRight();
		if ((left.isZero() == YES || left.isPositive() == YES) &&
				(right.isZero() == YES || right.isNegative() == YES)) return NO;
		if (left.isNegative() == YES && right.isPositive() == YES) return YES;
		return MAYBE;
	}
	@Override public Compare isReal() {
		Compare leftReal = getLeft().isReal();
		Compare rightReal = getRight().isReal();
		if (leftReal == YES && rightReal == YES) return YES;
		if (leftReal == YES && rightReal == NO ||
				leftReal == NO && rightReal == YES) return NO;
		return MAYBE;
	}
	@Override public Compare isImag() {
		Compare leftImag = getLeft().isImag();
		Compare rightImag = getRight().isImag();
		if (leftImag == YES && rightImag == YES) return YES;
		if (leftImag == YES && rightImag == NO ||
				leftImag == NO && rightImag == YES) return NO;
		return MAYBE;
	}
	
	@Override public String toString() {
		String left = getLeft().toString(),
				right = getRight().toString();
		return left + " - " + right;
	}
	
}
