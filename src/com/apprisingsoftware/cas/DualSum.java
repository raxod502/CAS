package com.apprisingsoftware.cas;

import java.util.ArrayList;

class DualSum extends DualFunction {
	
	public DualSum(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualSum(Expr left, Expr right) {
		super(left, right);
	}
	
	@Override public Expr getAlternateWithoutCommutivity() {
		// SPECIAL CASES
		
		// 0 + x = x
		if (getLeft().isZero() == YES) {
			return getRight();
		}
		// (-x) + x = 0
		if (getLeft().negative().getSimplest().equalTo(getRight())) {
			return ZERO;
		}
		// x + x = 2*x
		if (getLeft().equalTo(getRight())) {
			return new DualProduct(TWO, getLeft());
		}
		
		// ArgumentContainer
		if (getLeft() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot add an ArgumentContainer.");
		}
		// Product
		// [ Combine like terms ]
		// ac + bc = c(a + b)
		if ((getLeft() instanceof Product || getLeft() instanceof DualProduct || getLeft() instanceof Multiple) &&
				(getRight() instanceof Product || getRight() instanceof DualProduct || getRight() instanceof Multiple)) {
			ArrayList<Expr> largs = getLeft() instanceof Multiple ? new ArrayList<Expr>(((Multiple)getLeft()).values) : ((Function)getLeft()).args;
			ArrayList<Expr> rargs = getRight() instanceof Multiple ? new ArrayList<Expr>(((Multiple)getRight()).values) : ((Function)getRight()).args;
			Function left = largs.size() == 2 ? new DualProduct(largs.get(0), largs.get(1)) : new Product(new ArrayList<Expr>(largs));
			Function right = rargs.size() == 2 ? new DualProduct(rargs.get(0), rargs.get(1)) : new Product(new ArrayList<Expr>(rargs));
			ArrayList<Expr> factoredTerms = new ArrayList<Expr>();
			int i = 0;
			while (i < left.args.size()) {
				Expr arg = left.args.get(i);
				int j = 0;
				while (j < right.args.size()) {
					if (right.args.get(j).equalTo(arg)) {
						left.args.remove(i);
						right.args.remove(j);
						factoredTerms.add(arg);
						i -= 1;
						j -= 1;
					}
					j += 1;
				}
				i += 1;
			}
			if (left.args.size() == 0 || right.args.size() == 0) throw new AssertionError();
			Expr newLeft = left.args.size() == 1 ? left.args.get(0) :
				left.args.size() == 2 ? new DualProduct(left.args.get(0), left.args.get(1)) :
					new Product(left.args);
			Expr newRight = right.args.size() == 1 ? right.args.get(0) :
				right.args.size() == 2 ? new DualProduct(right.args.get(0), right.args.get(1)) :
					new Product(right.args);
			factoredTerms.add(new DualSum(newLeft, newRight));
			if (factoredTerms.size() == 0) throw new AssertionError();
			else if (factoredTerms.size() == 1) return factoredTerms.get(0);
			else if (factoredTerms.size() == 2) return new DualProduct(factoredTerms.get(0), factoredTerms.get(1));
			else return new Product(factoredTerms);
		}
		// c + bcdef = c(1 + bdef)
		if (getRight() instanceof Product || getRight() instanceof DualProduct || getRight() instanceof Multiple) {
			ArrayList<Expr> rargs = getRight() instanceof Multiple ? new ArrayList<Expr>(((Multiple)getRight()).values) : ((Function)getRight()).args;
			for (int i=0; i<rargs.size(); i++) {
				if (rargs.get(i).equals(getLeft())) {
					rargs.remove(i);
					if (rargs.size() == 0) throw new AssertionError();
					return new DualProduct(getLeft(), new DualSum(ONE,
							rargs.size() == 1 ? rargs.get(0) :
								rargs.size() == 2 ? new DualProduct(rargs.get(0), rargs.get(1)) :
									new Product(rargs)));
				}
			}
		}
		if ((getLeft() instanceof Product || getLeft() instanceof DualProduct || getLeft() instanceof Multiple) &&
				(getRight() instanceof Product || getRight() instanceof DualProduct || getRight() instanceof Multiple)) {
			ArrayList<Expr> largs = getLeft() instanceof Multiple ? new ArrayList<Expr>(((Multiple)getLeft()).values) : ((Function)getLeft()).args;
			ArrayList<Expr> rargs = getRight() instanceof Multiple ? new ArrayList<Expr>(((Multiple)getRight()).values) : ((Function)getRight()).args;
			Function left = largs.size() == 2 ? new DualProduct(largs.get(0), largs.get(1)) : new Product(new ArrayList<Expr>(largs));
			Function right = rargs.size() == 2 ? new DualProduct(rargs.get(0), rargs.get(1)) : new Product(new ArrayList<Expr>(rargs));
			ArrayList<Expr> factoredTerms = new ArrayList<Expr>();
			int i = 0;
			while (i < left.args.size()) {
				Expr arg = left.args.get(i);
				int j = 0;
				while (j < right.args.size()) {
					if (right.args.get(j).equalTo(arg)) {
						left.args.remove(i);
						right.args.remove(j);
						factoredTerms.add(arg);
						i -= 1;
						j -= 1;
					}
					j += 1;
				}
				i += 1;
			}
			if (left.args.size() == 0 || right.args.size() == 0) throw new AssertionError();
			Expr newLeft = left.args.size() == 1 ? left.args.get(0) :
				left.args.size() == 2 ? new DualProduct(left.args.get(0), left.args.get(1)) :
					new Product(left.args);
			Expr newRight = right.args.size() == 1 ? right.args.get(0) :
				right.args.size() == 2 ? new DualProduct(right.args.get(0), right.args.get(1)) :
					new Product(right.args);
			factoredTerms.add(new DualSum(newLeft, newRight));
			if (factoredTerms.size() == 0) throw new AssertionError();
			else if (factoredTerms.size() == 1) return factoredTerms.get(0);
			else if (factoredTerms.size() == 2) return new DualProduct(factoredTerms.get(0), factoredTerms.get(1));
			else return new Product(factoredTerms);
		}
		
		// Sum
		// (a+b+c)+d = a+b+c+d
		if (getLeft() instanceof Sum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((Sum)getLeft()).args);
			newArgs.add(getRight());
			return new Sum(newArgs);
		}
		// ArcTan2
		// ComplexExpr
		// [a + bi] + c = (a+c) + bi
		if (getLeft() instanceof ComplexExpr) {
			ComplexExpr left = (ComplexExpr)getLeft();
			return new ComplexExpr(new DualSum(left.getReal(), getRight()), left.getImag());
		}
		// Difference
		// a-b + c = a + (-b) + c
		if (getLeft() instanceof Difference) {
			Difference left = (Difference)getLeft();
			return new Sum(left.getLeft(), left.getRight().negative(), getRight());
		}
		// DualExponentiation
		// DualProduct - see Product
		// DualSum
		// (a+b)+c = a+b+c
		if (getLeft() instanceof DualSum) {
			DualSum left = (DualSum)getLeft();
			return new Sum(left.getLeft(), left.getRight(), getRight());
		}
		// LogBase
		// logbase(b, x) + logbase(b, y) = logbase(b, xy)
		if (getLeft() instanceof LogBase && getRight() instanceof LogBase &&
				((LogBase)getLeft()).getBase().equalTo(((LogBase)getRight()).getBase())) {
			LogBase left = (LogBase)getLeft(), right = (LogBase)getRight();
			return new LogBase(left.getBase(), new DualProduct(left.getArgument(), right.getArgument()));
		}
		// Quotient
		// a/c + b/c = (a+b)/c
		// a/b + c/d = (ad+bc)/(bd)
		if (getLeft() instanceof Quotient && getRight() instanceof Quotient) {
			Quotient left = (Quotient)getLeft(), right = (Quotient)getRight();
			if (left.getDenominator().equalTo(right.getDenominator())) {
				return new Quotient(new DualSum(left.getNumerator(), right.getNumerator()), left.getDenominator());
			}
			else {
				Expr a = left.getNumerator(), b = left.getDenominator(), c = right.getNumerator(), d = right.getDenominator();
				return new Quotient(new DualSum(new DualProduct(a, d), new DualProduct(b, c)), new DualProduct(b, d));
			}
		}
		// a/b + c = (a + bc)/b
		if (getLeft() instanceof Quotient) {
			Quotient left = (Quotient)getLeft();
			return new Quotient(new DualSum(left.getNumerator(),
					new DualProduct(left.getDenominator(), getRight())), left.getDenominator());
		}
		// Exponentiation
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// Im
		// ImaginaryExpr
		// bi + c = c + bi
		if (getLeft() instanceof ImaginaryExpr) {
			return new ComplexExpr(getRight(), ((ImaginaryExpr)getLeft()).getArgument());
		}
		// Inverse
		// 1/b + c = (1+bc)/b
		if (getLeft() instanceof Inverse) {
			Inverse left = (Inverse)getLeft();
			return new Quotient(new DualSum(ONE,
					new DualProduct(left.getArgument(), getRight())), left.getArgument());
		}
		// Log
		if (getLeft() instanceof Log && getRight() instanceof Log) {
			Log left = (Log)getLeft(), right = (Log)getRight();
			return new Log(new DualProduct(left.getArgument(), right.getArgument()));
		}
		// Log10
		if (getLeft() instanceof Log10 && getRight() instanceof Log10) {
			Log10 left = (Log10)getLeft(), right = (Log10)getRight();
			return new Log10(new DualProduct(left.getArgument(), right.getArgument()));
		}
		// Negative
		if (getLeft() instanceof Negative) {
			return new Difference(getRight(), ((Negative)getLeft()).getArgument());
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// Tan
		// Complex
		// (a+bi) + c = (a+c) + bi
		if (getLeft() instanceof Complex) {
			Complex left = (Complex)getLeft();
			return new ComplexExpr(new DualSum(left.real, getRight()), left.imag);
		}
		// Exponent
		// Fraction
		// a/b + c = (a+bc)/b
		if (getLeft() instanceof Fraction) {
			Fraction left = (Fraction)getLeft();
			return new Quotient(new DualSum(left.numerator,
					new DualProduct(left.denominator, getRight())), left.denominator);
		}
		// FunctionConstant
		// Imaginary
		// bi + c = c + bi
		if (getLeft() instanceof Imaginary) {
			return new ComplexExpr(getRight(), ((Imaginary)getLeft()).value);
		}
		// Integer
		if (getLeft() instanceof Integer && getRight() instanceof Integer) {
			Integer left = (Integer)getLeft(), right = (Integer)getRight();
			if (left.value + right.value > Long.MAX_VALUE) {
				throw new ArithmeticException("Overflow error while computing sum " + this + ".");
			}
			else {
				return new Integer(left.value + right.value);
			}
		}
		
		// NumNumSum
		// (a+b+c)+d = a+b+c+d
		if (getLeft() instanceof NumberSum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((NumberSum)getLeft()).values);
			newArgs.add(getRight());
			return new Sum(newArgs);
		}
		// Transcendental
		// PolynomialTerm
		if (getLeft() instanceof PolynomialTerm && getRight() instanceof PolynomialTerm) {
			PolynomialTerm left = (PolynomialTerm)getLeft(), right = (PolynomialTerm)getRight();
			if (left.variable.equalTo(right.variable) && left.power.equalTo(right.power)) {
				return new PolynomialTerm((NumNumSum)new DualSum(left.coefficient, right.coefficient).getSimplest(), left.variable, left.power);
			}
		}
		// Variable
		
		if (getLeft() instanceof Number && getRight() instanceof Number) {
			return new NumberSum((NumMultiple)getLeft(), (NumMultiple)getRight()).reduce();
		}
		return this;
	}
	@Override public Expr derivative(Variable var) {
		return new DualSum(getLeft().derivative(var), getRight().derivative(var));
	}
	@Override public Expr derivativePartial(int var) {
		if (var >= 2) return ZERO;
		return ONE;
	}
	@Override public Expr antiderivative(Variable var) {
		return new DualSum(getLeft().antiderivative(var), getRight().antiderivative(var));
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
		String left = getLeft().toString(),
				right = getRight().toString();
		return left + " + " + right;
	}
	
	@Override public Expr makeUseful() {
		DualSum newExpr = (DualSum)super.makeUseful();
		if (newExpr.getLeft().equals(ZERO)) {
			return newExpr.getRight();
		}
		if (newExpr.getRight().equals(ZERO)) {
			return newExpr.getLeft();
		}
		return newExpr;
	}
	
}
