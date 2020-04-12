package com.apprisingsoftware.cas;


class ArcTan extends SingleArgumentFunction {
	
	public ArcTan(Expr arg) {
		super(arg);
	}
	public ArcTan(Expr arg, int derivative) {
		super(arg, derivative);
	}
	
	@Override public Expr getAlternate() {
		Expr arg = getArgument();
		// atan(0) = 0
		if (arg.isZero() == YES) {
			return ZERO;
		}
		// atan(1) = π/4
		if (arg.equalTo(ONE)) {
			return new Multiple(oneHalf, pi);
		}
		// atan(-1) = -π/4
		if (arg.equalTo(NEG_ONE)) {
			return new Multiple(negOneHalf, pi);
		}
		// atan(√3) = π/3
		if (arg.equalTo(ROOT_THREE)) {
			return new Multiple(oneThird, pi);
		}
		// atan(-√3) = -π/3
		if (arg.equalTo(NEG_ROOT_THREE)) {
			return new Multiple(negOneThird, pi);
		}
		// atan(1/√3) = π/6
		if (arg.equalTo(new Fraction(ONE, ROOT_THREE))) {
			return new Multiple(oneSixth, pi);
		}
		// atan(-1/√3) = -π/6
		if (arg.equalTo(new Fraction(NEG_ONE, ROOT_THREE))) {
			return new Multiple(negOneSixth, pi);
		}
		
//		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	@Override public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new DualSum(getArgument().square(), ONE).inverse().getSimplest();
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Can't integrate atan yet, yo.");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return getArgument().isZero();
	}
	@Override public Compare isPositive() {
		return isReal().and(getArgument().isPositive());
	}
	@Override public Compare isNegative() {
		return isReal().and(getArgument().isNegative());
	}
	@Override public Compare isReal() {
		// Certified by Grapher!
		return getArgument().isReal();
	}
	@Override public Compare isImag() {
		// (Sort of) certified by Grapher!
		Expr value = new Im(getArgument()).getSimplest();
		return getArgument().isImag().and(value.lessThanOrEqualTo(ONE), value.greaterThanOrEqualTo(NEG_ONE));
	}
	
}
