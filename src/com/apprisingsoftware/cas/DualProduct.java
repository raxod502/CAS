package com.apprisingsoftware.cas;

import java.util.ArrayList;

class DualProduct extends DualFunction {
	
	public DualProduct(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualProduct(Expr left, Expr right) {
		super(left, right);
	}
	
	@Override public Expr getAlternateWithoutCommutivity() {
		// SPECIAL CASES
		
		// 0*x = 0
		if (getLeft().isZero() == YES) {
			return ZERO;
		}
		// 1*x = 1
		if (getLeft().equalTo(ONE)) {
			return getRight();
		}
		// 1/x * x = 1
		if (getLeft().inverse().getSimplest().equalTo(getRight())) {
			return ONE;
		}
		// x*x = x^2
		if (getLeft().equalTo(getRight())) {
			return new DualExponentiation(getLeft(), TWO);
		}
		
		// ArgumentContainer
		if (getRight() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot multiply an ArgumentContainer.");
		}
		// Product
		if (getRight() instanceof Product) {
			Product arg = (Product)getRight();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.args);
			newArgs.add(0, getLeft());
			return new Product(newArgs);
		}
		// Sum
		// x * (a+b+c) = x*a + x*b + x*c
		if (getRight() instanceof Sum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((Sum)getRight()).args);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new DualProduct(getLeft(), args.get(i)));
			}
			return new Product(newArgs);
		}
		// ArcTan2
		// ComplexExpr
		// x * (a + bi) = x*a + (x*b)i
		if (getRight() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getRight();
			return new ComplexExpr(new DualProduct(getLeft(), arg.getReal()), new DualProduct(getLeft(), arg.getImag()));
		}
		// Difference
		// x * (a-b) = x*a - x*b
		if (getRight() instanceof Difference) {
			Difference diff = (Difference)getRight();
			return new Difference(new DualProduct(getLeft(), diff.getLeft()), new DualProduct(getLeft(), diff.getRight()));
		}
		// DualExponentiation
		// x^a * x^b = x^(a+b)
		if (getRight() instanceof DualExponentiation &&
				getLeft() instanceof DualExponentiation &&
				((DualExponentiation)getLeft()).getBase().equalTo(((DualExponentiation)getRight()).getBase())) {
			DualExponentiation left = (DualExponentiation)getLeft(), right = (DualExponentiation)getRight();
			return new DualExponentiation(left.getBase(), new DualSum(left.getPower(), right.getPower()));
		}
		// DualProduct
		// a*(b*c) = a*b*c
		if (getRight() instanceof DualProduct) {
			DualProduct arg = (DualProduct)getRight();
			return new Product(getLeft(), arg.getLeft(), arg.getRight());
		}
		// DualSum
		// x * (a+b) = x*a + x*b
		if (getRight() instanceof DualSum) {
			DualSum arg = (DualSum)getRight();
			return new DualSum(new DualProduct(getLeft(), arg.getLeft()), new DualProduct(getLeft(), arg.getRight()));
		}
		// LogBase
		// Quotient
		// x * (y/z) = (x*y)/z
		if (getRight() instanceof Quotient) {
			Quotient arg = (Quotient)getRight();
			return new Quotient(new DualProduct(getLeft(), arg.getNumerator()), arg.getDenominator());
		}
		// Exponentiation
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// e^a * e^b = e^(a+b)
		if (getRight() instanceof Exp && getLeft() instanceof Exp) {
			Exp left = (Exp)getLeft(), right = (Exp)getRight();
			return new Exp(new DualSum(left.getArgument(), right.getArgument()));
		}
		// Im
		// ImaginaryExpr
		// a * bi = (ab)i
		if (getRight() instanceof ImaginaryExpr) {
			ImaginaryExpr right = (ImaginaryExpr)getRight();
			return new ImaginaryExpr(new DualProduct(getLeft(), right.getArgument()));
		}
		// Inverse
		// a * 1/b = a/b
		if (getRight() instanceof Inverse) {
			return new Quotient(getLeft(), ((Inverse)getRight()).getArgument());
		}
		// Log
		// Log10
		// Negative
		// a * -b = -(ab)
		if (getRight() instanceof Negative) {
			return new DualProduct(getLeft(), ((Negative)getRight()).getArgument()).negative();
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		if (getRight() instanceof Sqrt && getLeft() instanceof Sqrt) {
			return new Sqrt(new DualProduct(((Sqrt)getLeft()).getArgument(), ((Sqrt)getRight()).getArgument()));
		}
		// Tan
		// Complex
		// x * (a + bi) = x*a + (x*b)i
		if (getRight() instanceof Complex) {
			Complex arg = (Complex)getRight();
			return new ComplexExpr(new DualProduct(getLeft(), arg.real), new DualProduct(getLeft(), arg.imag));
		}
		// Exponent
		// x^a * x^b = x^(a+b)
		if (getRight() instanceof Exponent &&
				getLeft() instanceof Exponent &&
				((Exponent)getLeft()).base.equalTo(((Exponent)getRight()).base)) {
			Exponent left = (Exponent)getLeft(), right = (Exponent)getRight();
			return new Exponent(left.base, (NumIntegerTranscendentalFunctionConstant)new DualSum(left.power, right.power).getSimplest());
		}
		// Fraction
		// x * y/z = (x*y)/z
		if (getRight() instanceof Fraction) {
			Fraction arg = (Fraction)getRight();
			return new Quotient(new DualProduct(getLeft(), arg.numerator), arg.denominator);
		}
		// FunctionConstant
		// Imaginary
		if (getRight() instanceof Imaginary) {
			Imaginary right = (Imaginary)getRight();
			return new ImaginaryExpr(new DualProduct(getLeft(), right.value));
		}
		// Integer
		if (getRight() instanceof Integer && getLeft() instanceof Integer) {
			Integer left = (Integer)getLeft(), right = (Integer)getRight();
			if (Math.log10(left.value) + Math.log10(right.value) > Math.log10(Long.MAX_VALUE)) {
				throw new ArithmeticException("Overflow error while computing product " + this + ".");
			}
			else {
				return new Integer(left.value * right.value);
			}
		}
		// Multiple
		if (getRight() instanceof Product) {
			Multiple arg = (Multiple)getRight();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.values);
			newArgs.add(0, getLeft());
			return new Product(newArgs);
		}
		// NumNumSum
		// x * (a+b+c) = x*a + x*b + x*c
		if (getRight() instanceof NumberSum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((NumberSum)getRight()).values);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new DualProduct(getLeft(), args.get(i)));
			}
			return new Product(newArgs);
		}
		// Transcendental
		// PolynomialTerm
		if (getRight() instanceof PolynomialTerm) {
			PolynomialTerm poly = (PolynomialTerm)getRight();
			// ax^b * cx^d = (a*c)x^(b+d)
			if (getLeft() instanceof PolynomialTerm && poly.variable.equalTo(((PolynomialTerm)getLeft()).variable)) {
				PolynomialTerm polyL = (PolynomialTerm)getLeft();
				return new PolynomialTerm((NumNumSum)new DualProduct(poly.coefficient, polyL.coefficient).getSimplest(), poly.variable, (Integer)new DualSum(poly.power, polyL.power).getSimplest());
			}
			return new Product(getLeft(), poly.coefficient, new DualExponentiation(poly.variable, poly.power));
		}
		// Variable
		
		if (getLeft() instanceof Number && getRight() instanceof Number) {
			ArrayList<NumExponent> args = new ArrayList<>();
			if (getLeft() instanceof Multiple) {
				args.addAll(((Multiple)getLeft()).values);
			}
			else if (getLeft() instanceof NumExponent) {
				args.add((NumExponent)getLeft());
			}
			else {
				throw new AssertionError();
			}
			if (getRight() instanceof Multiple) {
				args.addAll(((Multiple)getRight()).values);
			}
			else if (getRight() instanceof NumExponent) {
				args.add((NumExponent)getRight());
			}
			else {
				throw new AssertionError();
			}
			if (args.size() < 2) throw new AssertionError();
			return new Multiple(args);
		}
		return this;
	}
	@Override public Expr derivative(Variable var) {
		return new DualSum(new DualProduct(getLeft(), getRight().derivative(var)), new DualProduct(getLeft().derivative(var), getRight()));
	}
	@Override public Expr derivativePartial(int var) {
		if (var == 0) return getRight();
		if (var == 1) return getLeft();
		return ZERO;
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
			if (isZero != NO) return isZero;
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
		String left = getLeft().toString(),
				right = getRight().toString();
		if (getLeft() instanceof DualSum || getLeft() instanceof Sum) {
			left = "(" + left + ")";
		}
		if (getRight() instanceof DualSum || getRight() instanceof Sum) {
			right = "(" + right + ")";
		}
		return left + " * " + right;
	}
	
	@Override public Expr makeUseful() {
		DualProduct newExpr = (DualProduct)super.makeUseful();
		if (newExpr.getLeft().equals(ONE)) {
			return newExpr.getRight();
		}
		if (newExpr.getRight().equals(ONE)) {
			return newExpr.getLeft();
		}
		if (newExpr.getLeft().equals(ZERO) || newExpr.getRight().equals(ZERO)) {
			return ZERO;
		}
		return newExpr;
	}
	
}
