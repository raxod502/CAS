package com.apprisingsoftware.cas;

import java.util.ArrayList;

class Exponentiation extends PartiallyAssociativeFunction {
	
	public Exponentiation(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public Exponentiation(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	public Exponentiation(ArrayList<Expr> args) {
		super(args);
	}
	public Exponentiation(Expr... args) {
		super(args);
	}
	
	@Override public Expr derivativePartial(int var) {
		if (var >= args.size()) return ZERO;
		ArrayList<Expr> group1 = new ArrayList<Expr>(args);
		if (var == 0) {
			Expr denom = group1.remove(0);
			group1.add(this);
			return new Quotient(new Product(group1), denom).getSimplest();
		}
		else {
			ArrayList<Expr> group2 = new ArrayList<Expr>(group1.subList(var+1, group1.size()));
			group1.subList(var, group1.size()).clear();
			if (group2.size() == 1)
				group1.add(group2.get(0));
			else if (group2.size() == 2)
				group1.add(new DualExponentiation(group2.get(0), group2.get(1)));
			else group1.add(new Exponentiation(group2));
			group1.add(this);
			return new Product(group1);
		}
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Do not know how to take the antiderivative of an arbitrary exponentiation (yet).");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return NO;
	}
	@Override public Compare isPositive() {
		return MAYBE;
	}
	@Override public Compare isNegative() {
		return MAYBE;
	}
	@Override public Compare isReal() {
		return MAYBE;
	}
	@Override public Compare isImag() {
		return MAYBE;
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Expr arg : args) {
			String str = arg.toString();
			if (arg instanceof DualSum || arg instanceof Sum ||
					arg instanceof DualProduct || arg instanceof Product ||
					arg instanceof Quotient) {
				str = "(" + str + ")";
			}
			sb.append(str).append(" ^ ");
		}
		String str = sb.toString();
		return str.substring(0, str.length()-3);
	}
	
	@Override public DualFunction getDualFunction(Expr left, Expr right) {
		return new DualExponentiation(left, right);
	}
	
}
