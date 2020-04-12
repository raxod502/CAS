package cas;

import java.util.HashSet;

public class Re extends SingleArgumentFunction {
	
	public Re(Expr arg) {
		super(arg);
	}
	public Re(Expr arg, int derivative) {
		super(arg, derivative);
	}
	
	public Expr simplifyFunction() {
		if (getArgument() instanceof ComplexExpr) {
			return new DualSum(new Re(((ComplexExpr)getArgument()).getReal()), new Im(((ComplexExpr)getArgument()).getImag())).simplify();
		}
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return arg.real;
		}
		if (getArgument() instanceof ImaginaryExpr) {
			return new Im(((ImaginaryExpr)getArgument()).getArgument()).simplify();
		}
		if (getArgument() instanceof Imaginary) {
			return ZERO;
		}
		if (getArgument() instanceof Exponent || getArgument() instanceof Fraction || getArgument() instanceof Integer || getArgument() instanceof Transcendental) {
			return getArgument();
		}
		if (getArgument() instanceof Cis) {
			Cis arg = (Cis)getArgument();
			return new Cos(arg.getArgument()).simplify();
		}
		if (getArgument() instanceof Re) {
			return ((Re)getArgument()).getArgument();
		}
		if (getArgument() instanceof Im) {
			return ZERO;
		}
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	public Expr derivativePartial(int var) {
		return new Re(getArgument(), 1);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Don't even get me started on the integral of Im...");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return getArgument().isZero();
	}
	public Compare isPositive() {
		return NO;
	}
	public Compare isNegative() {
		return NO;
	}
	public Compare isReal() {
		return getArgument().isZero();
	}
	public Compare isImag() {
		return YES;
	}
	
}
