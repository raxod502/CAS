package cas;

public abstract class DoubleArgumentFunction extends Function {
	
	public DoubleArgumentFunction(Expr left, Expr right) {
		super(left, right);
	}
	public DoubleArgumentFunction(int derivativeLeft, int derivativeRight, Expr left, Expr right) {
		super(new int[] {derivativeLeft, derivativeRight}, left, right);
	}
	
	public abstract Expr simplifyFunction();
	public Expr derivative(Variable var) {
		if (args.get(0) instanceof Number && args.get(1) instanceof Number) {
			return ZERO;
		}
		// d/dz f(x, y) = dx/dz df/dx + dy/dz df/dy
		return new DualSum(new DualProduct(getLeft().derivative(var), derivativePartial(0)),
				new DualProduct(getRight().derivative(var), derivativePartial(1))).simplify();
	}
	public abstract Expr derivativePartial(int var);
	public abstract Expr antiderivative(Variable var);
	
	public abstract Compare compare(Expr other);
	public abstract Compare isZero();
	public abstract Compare isPositive();
	public abstract Compare isNegative();
	public abstract Compare isReal();
	public abstract Compare isImag();
	
	public final int getMinimumArguments() { return 2; }
	public final int getMaximumArguments() { return 2; }
	public final Expr getLeft() { return args.get(0); }
	public final Expr getRight() { return args.get(1); }
	
}
