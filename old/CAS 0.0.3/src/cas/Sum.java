package cas;

import java.util.ArrayList;
import java.util.HashSet;

public final class Sum extends AssociativeFunction {
	
	public Sum(ArrayList<Expr> args) {
		super(args);
	}
	public Sum(Expr... args) {
		super(args);
	}
	public Sum(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public Sum(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	
	public Expr simplifyAssociativeFunction(HashSet<Expr> eq) {
		// Check through Products for common factors, i.e.
		// xy + yz + 2(dh/dx)y = y(x + z + 2(dh/dx))
		// Check for Sum factors [above is special case for Sum of one argument]
		// x^2 + xy + xz + x + y + z = (x + 1)(x + y + z)
		
		// But for now... we'll see.
		return this;
	}
	public Expr derivative(Variable var) {
		ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
		for (int i=0; i<args.size(); i++) {
			newArgs.set(i, newArgs.get(i).derivative(var));
		}
		return new Sum(newArgs);
	}
	public Expr derivativePartial(int var) {
		if (var >= args.size()) return ZERO;
		return ONE;
	}
	public Expr antiderivative(Variable var) {
		ArrayList<Expr> anti = new ArrayList<Expr>();
		for (int i=0; i<args.size(); i++) {
			anti.add(args.get(i).antiderivative(var));
		}
		return new Sum(anti).simplify(null);
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		boolean allPositive = true, allNegative = true, allZero = true;
		for (Expr arg : args) {
			if (arg.isPositive() != YES) allPositive = false;
			if (arg.isNegative() != YES) allNegative = false;
			if (arg.isZero() != YES) allZero = false;
		}
		if (allPositive || allNegative) return NO;
		if (allZero) return YES;
		return MAYBE;
	}
	public Compare isPositive() {
		boolean allPositive = true, allNegative = true, allZero = true;
		for (Expr arg : args) {
			if (arg.isPositive() != YES) allPositive = false;
			if (arg.isNegative() != YES) allNegative = false;
			if (arg.isZero() != YES) allZero = false;
		}
		if (allZero || allNegative) return NO;
		if (allPositive) return YES;
		return MAYBE;
	}
	public Compare isNegative() {
		boolean allPositive = true, allNegative = true, allZero = true;
		for (Expr arg : args) {
			if (arg.isPositive() != YES) allPositive = false;
			if (arg.isNegative() != YES) allNegative = false;
			if (arg.isZero() != YES) allZero = false;
		}
		if (allZero || allPositive) return NO;
		if (allNegative) return YES;
		return MAYBE;
	}
	public Compare isReal() {
		int numberNonreal = 0;
		for (Expr arg : args) {
			Compare isReal = arg.isReal();
			if (isReal == NO) {
				numberNonreal += 1;
			}
			else if (isReal == MAYBE) {
				return MAYBE;
			}
		}
		if (numberNonreal == 0) {
			return YES;
		}
		else if (numberNonreal == 1) {
			return NO;
		}
		else {
			return MAYBE;
		}
	}
	public Compare isImag() {
		int numberNonimag = 0;
		for (Expr arg : args) {
			Compare isImag = arg.isImag();
			if (isImag == NO) {
				numberNonimag += 1;
			}
			else if (isImag == MAYBE) {
				return MAYBE;
			}
		}
		if (numberNonimag == 0) {
			return YES;
		}
		else if (numberNonimag == 1) {
			return NO;
		}
		else {
			return MAYBE;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Expr arg : args) {
			String str = arg.toString();
			sb.append(str).append(" + ");
		}
		String str = sb.toString();
		return str.substring(0, str.length()-3);
	}
	
	public DualFunction getDualFunction(Expr left, Expr right) {
		return new DualSum(left, right);
	}
	
}
