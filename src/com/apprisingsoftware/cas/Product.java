package com.apprisingsoftware.cas;

import java.util.ArrayList;

public final class Product extends AssociativeFunction {
	
	public Product(ArrayList<Expr> args) {
		super(args);
	}
	public Product(Expr... args) {
		super(args);
	}
	public Product(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public Product(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	
	@Override public Expr derivative(Variable var) {
		ArrayList<Expr> newArgs = new ArrayList<Expr>();
		for (int i=0; i<args.size(); i++) {
			ArrayList<Expr> argPack = new ArrayList<Expr>(args);
			argPack.set(i, argPack.get(i).derivative(var));
			newArgs.add(new Product(argPack));
		}
		return new Sum(newArgs).getSimplest();
	}
	@Override public Expr derivativePartial(int var) {
		if (var >= args.size()) return ZERO;
		ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
		newArgs.remove(var);
		return new Product(newArgs).getSimplest();
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Do not know how to take the antiderivative of an arbitrary product (yet).");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		for (Expr arg : args) {
			Compare isZero = arg.isZero();
			if (isZero == YES) {
				return YES;
			}
			else if (isZero == MAYBE) {
				return MAYBE;
			}
		}
		return NO;
	}
	@Override public Compare isPositive() {
		boolean sgn = true;
		for (Expr arg : args) {
			if (arg.isZero() == YES) {
				return NO;
			}
			Compare isPositive = arg.isPositive();
			if (isPositive == NO) {
				sgn = !sgn;
			}
			else if (isPositive == MAYBE) {
				return MAYBE;
			}
		}
		if (sgn) return YES;
		else return NO;
	}
	@Override public Compare isNegative() {
		boolean sgn = true;
		for (Expr arg : args) {
			if (arg.isZero() == YES) {
				return NO;
			}
			Compare isPositive = arg.isPositive();
			if (isPositive == NO) {
				sgn = !sgn;
			}
			else if (isPositive == MAYBE) {
				return MAYBE;
			}
		}
		if (!sgn) return YES;
		else return NO;
	}
	@Override public Compare isReal() {
		int type = 0; // Real = 0, Imag = 1, Complex = 2
		for (Expr arg : args) {
			Compare isReal = arg.isReal(), isImag = arg.isImag(), isComplex = arg.isComplex();
			if (isReal == YES) {
				//
			}
			else if (isImag == YES) {
				if (type == 0) type = 1;
				else if (type == 1) type = 0;
			}
			else if (isComplex == YES) {
				if (type == 0) type = 2;
				else if (type == 1) type = 2;
				else if (type == 2) return MAYBE;
			}
			else {
				return MAYBE;
			}
		}
		if (type == 0) {
			return YES;
		}
		else {
			return NO;
		}
	}
	@Override public Compare isImag() {
		int type = 0; // Real = 0, Imag = 1, Complex = 2
		for (Expr arg : args) {
			Compare isReal = arg.isReal(), isImag = arg.isImag(), isComplex = arg.isComplex();
			if (isReal == YES) {
				//
			}
			else if (isImag == YES) {
				if (type == 0) type = 1;
				else if (type == 1) type = 0;
			}
			else if (isComplex == YES) {
				if (type == 0) type = 2;
				else if (type == 1) type = 2;
				else if (type == 2) return MAYBE;
			}
			else {
				return MAYBE;
			}
		}
		if (type == 1) {
			return YES;
		}
		else {
			return NO;
		}
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Expr arg : args) {
			String str = arg.toString();
			if (arg instanceof DualSum || arg instanceof Sum) {
				str = "(" + str + ")";
			}
			sb.append(str).append(" * ");
		}
		String str = sb.toString();
		return str.substring(0, str.length()-3);
	}
	
	@Override public DualFunction getDualFunction(Expr left, Expr right) {
		return new DualProduct(left, right);
	}
	
	@Override public Expr makeUseful() {
		Product newExpr = (Product)super.makeUseful();
		for (int i=0; i<newExpr.args.size(); i++) {
			if (newExpr.args.get(i).equals(ONE)) {
				newExpr.args.remove(i); // Pointer problem?
				i--;
			}
			else if (newExpr.args.get(i).equals(ZERO)) {
				return ZERO;
			}
		}
		if (newExpr.args.size() == 0) return ONE;
		if (newExpr.args.size() == 1) return newExpr.args.get(0);
		if (newExpr.args.size() == 2) return new DualProduct(newExpr.args.get(0), newExpr.args.get(1));
		return newExpr;
	}
	
}
