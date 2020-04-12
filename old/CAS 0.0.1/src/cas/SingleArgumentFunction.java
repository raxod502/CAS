package cas;

public abstract class SingleArgumentFunction extends Function {
	
	public SingleArgumentFunction(Expr arg) {
		super(arg);
	}
	public SingleArgumentFunction(Expr arg, int derivative) {
		super(new int[] {derivative}, arg);
	}
	
	public abstract Expr simplifyFunction();
	public Expr derivative(Variable var) {
		return new DualProduct(getArgument().derivative(var), derivativePartial(0)).simplify();
	}
	public abstract Expr derivativePartial(int var);
	public abstract Expr antiderivative(Variable var);
	
	public abstract Compare compare(Expr other);
	public abstract Compare isZero();
	public abstract Compare isPositive();
	public abstract Compare isNegative();
	public abstract Compare isReal();
	public abstract Compare isImag();
	
	public final int getMinimumArguments() { return 1; }
	public final int getMaximumArguments() { return 1; }
	public final Expr getArgument() { return args.get(0); }
	
}
