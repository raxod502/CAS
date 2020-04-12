package cas;

import java.util.HashSet;

public class Cos extends SingleArgumentFunction {
	
	public Cos(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Cos(Expr arg) {
		super(arg);
	}
	
	public Expr simplifyFunction(HashSet<Expr> eq) {
		// cos(0) = 1
		if (getArgument().isZero() == YES) {
			return ONE;
		}
		if (getArgument() instanceof Number) {
			Number arg = (Number)getArgument();
			// cos(0) = 1
			if (isDivisible(arg, ZERO))
				return ONE;
			// cos(π/6) = √3/2
			if (isDivisible(arg, new Multiple(oneSixth, pi)))
				return new Fraction(ROOT_THREE, TWO);
			// cos(π/3) = 1/2
			if (isDivisible(arg, new Multiple(oneThird, pi)))
				return oneHalf;
			// cos(π/2) = 0
			if (isDivisible(arg, new Multiple(oneHalf, pi)))
				return ZERO;
			// cos(2π/3) = -1/2
			if (isDivisible(arg, new Multiple(twoThirds, pi)))
				return negOneHalf;
			// cos(5π/6) = -√3/2
			if (isDivisible(arg, new Multiple(fiveSixths, pi)))
				return new Fraction(NEG_ROOT_THREE, TWO);
			// cos(π) = -1
			if (isDivisible(arg, pi))
				return NEG_ONE;
			// cos(7π/6) = -√3/2
			if (isDivisible(arg, new Multiple(new Fraction(SEVEN, SIX), pi)))
				return new Fraction(NEG_ROOT_THREE, TWO);
			// cos(4π/3) = -1/2
			if (isDivisible(arg, new Multiple(new Fraction(FOUR, THREE), pi)))
				return negOneHalf;
			// cos(3π/2) = 0
			if (isDivisible(arg, new Multiple(new Fraction(THREE, TWO), pi)))
				return ZERO;
			// cos(5π/3) = 1/2
			if (isDivisible(arg, new Multiple(new Fraction(FIVE, THREE), pi)))
				return oneHalf;
			// cos(11π/6) = √3/2
			if (isDivisible(arg, new Multiple(new Fraction(ELEVEN, SIX), pi)))
				return new Fraction(ROOT_THREE, TWO);
			// cos(π/4) = 1/√2
			if (isDivisible(arg, new Multiple(oneFourth, pi)))
				return new Fraction(ONE, ROOT_TWO);
			// cos(3π/4) = -1/√2
			if (isDivisible(arg, new Multiple(threeFourths, pi)))
				return new Fraction(NEG_ONE, ROOT_TWO);
			// cos(5π/4) = -1/√2
			if (isDivisible(arg, new Multiple(new Fraction(FIVE, FOUR), pi)))
				return new Fraction(NEG_ONE, ROOT_TWO);
			// cos(7π/4) = 1/√2
			if (isDivisible(arg, new Multiple(new Fraction(SEVEN, FOUR), pi)))
				return new Fraction(ONE, ROOT_TWO);
		}
		if (getArgument() instanceof ArcSin) {
			ArcSin arg = (ArcSin)getArgument();
			return new Sqrt(new Difference(ONE, new DualExponentiation(arg.getArgument(), TWO))).simplify(eq);
		}
		if (getArgument() instanceof ArcCos) {
			ArcCos arg = (ArcCos)getArgument();
			return arg.getArgument();
		}
		if (getArgument() instanceof ArcTan) {
			ArcTan arg = (ArcTan)getArgument();
			return new Sqrt(new DualSum(ONE, new DualExponentiation(arg.getArgument(), TWO))).inverse().simplify(eq);
		}
		if (getArgument() instanceof Negative) {
			return new Cos(((Negative)getArgument()).getArgument()).simplify(eq);
		}
		
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	/**
	 * Returns true if arg % 2π = div, and false otherwise.
	 */
	public static boolean isDivisible(Number arg, Number div) {
		return ((Number)new Difference(arg, div).simplify(null)).isDivisibleBy(new Multiple(TWO, pi));
	}
	public Expr derivativePartial(int var) {
		return new Sin(getArgument()).negative().simplify(null);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Do not know how to take the indefinite integral of an arbitrary Cos[x] dx (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
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
	public Compare isPositive() {
		return MAYBE; // Oh dear...
	}
	public Compare isNegative() {
		return MAYBE;
	}
	public Compare isReal() {
		Expr re = new Re(getArgument()).simplify(null), im = new Im(getArgument()).simplify(null);
		if (re instanceof Number) {
			return im.isZero().or(Tan.isDivisible((Number)re, new Multiple(oneHalf, pi)) ? YES : NO);
		}
		return im.isZero();
	}
	public Compare isImag() {
		if (getArgument().isImag() == YES) return YES;
		Expr re = new Re(getArgument()).simplify(null);
		if (re instanceof Number) {
			return Tan.isDivisible((Number)re, ZERO) ? YES : NO;
		}
		return MAYBE;
	}
	
}
