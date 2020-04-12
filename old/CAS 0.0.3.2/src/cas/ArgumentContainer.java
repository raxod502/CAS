package cas;

import java.util.ArrayList;
import java.util.HashSet;

public class ArgumentContainer extends Function {
	
	public ArgumentContainer(ArrayList<Expr> args) {
		super(args);
	}
	public ArgumentContainer(Expr... args) {
		super(args);
	}
	public ArgumentContainer(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public ArgumentContainer(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	
	public Expr simplifyFunction() {
		return this;
	}
	public Expr derivativePartial(int var)  {
		throw new UnsupportedOperationException("Cannot take the derivative of an ArgumentContainer. Even if it is the partial derivative.");
	}
	public Expr antiderivative(Variable var)  {
		throw new UnsupportedOperationException("Cannot take the antiderivative of an ArgumentContainer.");
	}
	
	public Compare compare(Expr other) {
		if (!(other instanceof ArgumentContainer)) return UNEQUAL;
		ArgumentContainer otherAC = (ArgumentContainer)other;
		if (args.size() != otherAC.args.size()) return UNEQUAL;
		for (int i=0; i<args.size(); i++) {
			Compare compare = args.get(i).compare(otherAC.args.get(i));
			if (compare != EQUAL) return UNEQUAL;
		}
		return EQUAL;
	}
	public Compare isZero() { return NO; }
	public Compare isPositive() { return NO; }
	public Compare isNegative() { return NO; }
	public Compare isReal() { return NO; }
	public Compare isImag() { return NO; }
	
	public int getMinimumArguments() { return -1; }
	public int getMaximumArguments() { return -1; }
	
}
