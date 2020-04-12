package cas;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public abstract class DualFunction extends DoubleArgumentFunction {
	
	public DualFunction(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualFunction(Expr left, Expr right) {
		super(left, right);
	}
	
	public final Expr simplifyFunction(HashSet<Expr> eq) {
		Expr resultOriginal = simplifyDualFunction(eq), resultSwapped = null;
		if (this instanceof DualProduct || this instanceof DualSum) {
			DualFunction inst;
			try {
				inst = getClass().getDeclaredConstructor(Expr.class, Expr.class).newInstance(getRight(), getLeft());
			}
			catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
			catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
			catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
			catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
			if (!inst.equals(this)) {
				resultSwapped = inst.simplify(eq);
			}
		}
		if (!resultOriginal.equals(this)) {
			return resultOriginal.simplify(eq);
		}
		else if (resultSwapped != null && !resultSwapped.equals(this)) {
			return resultSwapped; // Already .simplified
		}
		else {
			return this;
		}
	}
	public abstract Expr simplifyDualFunction(HashSet<Expr> eq);
	public abstract Expr derivative(Variable var);
	public abstract Expr antiderivative(Variable var);
	public abstract Compare compare(Expr other);
	
}
