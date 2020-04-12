package cas;

import java.util.HashSet;

public class Sgn extends SingleArgumentFunction {
	
	public Sgn(Expr arg) {
		super(arg);
	}
	public Sgn(Expr arg, int derivative) {
		super(arg, derivative);
	}
	
	public Expr simplifyFunction(HashSet<Expr> eq) {
		if (getArgument().isZero() == YES) {
			return ZERO;
		}
		if (getArgument().isPositive() == YES) {
			return ONE;
		}
		if (getArgument().isNegative() == YES) {
			return NEG_ONE;
		}
		if (getArgument().isImag() == YES) {
			Expr texpr = new Im(getArgument()).simplify(null);
			if (texpr.isPositive() == YES) {
				return I;
			}
			if (texpr.isNegative() == NO) {
				return negI;
			}
		}
		if (getArgument().isComplex() == YES) {
			return new Cis(new ArcTan2(new Re(getArgument()), new Im(getArgument()))).simplify(eq);
		}
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new Sgn(getArgument(), 1).simplify(null);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("sgn(f(x)) isn't even a well-defined integral! Come on!");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return getArgument().isZero();
	}
	public Compare isPositive() {
		return getArgument().isPositive();
	}
	public Compare isNegative() {
		return getArgument().isNegative();
	}
	public Compare isReal() {
		return getArgument().isReal();
	}
	public Compare isImag() {
		return getArgument().isImag();
	}
	
}
