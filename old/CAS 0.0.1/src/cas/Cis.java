	package cas;

public class Cis extends SingleArgumentFunction {
	
	public Cis(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Cis(Expr arg) {
		super(arg);
	}
	
	public Expr simplifyFunction() {
		// Cis[x] = Cos[x] + i Sin[x]
		return new ComplexExpr(new Cos(getArgument()), new Sin(getArgument())).simplify();
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new ComplexExpr(new Sin(getArgument()).negative(), new Cos(getArgument())).simplify();
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Cannot take the antiderivative of an arbitrary Cis[x] (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return NO;
	}
	public Compare isPositive() {
		if (getArgument() instanceof Number) {
			return Cos.isDivisible((Number)getArgument(), ZERO) ? YES : NO;
		}
		return getArgument().isZero();
	}
	public Compare isNegative() {
		if (getArgument() instanceof Number) {
			return Cos.isDivisible((Number)getArgument(), pi) ? YES : NO;
		}
		return MAYBE;
	}
	public Compare isReal() {
		Compare cond1 = isPositive(), cond2 = isNegative();
		if (cond1 == YES || cond2 == YES) return YES;
		if (cond1 == MAYBE || cond2 == MAYBE) return MAYBE;
		return NO;
	}
	public Compare isImag() {
		if (getArgument() instanceof Number) {
			Compare cond1 = Cos.isDivisible((Number)getArgument(), new Multiple(oneHalf, pi)) ? YES : NO;
			Compare cond2 = Cos.isDivisible((Number)getArgument(), new Multiple(negOneHalf, pi)) ? YES : NO;
			return cond1.and(cond2);
		}
		return MAYBE;
	}
	
}
