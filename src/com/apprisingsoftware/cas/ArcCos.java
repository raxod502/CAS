package com.apprisingsoftware.cas;


class ArcCos extends SingleArgumentFunction {
	
	public ArcCos(Expr arg) {
		super(arg);
	}
	public ArcCos(Expr arg, int derivative) {
		super(arg, derivative);
	}
	
	@Override public Expr getAlternate() {
		// acos(-1) = π
		Expr arg = getArgument();
		if (arg.equalTo(NEG_ONE)) {
			return pi;
		}
		// acos(-√3/2) = 5π/6
		if (arg.equalTo(new Fraction(NEG_ROOT_THREE, TWO))) {
			return new Multiple(fiveSixths, pi);
		}
		// acos(-√2/2) = 3π/4
		if (arg.equalTo(new Fraction(NEG_ROOT_TWO, TWO))) {
			return new Multiple(threeFourths, pi);
		}
		// acos(-1/2) = 2π/3
		if (arg.equalTo(negOneHalf)) {
			return new Multiple(twoThirds, pi);
		}
		// acos(0) = π/2
		if (arg.isZero() == YES) {
			return new Multiple(oneHalf, pi);
		}
		// acos(1/2) = π/3
		if (arg.equalTo(oneHalf)) {
			return new Multiple(oneThird, pi);
		}
		// acos(√2/2) = π/4
		if (arg.equalTo(new Fraction(ROOT_TWO, TWO))) {
			return new Multiple(oneFourth, pi);
		}
		// acos(√3/2) = π/6
		if (arg.equalTo(new Fraction(ROOT_THREE, TWO))) {
			return new Multiple(oneSixth, pi);
		}
		// acos(1) = 0
		if (arg.equalTo(ONE)) {
			return ZERO;
		}
		
//		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	
	@Override public Expr derivativePartial(int var) {
		if (var == 0) return new Sqrt(new Difference(ONE, getArgument().square())).inverse().negative().getSimplest();
		return ZERO;
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Don't know how to take the antiderivative of an arbitrary ArcCos (yet).");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return getArgument()._equalTo(ONE);
	}
	@Override public Compare isPositive() {
		return isReal().and(isZero().not());
	}
	@Override public Compare isNegative() {
		return NO;
	}
	@Override public Compare isReal() {
		// Certified by Wolfram|Alpha!
		Compare cond1 = getArgument().isReal();
		Compare cond2 = getArgument().greaterThanOrEqualTo(NEG_ONE);
		Compare cond3 = getArgument().lessThanOrEqualTo(ONE);
		if (cond1 == MAYBE || cond2 == MAYBE || cond3 == MAYBE) return MAYBE;
		if (cond1 == YES && cond2 == YES && cond3 == YES) return YES;
		return NO;
	}
	@Override public Compare isImag() {
		// Certified by Grapher!
		return new Re(getArgument()).getSimplest().greaterThanOrEqualTo(ONE);
	}
	
}
