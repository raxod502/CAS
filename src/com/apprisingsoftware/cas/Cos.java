package com.apprisingsoftware.cas;


class Cos extends SingleArgumentFunction {
	
	public Cos(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Cos(Expr arg) {
		super(arg);
	}
	
	@Override public Expr getAlternate() {
		// cos(0) = 1
		if (getArgument().isZero() == YES) {
			return ONE;
		}
		if (getArgument() instanceof Number) {
			Number arg = (Number)getArgument();
			// cos(0) = 1
			RecursionPrint.log("Testing cos(0):");
			if (isDivisible(arg, ZERO))
				return ONE;
			RecursionPrint.log("Finished testing cos(0).");
			// cos(π/6) = √3/2
			RecursionPrint.log("Testing cos(π/6):");
			if (isDivisible(arg, new Multiple(oneSixth, pi)))
				return new Fraction(ROOT_THREE, TWO);
			RecursionPrint.log("Finished testing cos(π/6).");
			// cos(π/3) = 1/2
			RecursionPrint.log("Testing cos(π/3):");
			if (isDivisible(arg, new Multiple(oneThird, pi)))
				return oneHalf;
			RecursionPrint.log("Finished testing cos(π/3).");
			// cos(π/2) = 0
			RecursionPrint.log("Testing cos(π/2):");
			if (isDivisible(arg, new Multiple(oneHalf, pi)))
				return ZERO;
			RecursionPrint.log("Finished testing cos(π/2).");
			// cos(2π/3) = -1/2
			RecursionPrint.log("Testing cos(2π/3):");
			if (isDivisible(arg, new Multiple(twoThirds, pi)))
				return negOneHalf;
			RecursionPrint.log("Finished testing cos(2π/3).");
			// cos(5π/6) = -√3/2
			RecursionPrint.log("Testing cos(5π/6):");
			if (isDivisible(arg, new Multiple(fiveSixths, pi)))
				return new Fraction(NEG_ROOT_THREE, TWO);
			RecursionPrint.log("Finished testing cos(5π/6).");
			// cos(π) = -1
			RecursionPrint.log("Testing cos(π):");
			if (isDivisible(arg, pi))
				return NEG_ONE;
			RecursionPrint.log("Finished testing cos(π).");
			// cos(7π/6) = -√3/2
			RecursionPrint.log("Testing cos(7π/6):");
			if (isDivisible(arg, new Multiple(new Fraction(SEVEN, SIX), pi)))
				return new Fraction(NEG_ROOT_THREE, TWO);
			RecursionPrint.log("Finished testing cos(7π/6).");
			// cos(4π/3) = -1/2
			RecursionPrint.log("Testing cos(4π/3):");
			if (isDivisible(arg, new Multiple(new Fraction(FOUR, THREE), pi)))
				return negOneHalf;
			RecursionPrint.log("Finished testing cos(4π/3).");
			// cos(3π/2) = 0
			RecursionPrint.log("Testing cos(3π/2):");
			if (isDivisible(arg, new Multiple(new Fraction(THREE, TWO), pi)))
				return ZERO;
			RecursionPrint.log("Finished testing cos(3π/2).");
			// cos(5π/3) = 1/2
			RecursionPrint.log("Testing cos(5π/3):");
			if (isDivisible(arg, new Multiple(new Fraction(FIVE, THREE), pi)))
				return oneHalf;
			RecursionPrint.log("Finished testing cos(5π/3).");
			// cos(11π/6) = √3/2
			RecursionPrint.log("Testing cos(11π/6):");
			if (isDivisible(arg, new Multiple(new Fraction(ELEVEN, SIX), pi)))
				return new Fraction(ROOT_THREE, TWO);
			RecursionPrint.log("Finished testing cos(11π/6).");
			// cos(π/4) = 1/√2
			RecursionPrint.log("Testing cos(π/4):");
			if (isDivisible(arg, new Multiple(oneFourth, pi)))
				return new Fraction(ONE, ROOT_TWO);
			RecursionPrint.log("Finished testing cos(π/4).");
			// cos(3π/4) = -1/√2
			RecursionPrint.log("Testing cos(3π/4):");
			if (isDivisible(arg, new Multiple(threeFourths, pi)))
				return new Fraction(NEG_ONE, ROOT_TWO);
			RecursionPrint.log("Finished testing cos(3π/4).");
			// cos(5π/4) = -1/√2
			RecursionPrint.log("Testing cos(5π/4):");
			if (isDivisible(arg, new Multiple(new Fraction(FIVE, FOUR), pi)))
				return new Fraction(NEG_ONE, ROOT_TWO);
			RecursionPrint.log("Finished testing cos(5π/4).");
			// cos(7π/4) = 1/√2
			RecursionPrint.log("Testing cos(7π/4):");
			if (isDivisible(arg, new Multiple(new Fraction(SEVEN, FOUR), pi)))
				return new Fraction(ONE, ROOT_TWO);
			RecursionPrint.log("Finished testing cos(7π/4).");
		}
		if (getArgument() instanceof ArcSin) {
			ArcSin arg = (ArcSin)getArgument();
			return new Sqrt(new Difference(ONE, new DualExponentiation(arg.getArgument(), TWO)));
		}
		if (getArgument() instanceof ArcCos) {
			ArcCos arg = (ArcCos)getArgument();
			return arg.getArgument();
		}
		if (getArgument() instanceof ArcTan) {
			ArcTan arg = (ArcTan)getArgument();
			return new Sqrt(new DualSum(ONE, new DualExponentiation(arg.getArgument(), TWO))).inverse();
		}
		if (getArgument() instanceof Negative) {
			return new Cos(((Negative)getArgument()).getArgument());
		}
		
//		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	/**
	 * Returns true if arg % 2π = div, and false otherwise.
	 */
	public static boolean isDivisible(Number arg, Number div) {
		return ((Number)new Difference(arg, div).getSimplest()).isDivisibleBy(new Multiple(TWO, pi));
	}
	@Override public Expr derivativePartial(int var) {
		return new Sin(getArgument()).negative().getSimplest();
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Do not know how to take the indefinite integral of an arbitrary Cos[x] dx (yet).");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		if (getArgument() instanceof Number) {
			if (isDivisible((Number)getArgument(), new Multiple(oneHalf, pi)) ||
				isDivisible((Number)getArgument(), new Multiple(new Fraction(THREE, TWO), pi))) {
				return YES;
			}
			else {
				return NO;
			}
		}
		return getArgument().isZero();
	}
	@Override public Compare isPositive() {
		return MAYBE; // Oh dear...
	}
	@Override public Compare isNegative() {
		return MAYBE;
	}
	@Override public Compare isReal() {
		Expr re = new Re(getArgument()).getSimplest(), im = new Im(getArgument()).getSimplest();
		if (re instanceof Number) {
			return im.isZero().or(Tan.isDivisible((Number)re, new Multiple(oneHalf, pi)) ? YES : NO);
		}
		return im.isZero();
	}
	@Override public Compare isImag() {
		if (getArgument().isImag() == YES) return YES;
		Expr re = new Re(getArgument()).getSimplest();
		if (re instanceof Number) {
			return Tan.isDivisible((Number)re, ZERO) ? YES : NO;
		}
		return MAYBE;
	}
	
}
