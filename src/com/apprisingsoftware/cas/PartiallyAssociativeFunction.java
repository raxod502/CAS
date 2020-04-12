package com.apprisingsoftware.cas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

abstract class PartiallyAssociativeFunction extends Function {
	
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
	
	@Override public final void expandFunction() {
		expandPartiallyAssociativeFunction();
		// For every possible pair of Exprs, in both directions (commutivity), see
		// if the resulting DualFunction can be simplified (beyond the same
		// DualFunction); if so, substitute the result into this AssociativeFunction
		// and add it to the equivalency group.
		for (int i=1; i<args.size(); i++) {
			DualFunction instance = getDualFunction(args.get(0), args.get(i));
			instance.expand();
			for (Expr alternate : instance.equivalenceClass()) {
				// We are only interested in "simplified" versions of the function
				if (!alternate.getClass().equals(instance.getClass())) {
					try {
						ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
						newArgs.set(0, instance);
						newArgs.remove(i);
						Function newInstance;
						if (newArgs.size() == 2) {
							newInstance = getDualFunction(newArgs.get(0), newArgs.get(1));
						}
						else {
							newInstance = getClass().getConstructor(ArrayList.class).newInstance(newArgs);
						}
						setEquivalent(newInstance);
					}
					catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
	public void expandPartiallyAssociativeFunction() {}
	@Override public Expr getAlternate() {
		return this;
	}
	
	@Override public Expr derivative(Variable var) {
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
		catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	@Override public abstract Expr derivativePartial(int var);
	@Override public abstract Expr antiderivative(Variable var);
	
	@Override public abstract Compare compare(Expr other);
	@Override public abstract Compare isZero();
	@Override public abstract Compare isPositive();
	@Override public abstract Compare isNegative();
	@Override public abstract Compare isReal();
	@Override public abstract Compare isImag();
	
	public abstract DualFunction getDualFunction(Expr left, Expr right);
	@Override public final int getMinimumArguments() { return 3; }
	@Override public final int getMaximumArguments() { return -1; }
	
}
