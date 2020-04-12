package cas;

import java.lang.reflect.InvocationTargetException;

public abstract class DualFunction extends DoubleArgumentFunction {
	
	public DualFunction(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualFunction(Expr left, Expr right) {
		super(left, right);
	}
	
	public final void expandFunction() {
		try {
			DualFunction swapped = getClass().getConstructor(Expr.class, Expr.class).newInstance(getRight(), getLeft());
			equivalenceClass.addExpr(swapped);
			swapped.expand();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract Expr derivative(Variable var);
	public abstract Expr antiderivative(Variable var);
	public abstract Compare compare(Expr other);
	
}
