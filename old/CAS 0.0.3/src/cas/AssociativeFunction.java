package cas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class AssociativeFunction extends Function {
	
	public AssociativeFunction(ArrayList<Expr> args) {
		super(args);
	}
	public AssociativeFunction(Expr... args) {
		super(args);
	}
	public AssociativeFunction(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public AssociativeFunction(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	
	public final Expr simplifyFunction(HashSet<Expr> eq) {
		ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
		int i = 0, j;
		while (i < newArgs.size()) {
			if (getClass().isInstance(newArgs.get(i))) {
				newArgs.addAll(((AssociativeFunction)newArgs.get(i)).args);
				newArgs.remove(i);
			}
			else if (getDualFunction(null, null).getClass().isInstance(newArgs.get(i))) {
				newArgs.addAll(((DualFunction)newArgs.get(i)).args);
				newArgs.remove(i);
			}
			else {
				i += 1;
			}
		}
		i = 0;
		while (i < newArgs.size()) {
			j = i + 1;
			while (j < newArgs.size()) {
				if (j == i) {
					j += 1;
					continue;
				}
				try {
					DualFunction result = getDualFunction(newArgs.get(i), newArgs.get(j));
					Expr resultFinal = result.simplifyFunction(eq);
					if (!result.equals(resultFinal)) {
						newArgs.set(i, resultFinal);
						newArgs.remove(j);
						j = 0;
					}
					else j += 1;
				}
				catch (Exception e) { e.printStackTrace(); throw new RuntimeException("Exception encountered."); }
			}
			i += 1;
		}
		if (newArgs.size() == 0) throw new IllegalStateException("AssociativeFunction reduction removed all arguments.");
		if (newArgs.size() == 1) return newArgs.get(0);
		if (newArgs.size() == 2) return getDualFunction(newArgs.get(0), newArgs.get(1));
		try {
			// Should already be simplified as much as is possible, .simplifyFunction() will cause infinite recursion.
			return getClass().getDeclaredConstructor(ArrayList.class).newInstance(newArgs).simplifyAssociativeFunction();
		}
		catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
		catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
		catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
		catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
	}
	public Expr simplifyAssociativeFunction() {
		return this;
	}
	public Expr derivative(Variable var) {
		if (args.size() == 1) return args.get(0).derivative(var);
		if (args.size() == 2) return getDualFunction(args.get(0), args.get(1)).derivative(var);
		ArrayList<Expr> terms = new ArrayList<Expr>();
		for (int i=0; i<args.size()-1; i++) {
			terms.set(i, args.get(i+1));
		}
		try {
			AssociativeFunction slice = getClass().getDeclaredConstructor(ArrayList.class).newInstance(terms);
			return getDualFunction(args.get(0), slice).derivative(var);
		}
		catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
		catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
		catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
		catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
	}
	public abstract Expr derivativePartial(int var);
	public abstract Expr antiderivative(Variable var);
	
	public abstract Compare compare(Expr other);
	public abstract Compare isZero();
	public abstract Compare isPositive();
	public abstract Compare isNegative();
	public abstract Compare isReal();
	public abstract Compare isImag();
	
	public final int getMinimumArguments() { return 3; }
	public final int getMaximumArguments() { return -1; }
	public abstract DualFunction getDualFunction(Expr left, Expr right);
	
}
