package cas;

import java.util.HashSet;

public class ImaginaryExpr extends SingleArgumentFunction {
	
	public ImaginaryExpr(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public ImaginaryExpr(Expr arg) {
		super(arg);
	}
	
	public Expr simplifyFunction(HashSet<Expr> eq) {
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getArgument();
			return new ComplexExpr(arg.getImag().negative(), arg.getReal()).simplify(eq);
		}
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return new Complex((NumNumSum)arg.imag.negative().simplify(null), arg.real);
		}
		if (getArgument() instanceof ImaginaryExpr) {
			ImaginaryExpr arg = (ImaginaryExpr)getArgument();
			return arg.getArgument().negative().simplify(eq);
		}
		if (getArgument() instanceof Imaginary) {
			Imaginary arg = (Imaginary)getArgument();
			return arg.value.negative().simplify(eq);
		}
		if (getArgument() instanceof Cis) {
			Cis arg = (Cis)getArgument();
			return new Sin(arg.getArgument()).simplify(eq);
		}
		if (getArgument() instanceof Number) {
			return new Imaginary((NumNumSum)getArgument()).reduce(eq);
		}
		return this;
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new ImaginaryExpr(getArgument());
	}
	public Expr antiderivative(Variable var) {
		return new ImaginaryExpr(getArgument().antiderivative(var));
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return getArgument().isZero();
	}
	public Compare isPositive() {
		return isReal().and(new Im(getArgument()).simplify(null).isPositive());
	}
	public Compare isNegative() {
		return isReal().and(new Im(getArgument()).simplify(null).isNegative());
	}
	public Compare isReal() {
		return getArgument().isImag();
	}
	public Compare isImag() {
		return getArgument().isReal();
	}
	
	public Expr getReal() { return getArgument(); }
	
}
