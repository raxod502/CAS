package cas;

import java.util.HashSet;

public class Sin extends SingleArgumentFunction {
	
	public Sin(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Sin(Expr arg) {
		super(arg);
	}
	
	public Expr simplifyFunction(HashSet<Expr> eq) {
		// sin(0) = 0
		if (getArgument().isZero() == YES) {
			return ZERO;
		}
		if (getArgument() instanceof Number) {
			Number arg = (Number)getArgument();
			// sin(0) = 0
			if (isDivisible(arg, ZERO))
				return ZERO;
			// sin(π/6) = 1/2
			if (isDivisible(arg, new Multiple(oneSixth, pi)))
				return oneHalf;
			// sin(π/3) = √3/2
			if (isDivisible(arg, new Multiple(oneThird, pi)))
				return new Fraction(ROOT_THREE, TWO);
			// sin(π/2) = 1
			if (isDivisible(arg, new Multiple(oneHalf, pi)))
				return ONE;
			// sin(2π/3) = √3/2
			if (isDivisible(arg, new Multiple(twoThirds, pi)))
				return new Fraction(ROOT_THREE, TWO);
			// sin(5π/6) = 1/2
			if (isDivisible(arg, new Multiple(fiveSixths, pi)))
				return oneHalf;
			// sin(π) = 0
			if (isDivisible(arg, pi))
				return ZERO;
			// sin(7π/6) = -1/2
			if (isDivisible(arg, new Multiple(new Fraction(SEVEN, SIX), pi)))
				return negOneHalf;
			// sin(4π/3) = -√3/2
			if (isDivisible(arg, new Multiple(new Fraction(FOUR, THREE), pi)))
				return new Fraction(NEG_ROOT_THREE, TWO);
			// sin(3π/2) = -1
			if (isDivisible(arg, new Multiple(new Fraction(THREE, TWO), pi)))
				return NEG_ONE;
			// sin(5π/3) = -√3/2
			if (isDivisible(arg, new Multiple(new Fraction(FIVE, THREE), pi)))
				return new Fraction(NEG_ROOT_THREE, TWO);
			// sin(11π/6) = -1/2
			if (isDivisible(arg, new Multiple(new Fraction(ELEVEN, SIX), pi)))
				return negOneHalf;
			// sin(π/4) = 1/√2
			if (isDivisible(arg, new Multiple(oneFourth, pi)))
				return new Fraction(ONE, ROOT_TWO);
			// sin(3π/4) = 1/√2
			if (isDivisible(arg, new Multiple(threeFourths, pi)))
				return new Fraction(ONE, ROOT_TWO);
			// sin(5π/4) = -1/√2
			if (isDivisible(arg, new Multiple(new Fraction(FIVE, FOUR), pi)))
				return new Fraction(NEG_ONE, ROOT_TWO);
			// sin(7π/4) = -1/√2
			if (isDivisible(arg, new Multiple(new Fraction(SEVEN, FOUR), pi)))
				return new Fraction(NEG_ONE, ROOT_TWO);
		}
		if (getArgument() instanceof ArcSin) {
			ArcSin arg = (ArcSin)getArgument();
			return arg.getArgument();
		}
		if (getArgument() instanceof ArcCos) {
			ArcCos arg = (ArcCos)getArgument();
			return new Sqrt(new Difference(ONE, new DualExponentiation(arg.getArgument(), TWO))).simplify(eq);
		}
		if (getArgument() instanceof ArcTan) {
			ArcTan arg = (ArcTan)getArgument();
			return new Quotient(arg.getArgument(),
					new Sqrt(new DualSum(new DualExponentiation(arg.getArgument(), TWO), ONE))).simplify(eq);
		}
		if (getArgument() instanceof Negative) {
			return new Sin(((Negative)getArgument()).getArgument()).negative().simplify(eq);
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
		return new Cos(getArgument()).simplify(null);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Do not know how to take the indefinite integral of an arbitrary Sin[x] dx (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		if (getArgument() instanceof Number) {
			return Tan.isDivisible((Number)getArgument(), ZERO) ? YES : NO;
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
		return getArgument().isReal().or(
				getArgument() instanceof Number ? (
						Tan.isDivisible((Number)getArgument(), new Multiple(oneHalf, pi)) ? YES : NO) : MAYBE);
	}
	public Compare isImag() {
		return getArgument() instanceof Number ? (
				Tan.isDivisible((Number)getArgument(), ZERO) ? YES : NO) : MAYBE;
	}
	
}
