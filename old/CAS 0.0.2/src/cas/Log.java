package cas;

import java.util.*;

public class Log extends SingleArgumentFunction {

	public Log(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Log(Expr argument) {
		super(argument);
		if (getArgument().isZero() == YES) {
			throw new IllegalArgumentException("The argument of a logarithm cannot be zero.");
		}
	}
	
	public Expr simplifyFunction() {
		// log 1 = 0
		if (getArgument().equalTo(ONE)) {
			return ZERO;
		}
		// log e = 1
		if (getArgument().equalTo(new Transcendental(Transcendent.E))) {
			return ONE;
		}
		
		// ArgumentContainer
		if (getArgument() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot logarithmize and ArgumentContainer.");
		}
		// Product
		// log xyz = log x + log y + log z
		if (getArgument() instanceof Product) {
			Expr[] terms = new Expr[((Product)getArgument()).args.size()];
			for (int i=0; i<((Product)getArgument()).args.size(); i++) {
				terms[i] = new Log(((Product)getArgument()).args.get(i));
			}
			return new Sum(terms).simplify();
		}
		// Sum
		// ArcTan2
		// ComplexExpr
		// log(a + bi) = log √(a^2+b^2) + i atan2(a, b)
		if (getArgument() instanceof ComplexExpr) {
			Expr real = ((ComplexExpr)getArgument()).getReal();
			Expr imag = ((ComplexExpr)getArgument()).getImag();
			return new ComplexExpr(new Log(new Sqrt(new Sum(new DualExponentiation(real, TWO),
					new DualExponentiation(imag, TWO)))),
					new Arg(getArgument())).simplify();
		}
		// Difference
		// DualExponentiation
		// log(x^y) = y log x
		if (getArgument() instanceof DualExponentiation) {
			return new DualProduct(((DualExponentiation)getArgument()).getPower(), new Log(((DualExponentiation)getArgument()).getBase())).simplify();
		}
		// DualProduct
		// log xy = log x + log y
		if (getArgument() instanceof DualProduct) {
			return new DualSum(new Log(((DualProduct)getArgument()).getLeft()), new Log(((DualProduct)getArgument()).getRight())).simplify();
		}
		// DualSum
		// LogBase
		// Quotient
		// log(x/y) = log x - log y
		if (getArgument() instanceof Quotient) {
			return new Difference(new Log(((Quotient)getArgument()).getNumerator()), new Log(((Quotient)getArgument()).getDenominator())).simplify();
		}
		// Exponentiation
		// log(x^y^z) = yz log x
		if (getArgument() instanceof Exponentiation) {
			Exponentiation exp = (Exponentiation)getArgument();
			ArrayList<Expr> terms = new ArrayList<Expr>();
			int i;
			for (i=0; i<exp.args.size()-1; i++) {
				terms.add(exp.args.get(i+1));
			}
			terms.add(new Log(exp.args.get(0)));
			return new Product(terms);
		}
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// log(e^x) = x
		if (getArgument() instanceof Exp) {
			return ((Exp)getArgument()).getArgument();
		}
		// Im
		// ImaginaryExpr
		// log(bi) = log b + i sgn(b) π/2
		if (getArgument() instanceof ImaginaryExpr) {
			Expr b = ((ImaginaryExpr)getArgument()).getArgument();
			return new ComplexExpr(new Log(b), new DualProduct(new Sgn(b), new Multiple(oneHalf, pi))).simplify();
		}
		// Inverse
		// log(1/x) = -log x
		if (getArgument() instanceof Inverse) {
			return new Negative(new Log(((Inverse)getArgument()).getArgument()));
		}
		// Log
		// Log10
		// Negative
		// Re
		// Sin
		// Sgn
		// Sqrt
		// log(√x) = 1/2 log(x)
		if (getArgument() instanceof Sqrt) {
			return new DualProduct(oneHalf, new Log(((Sqrt)getArgument()).getArgument())).simplify();
		}
		// Tan
		// Complex
		// log(a + bi) = log √(a^2+b^2) + i atan2(a, b)
		if (getArgument() instanceof Complex) {
			Number real = ((Complex)getArgument()).real;
			Number imag = ((Complex)getArgument()).imag;
			return new ComplexExpr(new Log(new Sqrt(new Sum(new DualExponentiation(real, TWO),
					new DualExponentiation(imag, TWO)))),
					new Arg(getArgument())).simplify();
		}
		// Exponent
		// log(a^b) = b log a
		if (getArgument() instanceof Exponent) {
			return new DualProduct(((Exponent)getArgument()).power, new Log(((Exponent)getArgument()).base)).simplify();
		}
		// Fraction
		// log(a/b) = log a - log b
		if (getArgument() instanceof Fraction) {
			return new Difference(new Log(((Fraction)getArgument()).numerator), new Log(((Fraction)getArgument()).denominator)).simplify();
		}
		// Imaginary
		// log(bi) = log b + i sgn(b) π/2
		if (getArgument() instanceof Imaginary) {
			Expr b = ((Imaginary)getArgument()).value;
			return new ComplexExpr(new Log(b), new DualProduct(new Sgn(b), new Multiple(oneHalf, pi))).simplify();
		}
		// Integer
		// Multiple
		// log xyz = log x + log y + log z
		if (getArgument() instanceof Multiple) {
			Expr[] terms = new Expr[((Multiple)getArgument()).values.size()];
			for (int i=0; i<((Multiple)getArgument()).values.size(); i++) {
				terms[i] = new Log(((Multiple)getArgument()).values.get(i));
			}
			return new Sum(terms).simplify();
		}
		// NumNumSum
		// Transcendental
		// PolynomialTerm
		// log(ax^b) = log(a) + b log(x)
		if (getArgument() instanceof PolynomialTerm) {
			PolynomialTerm term = (PolynomialTerm)getArgument();
			return new DualSum(new Log(term.coefficient), new DualProduct(term.power, new Log(term.variable))).simplify();
		}
		// Variable
		
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return getArgument().inverse();
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Cannot find antiderivative of arbitrary log(f(x)) dx");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return getArgument()._equalTo(ONE);
	}
	public Compare isPositive() {
		return new Difference(getArgument(), ONE).simplify().isPositive();
	}
	public Compare isNegative() {
		return isReal().and(new Difference(getArgument(), ONE).simplify().isPositive());
	}
	public Compare isReal() {
		return getArgument().isPositive();
	}
	public Compare isImag() {
		return new DualSum(new Re(getArgument()).square(), new Im(getArgument()).square()).simplify()._equalTo(ONE);
	}
	
}
