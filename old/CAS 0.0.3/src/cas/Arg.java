package cas;

import java.util.HashSet;

public class Arg extends SingleArgumentFunction {
	
	public Arg(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Arg(Expr arg) {
		super(arg);
	}
	
	public Expr simplifyFunction(HashSet<Expr> eq) {
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getArgument();
			if (arg.getReal().isReal() == YES && arg.getImag().isReal() == YES)
			return new ArcTan2(arg.getReal(), arg.getImag()).simplify(eq);
		}
		if (getArgument() instanceof ImaginaryExpr) {
			ImaginaryExpr arg = (ImaginaryExpr)getArgument();
			if (arg.getReal().isReal() == YES)
			return new ArcTan2(ZERO, arg.getArgument()).simplify(eq);
		}
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return new ArcTan2(arg.real, arg.imag).simplify(eq);
		}
		if (getArgument() instanceof Imaginary) {
			Imaginary arg = (Imaginary)getArgument();
			return new ArcTan2(ZERO, arg.value).simplify(eq);
		}
		// Real number
		if (getArgument().isReal() == YES) {
			return ZERO;
		}
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		// Arg[a + bi] = -i log(sgn(a + bi)) [apparently...]
		return new ImaginaryExpr(new Log(new Sgn(getArgument()))).negative().simplify(eq);
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new Arg(getArgument(), 1).simplify(null);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Don't know how to take the antiderivative of an Arg (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return getArgument().isReal().and(getArgument().isPositive());
	}
	public Compare isPositive() {
		if (getArgument().isReal() == YES) {
			if (getArgument().isPositive() == YES) return NO;
			if (getArgument().isNegative() == YES) return YES;
		}
		if (getArgument().isImag() == YES) {
			return getArgument().isPositive();
		}
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getArgument();
			return Compare._or(arg.getRight().isPositive(), Compare._and(arg.getLeft().isNegative(), arg.getRight().isZero()));
		}
		if (getArgument() instanceof ImaginaryExpr) {
			ImaginaryExpr arg = (ImaginaryExpr)getArgument();
			if (arg.getReal().isReal() == YES)
			return arg.getReal().isPositive();
		}
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return Compare._or(arg.imag.isPositive(), Compare._and(arg.real.isNegative(), arg.imag.isZero()));
		}
		if (getArgument() instanceof Imaginary) {
			Imaginary arg = (Imaginary)getArgument();
			return arg.value.isPositive();
		}
		return MAYBE;
	}
	public Compare isNegative() {
		if (getArgument().isReal() == YES) {
			if (getArgument().isPositive() == YES) return YES;
			if (getArgument().isNegative() == YES) return NO;
		}
		if (getArgument().isImag() == YES) {
			return getArgument().isNegative();
		}
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getArgument();
			return arg.getRight().isNegative();
		}
		if (getArgument() instanceof ImaginaryExpr) {
			ImaginaryExpr arg = (ImaginaryExpr)getArgument();
			if (arg.getReal().isReal() == YES)
			return arg.getReal().isNegative();
		}
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return arg.imag.isNegative();
		}
		if (getArgument() instanceof Imaginary) {
			Imaginary arg = (Imaginary)getArgument();
			return arg.value.isNegative();
		}
		return MAYBE;
	}
	public Compare isReal() {
		return YES;
	}
	public Compare isImag() {
		return NO;
	}
	
}
