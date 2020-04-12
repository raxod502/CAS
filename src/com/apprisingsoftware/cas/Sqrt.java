package com.apprisingsoftware.cas;

import java.util.ArrayList;

class Sqrt extends SingleArgumentFunction {
	
	public Sqrt(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Sqrt(Expr arg) {
		super(arg);
	}
	
	@Override public Expr getAlternate() {
		// ArgumentContainer
		if (getArgument() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot Sqrt an ArgumentContainer.");
		}
		// Product
		// √(xyz) = √x √y √z
		if (getArgument() instanceof Product) {
			Product product = (Product)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr arg : product.args) {
				newArgs.add(new Sqrt(arg));
			}
			return new Product(newArgs);
		}
		// Sum
		// ArcTan2
		// ComplexExpr
		// Difference
		// DualExponentiation
		// √(x^y) = x^(y/2) = x^y^(1/2)
		if (getArgument() instanceof DualExponentiation) {
			DualExponentiation de = (DualExponentiation)getArgument();
			return new Exponentiation(de.getBase(), de.getPower(), oneHalf);
		}
		// DualProduct
		// √(xy) = √x √y
		if (getArgument() instanceof DualProduct) {
			DualProduct dp = (DualProduct)getArgument();
			return new DualProduct(new Sqrt(dp.getLeft()), new Sqrt(dp.getRight()));
		}
		// DualSum
		// LogBase
		// Quotient
		// √(x/y) = √x / √y
		if (getArgument() instanceof Quotient) {
			Quotient q = (Quotient)getArgument();
			return new Quotient(new Sqrt(q.getNumerator()), new Sqrt(q.getDenominator()));
		}
		// Exponentiation
		// √(x^y^z) = x^y^z^(1/2)
		if (getArgument() instanceof Exponentiation) {
			Exponentiation exp = (Exponentiation)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(exp.args);
			newArgs.add(oneHalf);
			return new Exponentiation(newArgs);
		}
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// √(e^x) = e^x^(1/2)
		if (getArgument() instanceof Exp) {
			return new Exponentiation(E, ((Exp)getArgument()).getArgument(), oneHalf);
		}
		// Im
		// ImaginaryExpr
		// √(ix) = [1/√2 + i/√2]*√x
		if (getArgument() instanceof ImaginaryExpr) {
			Expr arg = ((ImaginaryExpr)getArgument()).getArgument();
			Complex rootI = new Complex(new Fraction(ONE, new Exponent(TWO, oneHalf)), new Fraction(ONE, new Exponent(TWO, oneHalf)));
			return new DualProduct(rootI, new Sqrt(arg));
		}
		// Inverse
		// √(1/x) = 1/√x
		if (getArgument() instanceof Inverse) {
			return new Sqrt(((Inverse)getArgument()).getArgument()).inverse();
		}
		// Log
		// Log10
		// Negative
		// √(-x) = i√x
		if (getArgument() instanceof Negative) {
			return new ImaginaryExpr(new Sqrt(((Negative)getArgument()).getArgument()));
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// √√x = x^(1/4)
		if (getArgument() instanceof Sqrt) {
			return new DualExponentiation(((Sqrt)getArgument()).getArgument(), oneFourth);
		}
		// Tan
		// Complex
		// Exponent
		// √(x^y) = x^(y/2)
		if (getArgument() instanceof Exponent) {
			Exponent exp = (Exponent)getArgument();
			return new Exponent(exp.base, (NumFraction)new DualProduct(exp.power, oneHalf).getSimplest());
		}
		// Fraction
		// √(x/y) = √x/√y
		if (getArgument() instanceof Fraction) {
			Fraction frac = (Fraction)getArgument();
			return new Fraction((NumNumSum)new Sqrt(frac.numerator).getSimplest(), (NumNumSum)new Sqrt(frac.denominator).getSimplest());
		}
		// FunctionConstant
		// Imaginary
		// √(ix) = [1/√2 + i/√2]*√x
		if (getArgument() instanceof Imaginary) {
			Number arg = ((Imaginary)getArgument()).value;
			Complex rootI = new Complex(new Fraction(ONE, new Exponent(TWO, oneHalf)), new Fraction(ONE, new Exponent(TWO, oneHalf)));
			return new DualProduct(rootI, new Sqrt(arg));
		}
		// Integer
		if (getArgument() instanceof Integer) {
			long value = ((Integer)getArgument()).value;
			ArrayList<Long> factors = Util.uniquePrimeFactorList(value);
			int repeats = 0;
			long lastFactor = -1;
			long squareProduct = 1;
			for (int i=0; i<factors.size(); i++) {
				if (repeats == 2) {
					squareProduct *= lastFactor;
					value /= lastFactor * lastFactor;
					repeats = 0;
				}
				else {
					if (factors.get(i) != lastFactor) {
						repeats = 1;
						lastFactor = factors.get(i);
					}
					else {
						repeats += 1;
					}
				}
			}
			if (squareProduct > 1) {
				return new DualProduct(new Integer(squareProduct), new Sqrt(new Integer(value)));
			}
		}
		// Multiple
		// √(xyz) = √x √y √z
		if (getArgument() instanceof Multiple) {
			Multiple product = (Multiple)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr arg : product.values) {
				newArgs.add(new Sqrt(arg));
			}
			return new Product(newArgs);
		}
		// NumNumSum
		// Transcendental
		
		// √x = x^(1/2)
		if (getArgument() instanceof Number) {
			return new Exponent((NumFraction)getArgument(), oneHalf).reduce();
		}
		else {
			return new DualExponentiation(getArgument(), oneHalf);
		}
	}
	@Override public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		// d/dx √x = 1/(2√x)
		return new DualProduct(TWO, new Sqrt(getArgument())).inverse().getSimplest();
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Cannot antideviate an arbitrary square root, yet...");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return getArgument().isZero();
	}
	@Override public Compare isPositive() {
		return getArgument().isPositive();
	}
	@Override public Compare isNegative() {
		return NO;
	}
	@Override public Compare isReal() {
		return getArgument().isPositive();
	}
	@Override public Compare isImag() {
		return getArgument().isNegative();
	}
}
