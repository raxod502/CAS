package cas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class DualFunction extends DoubleArgumentFunction {
	
	public DualFunction(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualFunction(Expr left, Expr right) {
		super(left, right);
	}
	
	public final Expr simplifyFunction() {
		Expr result = simplifyDualFunction();
		if (!result.equals(this)) {
			return result.simplify();
		}
		else {
			DualFunction inst;
			try {
				inst = getClass().getDeclaredConstructor(ArrayList.class).newInstance(getRight(), getLeft());
			}
			catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
			catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
			catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
			catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
			result = inst.simplifyDualFunction();
			if (!result.equals(inst)) {
				return result.simplify();
			}
			else {
				return this;
			}
		}
	}
	public abstract Expr simplifyDualFunction();
	public abstract Expr derivative(Variable var);
	public abstract Expr antiderivative(Variable var);
	public abstract Compare compare(Expr other);
	
}
