package cas;

import java.lang.reflect.*;
import java.util.*;

public abstract class Function extends Expr {
	
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
			ArrayList<ArrayList<Expr>> choicesList = new ArrayList<ArrayList<Expr>>();
			for (int i=0; i<args.size(); i++) {
				choicesList.add(args.get(i).array);
			}
			ArrayList<ArrayList<Expr>> functionArguments = selectObjectsFromLists(choicesList);
			boolean hasMoreThanOneCombination = false;
			for (int i=0; i<args.size(); i++) {
				if (args.get(i).array.size() > 1) {
					hasMoreThanOneCombination = true;
				}
			}
			if (hasMoreThanOneCombination) {
				array = new ArrayList<Expr>(functionArguments.size());
				for (int i=0; i<functionArguments.size(); i++) {
					try {
						Function instance;
						if (this instanceof SingleArgumentFunction) {
							instance = getClass().getDeclaredConstructor(Expr.class).newInstance(functionArguments.get(i).get(0));
						}
						else if (this instanceof DoubleArgumentFunction) {
							instance = getClass().getDeclaredConstructor(Expr.class, Expr.class).newInstance(functionArguments.get(i).get(0), functionArguments.get(i).get(1));
						}
						else {
							instance = getClass().getDeclaredConstructor(ArrayList.class).newInstance(functionArguments.get(i));
						}
						array.add(instance);
					}
					catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
					catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
					catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
					catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
				}
			}
			this.args = args;
		}
		else {
			if (minimumArguments == -1)
				throw new IllegalArgumentException(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1) + " objects cannot have more than " + maximumArguments + " arguments.");
			if (maximumArguments == -1)
				throw new IllegalArgumentException(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1) + " objects must have at least " + minimumArguments + " arguments.");
			throw new IllegalStateException("Something went wrong while checking the number of arguments.");
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
			throw new IllegalStateException("Something went wrong while checking the number of arguments.");
		}
	}
	/**
	 * Creates a list of sublists, where each sublist is formed by taking one object
	 * from the first subarray in the array given to the method, one from the second,
	 * and so on. The list returned will contain every possible selection of subarrays
	 * from the array provided.
	 */
	private static <T> ArrayList<ArrayList<T>> selectObjectsFromLists(ArrayList<ArrayList<T>> choicesList) {
		int[] indices = new int[choicesList.size()]; // Initialized as [0, 0, 0, ...]
		int count = 1;
		for (ArrayList<T> choices : choicesList) count *= choices.size();
		ArrayList<ArrayList<T>> permutations = new ArrayList<ArrayList<T>>();
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
	public Function(int[] derivatives, Expr... args) {
		this(new ArrayList<Expr>(Arrays.asList(args)), derivatives);
	}
	public Function(ArrayList<Expr> args) {
		this(args, new int[args.size()]);
	}
	public Function(Expr... args) {
		this(new int[args.length], args);
	}
	
	public final Expr simplifyExpr() {
		// We don't know how to simplify the derivatives of functions, in general.
		for (int d : derivatives) if (d != 0) return this;
		ArrayList<Expr> newargs = new ArrayList<Expr>();
		for (int i=0; i<args.size(); i++) {
			newargs.add(args.get(i).simplify());
		}
		try {
			if (this instanceof SingleArgumentFunction) {
				return getClass().getDeclaredConstructor(Expr.class).newInstance(newargs.get(0)).simplifyFunction();
			}
			else if (this instanceof DoubleArgumentFunction) {
				return getClass().getDeclaredConstructor(Expr.class, Expr.class).newInstance(newargs.get(0), newargs.get(1)).simplifyFunction();
			}
			else {
				return getClass().getDeclaredConstructor(ArrayList.class).newInstance(newargs).simplifyFunction();
			}
		}
		catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
		catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
		catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
		catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
	}
	public abstract Expr simplifyFunction();
	
	// Subclasses can (should) override to make this method more efficient
	public Expr derivative(Variable var) {
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
			catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
			catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
			catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
			catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
			ders.add(new DualProduct(arg.derivative(var), duplicate.derivativePartial(i)));
		}
		if (ders.size() == 1) return ders.get(0).simplify();
		if (ders.size() == 2) return new DualSum(ders.get(0), ders.get(1)).simplify();
		return new Sum(ders).simplify();
	}
	public abstract Expr derivativePartial(int var);
	public abstract Expr antiderivative(Variable var);
	
	public abstract Compare compare(Expr other);
	public abstract Compare isZero();
	public abstract Compare isPositive();
	public abstract Compare isNegative();
	public abstract Compare isReal();
	public abstract Compare isImag();
	
	public String toString() {
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result + Arrays.hashCode(derivatives);
		return result;
	}
	public boolean equals(Object obj) {
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
	
}
