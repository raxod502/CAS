package com.apprisingsoftware.cas;

import java.util.ArrayList;

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
	
	@Override public Expr derivative(Variable var) {
		ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
		for (int i=0; i<args.size(); i++) {
			newArgs.set(i, newArgs.get(i).derivative(var));
		}
		return new Sum(newArgs);
	}
	@Override public Expr derivativePartial(int var) {
		if (var >= args.size()) return ZERO;
		return ONE;
	}
	@Override public Expr antiderivative(Variable var) {
		ArrayList<Expr> anti = new ArrayList<Expr>();
		for (int i=0; i<args.size(); i++) {
			anti.add(args.get(i).antiderivative(var));
		}
		return new Sum(anti).getSimplest();
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
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
	@Override public Compare isPositive() {
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
	@Override public Compare isNegative() {
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
	@Override public Compare isReal() {
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
	@Override public Compare isImag() {
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
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Expr arg : args) {
			String str = arg.toString();
			sb.append(str).append(" + ");
		}
		String str = sb.toString();
		return str.substring(0, str.length()-3);
	}
	
	@Override public DualFunction getDualFunction(Expr left, Expr right) {
		return new DualSum(left, right);
	}
	
	@Override public Expr makeUseful() {
		Sum newExpr = (Sum)super.makeUseful();
		for (int i=0; i<newExpr.args.size(); i++) {
			if (newExpr.args.get(i).equals(ZERO)) {
				newExpr.args.remove(i); // Pointer problem?
				i--;
			}
		}
		if (newExpr.args.size() == 0) return ZERO;
		if (newExpr.args.size() == 1) return newExpr.args.get(0);
		if (newExpr.args.size() == 2) return new DualSum(newExpr.args.get(0), newExpr.args.get(1));
		return newExpr;
	}
	
}
