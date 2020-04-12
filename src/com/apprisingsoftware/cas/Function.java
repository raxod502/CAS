package com.apprisingsoftware.cas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract class Function extends Expr implements MaybeUseful {
	
	public ArrayList<Expr> args;
	public int[] derivatives;
	
	/**
	 * Checks if the number of args provided is in [minimumArguments, maximumArguments].
	 * Sets Expr array to include a new instance for every possible permutation of the
	 * possibilities in each arg's Expr array.
	 */
	public Function(ArrayList<Expr> args, int[] derivatives) {
		int minimumArguments = getMinimumArguments();
		int maximumArguments = getMaximumArguments();
		if ((minimumArguments == -1 || args.size() >= minimumArguments) &&
				(maximumArguments == -1 || args.size() <= maximumArguments)) {
			// Set arguments for actual function object
			this.args = args;
		}
		else {
			if (minimumArguments == -1)
				throw new IllegalArgumentException(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1) + " objects cannot have more than " + maximumArguments + " arguments.");
			if (maximumArguments == -1)
				throw new IllegalArgumentException(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1) + " objects must have at least " + minimumArguments + " arguments.");
			throw new AssertionError("Something went wrong while checking the number of arguments.");
		}
		if ((minimumArguments == -1 || derivatives.length >= minimumArguments) &&
				(maximumArguments == -1 || derivatives.length <= maximumArguments)) {
			this.derivatives = Arrays.copyOf(derivatives, derivatives.length);
		}
		else {
			if (minimumArguments == -1)
				throw new IllegalArgumentException(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1) + " objects cannot have more than " + maximumArguments + " derivative arguments.");
			if (maximumArguments == -1)
				throw new IllegalArgumentException(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1) + " objects must have at least " + minimumArguments + " derivative arguments.");
			throw new AssertionError("Something went wrong while checking the number of arguments.");
		}
	}
	public Function(int[] derivatives, Expr... args) {
		this(new ArrayList<Expr>(Arrays.asList(args)), derivatives);
	}
	public Function(ArrayList<Expr> args) {
		this(args, new int[args.size()]);
	}
	public Function(Expr... args) {
		this(new int[args.length], args);
	}
	
	@Override public final void expandExpr() {
		expandFunction();
		for (Expr arg : args) {
			arg.expand();
		}
		List<List<Expr>> argGroups = new ArrayList<>();
		for (Expr arg : args) {
			List<Expr> list = arg.equivalenceClass().toList();
			int minNodes = java.lang.Integer.MAX_VALUE;
			for (int i=0; i<list.size(); i++) {
				int numNodes = list.get(i).nodeCount();
				if (numNodes < minNodes) {
					while (i > 0) {
						list.remove(0);
						i -= 1;
					}
					minNodes = numNodes;
				}
				else if (numNodes > minNodes) {
					list.remove(i);
					i -= 1;
				}
			}
			argGroups.add(list);
		}
		List<List<Expr>> argPermutations = Function.selectObjectsFromLists(argGroups);
		for (List<Expr> argPermutation : argPermutations) {
			Function instance;
			try {
				if (this instanceof SingleArgumentFunction) {
					instance = getClass().getConstructor(Expr.class).newInstance(argPermutation.get(0));
				}
				else if (this instanceof DoubleArgumentFunction) {
					instance = getClass().getConstructor(Expr.class, Expr.class).newInstance(argPermutation.get(0), argPermutation.get(1));
				}
				else {
					instance = getClass().getConstructor(ArrayList.class).newInstance(argPermutation);
				}
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Exception encountered while instantiating Function object");
			}
			setEquivalent(instance);
		}
	}
	/**
	 * Creates a list of sublists, where each sublist is formed by taking one object
	 * from the first subarray in the array given to the method, one from the second,
	 * and so on. The list returned will contain every possible selection of subarrays
	 * from the array provided.
	 */
	private static <T> List<List<T>> selectObjectsFromLists(List<List<T>> choicesList) {
		int[] indices = new int[choicesList.size()]; // Initialized as [0, 0, 0, ...]
		int count = 1;
		for (List<T> choices : choicesList) count *= choices.size();
		List<List<T>> permutations = new ArrayList<List<T>>();
		int index = 0;
		while (index < count) {
			permutations.add(new ArrayList<T>(Collections.nCopies(choicesList.size(), null)));
			// Pick choices from subarrays
			for (int i=0; i<choicesList.size(); i++) {
				permutations.get(index).set(i, choicesList.get(i).get(indices[i]));
			}
			index += 1;
			// Increment indices
			for (int i=0; i<indices.length; i++) {
				indices[i] += 1;
				if (indices[i] == choicesList.get(i).size()) {
					indices[i] = 0;
				}
				else {
					break;
				}
			}
		}
		return permutations;
	}
	@SuppressWarnings("unused")
	private static <T> List<T> iterableToList(Iterable<T> iterable) {
		ArrayList<T> collection = new ArrayList<T>();
		for (T item : iterable) {
			collection.add(item);
		}
		return collection;
	}
	public void expandFunction() {}
	@Override public abstract Expr getAlternate();
	
	// Subclasses can (should) override to make this method more efficient
	@Override public Expr derivative(Variable var) {
		boolean allNumbers = true;
		for (Expr arg : args) {
			if (!(arg instanceof Number)) {
				allNumbers = false;
				break;
			}
		}
		if (allNumbers) return ZERO;
		ArrayList<Expr> ders = new ArrayList<Expr>();
		for (int i=0; i<args.size(); i++) {
			Expr arg = args.get(i);
			// Derivative of argument * partial derivative of function w/respect to argument i
			Function duplicate;
			try {
				duplicate = getClass().getDeclaredConstructor(ArrayList.class, int[].class).newInstance(new ArrayList<Expr>(args), derivatives.clone());
			}
			catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			ders.add(new DualProduct(arg.derivative(var).getSimplest(), duplicate.derivativePartial(i).getSimplest()));
		}
		if (ders.size() == 1) return ders.get(0).getSimplest();
		if (ders.size() == 2) return new DualSum(ders.get(0), ders.get(1)).getSimplest();
		return new Sum(ders).getSimplest();
	}
	public abstract Expr derivativePartial(int var);
	@Override public abstract Expr antiderivative(Variable var);
	
	@Override public abstract Compare compare(Expr other);
	@Override public abstract Compare isZero();
	@Override public abstract Compare isPositive();
	@Override public abstract Compare isNegative();
	@Override public abstract Compare isReal();
	@Override public abstract Compare isImag();
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1));
		sb.append("[");
		boolean first = true;
		for (Expr arg : args) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(arg);
		}
		return sb.append("]").toString();
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result + Arrays.hashCode(derivatives);
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Function other = (Function) obj;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		if (!Arrays.equals(derivatives, other.derivatives))
			return false;
		return true;
	}
	
	public abstract int getMinimumArguments();
	public abstract int getMaximumArguments();
	public final Expr getArgument(int index) {
		return args.get(index);
	}
	public final Number getNumber(int index) {
		return (Number)getArgument(index);
	}
	public final double getDouble(int index) {
		return ((NumNumSum)getNumber(index)).doubleValue();
	}
	
	@Override public final int nodeCount() {
		int count = 1;
		for (Expr arg : args) {
			count += arg.nodeCount();
		}
		return count;
	}
	@Override public Expr makeUseful() {
		boolean allUseful = true;
		for (Expr arg : args) {
			if (arg instanceof MaybeUseful) {
				allUseful = false;
				break;
			}
		}
		if (allUseful) return this;
		Function instance;
		try {
			if (this instanceof SingleArgumentFunction) {
				instance = getClass().getConstructor(Expr.class).newInstance(args.get(0));
			}
			else if (this instanceof DoubleArgumentFunction) {
				instance = getClass().getConstructor(Expr.class, Expr.class).newInstance(args.get(0), args.get(1));
			}
			else {
				instance = getClass().getConstructor(ArrayList.class).newInstance(new ArrayList<Expr>(args));
			}
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
		for (int i=0; i<args.size(); i++) {
			if (instance.args.get(i) instanceof MaybeUseful) {
				instance.args.set(i, ((MaybeUseful)instance.args.get(i)).makeUseful());
			}
		}
		return instance;
	}
	
}
