package cas;

public abstract class Number extends Expr {
	
	public final Expr simplifyExpr() {
		return this;
	}
	public final Expr reduce() {
		return this; // Todo if necessary
	}
	public final Expr derivative(Variable var) {
		return ZERO;
	}
	public final Expr antiderivative(Variable var) {
		return new DualProduct(this, var).simplify();
	}
	public final boolean isDivisibleBy(Number other) {
		Number res = (Number)new Quotient(this, other).simplify();
		return res instanceof Integer;
	}
	public abstract ComplexDouble complexDoubleValue();
	
	public abstract Compare compare(Expr other);
	public abstract Compare isZero();
	public abstract Compare isPositive();
	public abstract Compare isNegative();
	public abstract Compare isReal();
	public abstract Compare isImag();
	
	public abstract String toString();
	
}
