package cas;

import java.util.HashSet;

public abstract class Number extends Expr {
	
	public final Expr simplifyExpr(HashSet<Expr> eq) {
		return this;
	}
	public final Expr reduce(HashSet<Expr> eq) {
		return this; // Todo if necessary
	}
	public final Expr derivative(Variable var) {
		return ZERO;
	}
	public final Expr antiderivative(Variable var) {
		return new DualProduct(this, var).simplify(null);
	}
	public final boolean isDivisibleBy(Number other) {
		Number res = (Number)new Quotient(this, other).simplify(null);
		return res instanceof Integer;
	}
	public abstract ComplexDouble complexdoubleValue();
	
	public abstract Compare compare(Expr other);
	public abstract Compare isZero();
	public abstract Compare isPositive();
	public abstract Compare isNegative();
	public abstract Compare isReal();
	public abstract Compare isImag();
	
	public abstract String toString();
	
}
