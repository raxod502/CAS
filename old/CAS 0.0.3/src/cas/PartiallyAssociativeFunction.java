package cas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class PartiallyAssociativeFunction extends Function {
	
	public PartiallyAssociativeFunction(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public PartiallyAssociativeFunction(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	public PartiallyAssociativeFunction(ArrayList<Expr> args) {
		super(args);
	}
	public PartiallyAssociativeFunction(Expr... args) {
		super(args);
	}
	
	public final Expr simplifyFunction(HashSet<Expr> eq) {
		ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
		int i = 1;
		while (i < newArgs.size()) {
			try {
				DualFunction result = getDualFunction(newArgs.get(0), newArgs.get(i));
				Expr resultFinal = result.simplifyFunction(eq);
				if (!result.equals(resultFinal)) {
					newArgs.set(0, resultFinal);
					newArgs.remove(i);
				}
				else i += 1;
			}
			catch (Exception e) { e.printStackTrace(); throw new RuntimeException("Exception encountered."); }
		}
		if (newArgs.size() == 0) throw new IllegalStateException("PartiallyAssociativeFunction reduction removed all arguments.");
		if (newArgs.size() == 1) return newArgs.get(0);
		if (newArgs.size() == 2) return getDualFunction(newArgs.get(0), newArgs.get(1));
		try {
			// Should already be simplified as much as is possible, .simplifyFunction() will cause infinite recursion.
			return getClass().getDeclaredConstructor(ArrayList.class).newInstance(newArgs);
		}
		catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
		catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
		catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
		catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
	}
	public Expr derivative(Variable var) {
		if (args.size() == 1) return args.get(0).derivative(var);
		if (args.size() == 2) return getDualFunction(args.get(0), args.get(1)).derivative(var);
		ArrayList<Expr> terms = new ArrayList<Expr>();
		for (int i=0; i<args.size()-1; i++) {
			terms.add(args.get(i+1));
		}
		try {
			PartiallyAssociativeFunction slice = getClass().getDeclaredConstructor(ArrayList.class).newInstance(terms);
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
	
	public abstract DualFunction getDualFunction(Expr left, Expr right);
	public final int getMinimumArguments() { return 3; }
	public final int getMaximumArguments() { return -1; }
	
}
