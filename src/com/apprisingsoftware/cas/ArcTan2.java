package com.apprisingsoftware.cas;


// ArcTan2 is undefined for complex arguments
class ArcTan2 extends DoubleArgumentFunction {
	
	public ArcTan2(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public ArcTan2(Expr left, Expr right) {
		super(left, right);
	}
	
	@Override public Expr getAlternate() {
		Expr left = getLeft(), right = getRight();
		Sqrt rootThree = new Sqrt(THREE);
		Expr negRootThree = rootThree.negative().getSimplest();
		// atan(0, 0) = undef
		if (left.isZero() == YES && right.isZero() == YES) {
			throw new AssertionError("ArcTan2(0, 0) is undefined.");
		}
		// atan(+x, 0) = 0
		if (left.isPositive() == YES && right.isZero() == YES) {
			return ZERO;
		}
		// atan(-x, 0) = π (max)
		if (left.isNegative() == YES && right.isZero() == YES) {
			return pi;
		}
		// atan(0, +x) = π/2
		if (left.isZero() == YES && right.isPositive() == YES) {
			return new Multiple(oneHalf, pi);
		}
		// atan(0, -x) = -π/2
		if (left.isZero() == YES && right.isNegative() == YES) {
			return new Multiple(negOneHalf, pi);
		}
		// atan(+x, +x) = π/4
		if (left.equalTo(right) && left.isPositive() == YES) {
			return new Multiple(oneFourth, pi);
		}
		// atan(-x, +x) = 3π/4
		if (left.negative().getSimplest().equalTo(right) && left.isNegative() == YES) {
			return new Multiple(threeFourths, pi);
		}
		// atan(+x, -x) = -π/4
		if (right.negative().getSimplest().equalTo(left) && right.isNegative() == YES) {
			return new Multiple(negOneFourth, pi);
		}
		// atan(-x, -x) = -3π/4
		if (left.equalTo(right) && left.isNegative() == YES) {
			return new Multiple(negThreeFourths, pi);
		}
		// atan(+x√3, +x) = π/6
		if (new DualProduct(right, rootThree).getSimplest().equalTo(left) && right.isPositive() == YES) {
			return new Multiple(oneSixth, pi);
		}
		// atan(+x, +x√3) = π/3
		if (new DualProduct(left, rootThree).getSimplest().equalTo(right) && left.isPositive() == YES) {
			return new Multiple(oneThird, pi);
		}
		// atan(-x, +x√3) = 2π/3
		if (new DualProduct(left, negRootThree).getSimplest().equalTo(right) && left.isNegative() == YES) {
			return new Multiple(twoThirds, pi);
		}
		// atan(-x√3, +x) = 5π/6
		if (new DualProduct(right, negRootThree).getSimplest().equalTo(left) && right.isPositive() == YES) {
			return new Multiple(fiveSixths, pi);
		}
		// atan(-x√3, -x) = -5π/6
		if (new DualProduct(right, rootThree).getSimplest().equalTo(left) && right.isNegative() == YES) {
			return new Multiple(negFiveSixths, pi);
		}
		// atan(-x, -x√3) = -2π/3
		if (new DualProduct(left, rootThree).getSimplest().equalTo(right) && left.isNegative() == YES) {
			return new Multiple(negTwoThirds, pi);
		}
		// atan(+x, -x√3) = -π/3
		if (new DualProduct(left, negRootThree).getSimplest().equalTo(right) && left.isPositive() == YES) {
			return new Multiple(negOneThird, pi);
		}
		// atan(+x√3, -x) = -π/6
		if (new DualProduct(right, negRootThree).getSimplest().equalTo(left) && right.isNegative() == YES) {
			return new Multiple(negOneSixth, pi);
		}
		if (left.isReal() == YES && right.isReal() == YES) {
			// atan(x, y) = atan(y/x)
			if (left.isPositive() == YES) {
				return new ArcTan(new Quotient(right, left));
			}
			// atan(x, y) = atan(y/x) + π
			if (left.isNegative() == YES && right.isNegative() == NO) {
				return new DualSum(new ArcTan(new Quotient(right, left)), pi);
			}
			// atan(x, y) = atan(y/x) - π
			if (left.isNegative() == YES && right.isNegative() == YES) {
				return new Difference(new ArcTan(new Quotient(right, left)), pi);
			}
		}
		
//		if (getLeft() instanceof Number && getRight() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	@Override public Expr derivative(Variable var) {
		Expr x = getLeft(), y = getRight();
		return new Quotient(new Difference(new DualProduct(x, y.derivative(var)), new DualProduct(x.derivative(var), y)),
				new DualSum(new DualExponentiation(x, TWO), new DualExponentiation(y, TWO))).getSimplest();
	}
	@Override public Expr derivativePartial(int var) {
		Expr x = getLeft(), y = getRight();
		if (var == 0) {
			return new Quotient(y.negative(), new DualSum(x.square(), y.square())).getSimplest();
		}
		if (var == 1) {
			return new Quotient(x, new DualSum(x.square(), y.square())).getSimplest();
		}
		return ZERO;
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Don't know how to the take the antiderivative of an arbitrary atan2(x, y).");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return Compare._and(getLeft().isZero().not(), getRight().isPositive());
	}
	@Override public Compare isPositive() {
		return Compare._or(getRight().isPositive(), Compare._and(getLeft().isNegative(), getRight().isZero()));
	}
	@Override public Compare isNegative() {
		return getRight().isNegative();
	}
	@Override public Compare isReal() {
		return YES;
	}
	@Override public Compare isImag() {
		return NO;
	}
	
}
