package com.apprisingsoftware.cas;

import java.util.ArrayList;

class Exp extends SingleArgumentFunction {
	
	public Exp(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Exp(Expr arg) {
		super(arg);
	}
	
	@Override public Expr getAlternate() {
		// e^0 = 1
		if (getArgument().equalTo(ZERO)) {
			return ONE;
		}
		// e^1 = e
		if (getArgument().equalTo(ONE)) {
			return E;
		}
		
		// Product
		// e^(xyz) = e^x^y^z
		if (getArgument() instanceof Product) {
			Product arg = (Product)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.args);
			newArgs.add(0, E);
			return new Exponentiation(newArgs);
		}
		// Sum
		// e^(a+b+c) = e^a * e^b * e^c
		if (getArgument() instanceof Sum) {
			Sum arg = (Sum)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr expr : arg.args) {
				newArgs.add(new Exp(expr));
			}
			return new Product(newArgs);
		}
		// ArcTan2
		// ComplexExpr
		// e^(a+bi) = e^a * e^(bi)
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getArgument();
			return new DualProduct(new Exp(arg.getReal()), new Exp(new ImaginaryExpr(arg.getImag())));
		}
		// Difference
		// e^(a-b) = e^a / e^b
		if (getArgument() instanceof Difference) {
			Difference arg = (Difference)getArgument();
			return new Quotient(new Exp(arg.getLeft()), new Exp(arg.getRight()));
		}
		// DualExponentiation
		// e^(a^b)
		// DualProduct
		// e^(xy) = e^x^y
		if (getArgument() instanceof DualProduct) {
			DualProduct arg = (DualProduct)getArgument();
			return new Exponentiation(E, arg.getLeft(), arg.getRight());
		}
		// DualSum
		// e^(x+y) = e^x * e^y
		if (getArgument() instanceof DualSum) {
			DualSum arg = (DualSum)getArgument();
			return new DualProduct(new Exp(arg.getLeft()), new Exp(arg.getRight()));
		}
		// LogBase
		// e^logbase(b, c) = e^(ln[b]/ln[c]) = e^(ln[b])^(1/ln[c]) = b^(1/ln[c])
		if (getArgument() instanceof LogBase) {
			LogBase arg = (LogBase)getArgument();
			return new DualExponentiation(arg.getBase(), new Log(arg.getArgument()).inverse());
		}
		// Quotient
		// e^(b/c) = e^b^(1/c)
		if (getArgument() instanceof Quotient) {
			Quotient arg = (Quotient)getArgument();
			return new Exponentiation(E, arg.getNumerator(), arg.getDenominator().inverse());
		}
		// Exponentiation
		// e^(x^y^z)
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// Im
		// ImaginaryExpr
		// e^(xi) = cis(x)
		if (getArgument() instanceof ImaginaryExpr) {
			return new Cis(((ImaginaryExpr)getArgument()).getArgument());
		}
		// Inverse
		// Log
		// e^log(x) = x
		if (getArgument() instanceof Log) {
			return ((Log)getArgument()).getArgument();
		}
		// Log10
		// e^log10(x) = x^(1/log(10))
		if (getArgument() instanceof Log10) {
			return new DualExponentiation(((Log10)getArgument()).getArgument(), new Log(TEN).inverse());
		}
		// Negative
		// e^(-x) = 1/e^x
		if (getArgument() instanceof Negative) {
			Negative arg = (Negative)getArgument();
			return new Exp(arg.getArgument()).inverse();
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// e^√x
		// Tan
		// Complex
		// e^(a+bi) = e^a * e^(bi)
		if (getArgument() instanceof Complex) {
			Complex arg = (Complex)getArgument();
			return new DualProduct(new Exp(arg.real), new Exp(new Imaginary(arg.imag)));
		}
		// Exponent
		// Fraction
		// e^(x/y) = e^x^(1/y)
		if (getArgument() instanceof Fraction) {
			Fraction arg = (Fraction)getArgument();
			return new Exponentiation(E, arg.numerator, arg.denominator.inverse());
		}
		// FunctionConstant
		// Imaginary
		// e^(xi) = cis(x)
		if (getArgument() instanceof Imaginary) {
			return new Cis(((Imaginary)getArgument()).value);
		}
		// Integer
		// Multiple
		// e^(xyz) = e^x^y^z
		if (getArgument() instanceof Multiple) {
			Multiple arg = (Multiple)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.values);
			newArgs.add(0, E);
			return new Exponentiation(newArgs);
		}
		// NumNumSum
		// e^(a+b+c) = e^a * e^b * e^c
		if (getArgument() instanceof NumberSum) {
			NumberSum arg = (NumberSum)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr expr : arg.values) {
				newArgs.add(new Exp(expr));
			}
			return new Product(newArgs);
		}
		// Transcendental
		// PolynomialTerm
		// e^(cx^y) = e^c^(x^y)
		if (getArgument() instanceof PolynomialTerm) {
			PolynomialTerm arg = (PolynomialTerm)getArgument();
			return new Exponentiation(E, arg.coefficient, new DualExponentiation(arg.variable, arg.power));
		}
		// Variable
		
//		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	@Override public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return this;
	}
	@Override public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Don't know how to take the antiderivative of an arbitrary Exp[f(x)]... yet!");
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return NO;
	}
	@Override public Compare isPositive() {
		Compare cond1 = isReal();
		Expr imPart = new Im(getArgument()).getSimplest();
		Compare cond2 = MAYBE;
		if (imPart instanceof Number) {
			cond2 = Cos.isDivisible((Number)imPart, ZERO) ? YES : NO;
		}
		return cond1.and(cond2);
	}
	@Override public Compare isNegative() {
		Compare cond1 = isReal();
		Expr imPart = new Im(getArgument()).getSimplest();
		Compare cond3 = MAYBE;
		if (imPart instanceof Number) {
			cond3 = Tan.isDivisible((Number)imPart, pi) ? YES : NO;
		}
		return cond1.and(cond3);
	}
	@Override public Compare isReal() {
		Expr imPart = new Im(getArgument()).getSimplest();
		if (imPart instanceof Number) {
			return Tan.isDivisible((Number)imPart, ZERO) ? YES : NO;
		}
		return MAYBE;
	}
	@Override public Compare isImag() {
		Expr imPart = new Im(getArgument()).getSimplest();
		if (imPart instanceof Number) {
			return Tan.isDivisible((Number)imPart, new Multiple(oneHalf, pi)) ? YES : NO;
		}
		return MAYBE;
	}
	
}
