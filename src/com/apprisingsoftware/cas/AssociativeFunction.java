package com.apprisingsoftware.cas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

abstract class AssociativeFunction extends Function {
	
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
	
	@Override public final void expandFunction() {
		expandAssociativeFunction();
		// For every possible pair of Exprs, in both directions (commutivity), see
		// if the resulting DualFunction can be simplified (beyond the same
		// DualFunction); if so, substitute the result into this AssociativeFunction
		// and add it to the equivalency group.
		// UPDATE: actually, no, we have to deal with commutivity in the DualFunction
		// classes because DualFunction classes can be (and are) instantiated without
		// a wrapper AssociativeFunction class.
		for (int i=0; i<args.size(); i++) {
			for (int j=i+1; j<args.size(); j++) {
				Expr instance = getDualFunction(args.get(i), args.get(j));
				RecursionPrint.push(instance);
				Expr alt = instance.getAlternate();
				RecursionPrint.pop();
				ArrayList<Expr> allResults = new ArrayList<>(alt.alternateResults);
				allResults.add(alt);
				for (Expr result : allResults) {
					// We are only interested in "simplified" versions of the function
					if (!result.equals(instance)) {
						result.expand();
						for (Expr alternate : result.equivalenceClass()) {
							try {
								ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
								newArgs.set(i, alternate);
								newArgs.remove(j);
								Function newInstance;
								if (newArgs.size() == 2) {
									newInstance = getDualFunction(newArgs.get(0), newArgs.get(1));
								}
								else {
									newInstance = getClass().getConstructor(ArrayList.class).newInstance(newArgs);
								}
								if (equivalenceClass().contains(newInstance)) continue;
								setEquivalent(newInstance);
							}
							catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
		}
	}
	public void expandAssociativeFunction() {}
	@Override public final Expr getAlternate() {
		return this;
	}
	
	@Override public Expr derivative(Variable var) {
		ArrayList<Expr> terms = new ArrayList<Expr>();
		for (int i=0; i<args.size()-1; i++) {
			terms.add(args.get(i+1));
		}
		try {
			AssociativeFunction slice = getClass().getDeclaredConstructor(ArrayList.class).newInstance(terms);
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
	
	@Override public final int getMinimumArguments() { return 3; }
	@Override public final int getMaximumArguments() { return -1; }
	public abstract DualFunction getDualFunction(Expr left, Expr right);
	
}
