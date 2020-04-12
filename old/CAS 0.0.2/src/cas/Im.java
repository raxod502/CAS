package cas;

public class Im extends SingleArgumentFunction {
	
	public Im(Expr arg) {
		super(arg);
	}
	public Im(Expr arg, int derivative) {
		super(arg, derivative);
	}
	
	public Expr simplifyFunction() {
		if (getArgument() instanceof ComplexExpr) {
			return new DualSum(new Im(((ComplexExpr)getArgument()).getReal()), new Re(((ComplexExpr)getArgument()).getImag())).simplify();
		}
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return arg.imag;
		}
		if (getArgument() instanceof ImaginaryExpr) {
			return new Re(((ImaginaryExpr)getArgument()).getArgument()).simplify();
		}
		if (getArgument() instanceof Imaginary) {
			Imaginary arg = (Imaginary)getArgument();
			return arg.value;
		}
		if (getArgument() instanceof Exponent || getArgument() instanceof Fraction || getArgument() instanceof Integer || getArgument() instanceof Transcendental) {
			return ZERO;
		}
		if (getArgument() instanceof Cis) {
			Cis arg = (Cis)getArgument();
			return new Sin(arg.getArgument()).simplify();
		}
		if (getArgument() instanceof Re) {
			return ZERO;
		}
		if (getArgument() instanceof Im) {
			return ((Im)getArgument()).getArgument();
		}
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	public Expr derivativePartial(int var) {
		return new Im(getArgument(), 1);
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
