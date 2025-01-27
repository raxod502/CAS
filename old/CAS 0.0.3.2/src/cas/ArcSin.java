package cas;

import java.util.HashSet;

public class ArcSin extends SingleArgumentFunction {
	
	public ArcSin(Expr arg) {
		super(arg);
	}
	public ArcSin(Expr arg, int derivative) {
		super(arg, derivative);
	}
	
	public Expr simplifyFunction() {
		// asin(-1) = -π/2
		Expr arg = getArgument();
		if (arg.equalTo(NEG_ONE)) {
			return new Multiple(negOneHalf, pi);
		}
		// asin(-√3/2) = -π/3
		if (arg.equalTo(new Fraction(NEG_ROOT_THREE, TWO))) {
			return new Multiple(negOneThird, pi);
		}
		// asin(-√2/2) = -π/4
		if (arg.equalTo(new Fraction(NEG_ROOT_TWO, TWO))) {
			return new Multiple(negOneFourth, pi);
		}
		// asin(-1/2) = -π/6
		if (arg.equalTo(negOneHalf)) {
			return new Multiple(negOneSixth, pi);
		}
		// asin(0) = 0
		if (arg.isZero() == YES) {
			return ZERO;
		}
		// asin(1/2) = π/6
		if (arg.equalTo(oneHalf)) {
			return new Multiple(oneSixth, pi);
		}
		// asin(√2/2) = π/4
		if (arg.equalTo(new Fraction(ROOT_TWO, TWO))) {
			return new Multiple(oneFourth, pi);
		}
		// asin(√3/2) = π/3
		if (arg.equalTo(new Fraction(ROOT_THREE, TWO))) {
			return new Multiple(oneThird, pi);
		}
		// asin(1) = π/2
		if (arg.equalTo(ONE)) {
			return new Multiple(oneHalf, pi);
		}
		
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new Sqrt(new Difference(ONE, getArgument().square())).inverse().simplify(null);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Cannot take the antiderivative of an arbitrary ArcSin[x] (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return getArgument().isZero();
	}
	public Compare isPositive() {
		return isReal().and(getArgument().isPositive());
	}
	public Compare isNegative() {
		return isReal().and(getArgument().isNegative());
	}
	public Compare isReal() {
		// Certified by Grapher!
		Compare cond1 = getArgument().isReal();
		Compare cond2 = getArgument().greaterThanOrEqualTo(NEG_ONE);
		Compare cond3 = getArgument().lessThanOrEqualTo(ONE);
		if (cond1 == MAYBE || cond2 == MAYBE || cond3 == MAYBE) return MAYBE;
		if (cond1 == YES && cond2 == YES && cond3 == YES) return YES;
		return NO;
	}
	public Compare isImag() {
		// Certified by Grapher!
		return new Re(getArgument()).simplify(null).isZero();
	}
	
}
