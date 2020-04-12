package cas;

public class Tan extends SingleArgumentFunction {
	
	public Tan(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Tan(Expr arg) {
		super(arg);
	}
	
	public Expr simplifyFunction() {
		// tan(0) = 1
		if (getArgument().isZero() == YES) {
			return ONE;
		}
		if (getArgument() instanceof Number) {
			Number arg = (Number)getArgument();
			// tan(0) = 0
			if (isDivisible(arg, ZERO))
				return ZERO;
			// tan(π/6) = 1/√3
			if (isDivisible(arg, new Multiple(oneSixth, pi)))
				return new Fraction(ONE, ROOT_THREE);
			// tan(π/4) = 1
			if (isDivisible(arg, new Multiple(oneFourth, pi)))
				return ONE;
			// tan(π/3) = √3
			if (isDivisible(arg, new Multiple(oneThird, pi)))
				return ROOT_THREE;
			// tan(π/2) = undef
			if (isDivisible(arg, new Multiple(oneHalf, pi)))
				throw new IllegalStateException("The tangent of π/2 is undefined (positive ∞).");
			// tan(2π/3) = -√3
			if (isDivisible(arg, new Multiple(twoThirds, pi)))
				return NEG_ROOT_THREE;
			// tan(3π/4) = -1
			if (isDivisible(arg, new Multiple(threeFourths, pi)))
				return NEG_ONE;
			// tan(5π/6) = -1/√3
			if (isDivisible(arg, new Multiple(fiveSixths, pi)))
				return new Fraction(NEG_ONE, ROOT_THREE);
		}
		if (getArgument() instanceof ArcSin) {
			ArcSin arg = (ArcSin)getArgument();
			return new Quotient(arg.getArgument(), new Sqrt(
					new Difference(ONE, new DualExponentiation(arg.getArgument(), TWO)))).simplify();
		}
		if (getArgument() instanceof ArcCos) {
			ArcCos arg = (ArcCos)getArgument();
			return new Quotient(new Sqrt(new Difference(ONE, new DualExponentiation(arg.getArgument(), TWO))), arg.getArgument()).simplify();
		}
		if (getArgument() instanceof ArcTan) {
			ArcTan arg = (ArcTan)getArgument();
			return arg.getArgument();
		}
		if (getArgument() instanceof Negative) {
			return new Tan(((Negative)getArgument()).getArgument()).negative().simplify();
		}
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	/**
	 * Returns true if arg % π = div, and false otherwise.
	 */
	public static boolean isDivisible(Number arg, Number div) {
		return ((Number)new Difference(arg, div).simplify()).isDivisibleBy(pi);
	}
	public Expr derivativePartial(int var) {
		return new DualExponentiation(new Cos(getArgument()), TWO).inverse().simplify();
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
		return getArgument().isReal();
	}
	public Compare isImag() {
		return getArgument() instanceof Number ? (
				(Tan.isDivisible((Number)getArgument(), ZERO) ? YES : NO).or
				(Tan.isDivisible((Number)getArgument(), new Multiple(oneHalf, pi)) ? YES : NO)) :
					MAYBE;
	}
	
}
