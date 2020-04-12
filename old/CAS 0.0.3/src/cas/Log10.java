package cas;

import java.util.ArrayList;
import java.util.HashSet;

public class Log10 extends SingleArgumentFunction {

	public Log10(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Log10(Expr argument) {
		super(argument);
		if (getArgument().isZero() == YES) {
			throw new IllegalArgumentException("The argument of a logarithm cannot be zero.");
		}
	}
	
	public Expr simplifyFunction(HashSet<Expr> eq) {
		// log 1 = 0
		if (getArgument().equalTo(ONE)) {
			return ZERO;
		}
		// log 10 = 1
		if (getArgument().equalTo(TEN)) {
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
				terms[i] = new Log10(((Product)getArgument()).args.get(i));
			}
			return new Sum(terms).simplify(eq);
		}
		// Sum
		// ArcTan2
		// ComplexExpr
		// reduce to natural logs
		if (getArgument() instanceof ComplexExpr) {
			return new Quotient(new Log(getArgument()), new Log(TEN)).simplify(eq);
		}
		// Difference
		// DualExponentiation
		// log(x^y) = y log x
		if (getArgument() instanceof DualExponentiation) {
			return new DualProduct(((DualExponentiation)getArgument()).getPower(), new Log10(((DualExponentiation)getArgument()).getBase())).simplify(eq);
		}
		// DualProduct
		// log xy = log x + log y
		if (getArgument() instanceof DualProduct) {
			return new DualSum(new Log10(((DualProduct)getArgument()).getLeft()), new Log10(((DualProduct)getArgument()).getRight())).simplify(eq);
		}
		// DualSum
		// LogBase
		// Quotient
		// log(x/y) = log x - log y
		if (getArgument() instanceof Quotient) {
			return new Difference(new Log10(((Quotient)getArgument()).getNumerator()), new Log10(((Quotient)getArgument()).getDenominator())).simplify(eq);
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
			terms.add(new Log10(exp.args.get(0)));
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
		// reduce to natural logs
		if (getArgument() instanceof ImaginaryExpr) {
			return new Quotient(new Log(getArgument()), new Log(TEN)).simplify(eq);
		}
		// Inverse
		// log(1/x) = -log x
		if (getArgument() instanceof Inverse) {
			return new Negative(new Log10(((Inverse)getArgument()).getArgument()));
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
			return new DualProduct(oneHalf, new Log10(((Sqrt)getArgument()).getArgument())).simplify(eq);
		}
		// Tan
		// Complex
		// reduce to natural logs
		if (getArgument() instanceof Complex) {
			return new Quotient(new Log(getArgument()), new Log(TEN)).simplify(eq);
		}
		// Exponent
		// log(a^b) = b log a
		if (getArgument() instanceof Exponent) {
			return new DualProduct(((Exponent)getArgument()).power, new Log10(((Exponent)getArgument()).base)).simplify(eq);
		}
		// Fraction
		// log(a/b) = log a - log b
		if (getArgument() instanceof Fraction) {
			return new Difference(new Log10(((Fraction)getArgument()).numerator), new Log10(((Fraction)getArgument()).denominator)).simplify(eq);
		}
		// Imaginary
		// reduce to natural logs
		if (getArgument() instanceof Imaginary) {
			return new Quotient(new Log(getArgument()), new Log(TEN)).simplify(eq);
		}
		// Integer
		// Multiple
		// log xyz = log x + log y + log z
		if (getArgument() instanceof Multiple) {
			Expr[] terms = new Expr[((Multiple)getArgument()).values.size()];
			for (int i=0; i<((Multiple)getArgument()).values.size(); i++) {
				terms[i] = new Log10(((Multiple)getArgument()).values.get(i));
			}
			return new Sum(terms).simplify(eq);
		}
		// NumNumSum
		// Transcendental
		// PolynomialTerm
		// log(ax^b) = log(a) + b log(x)
		if (getArgument() instanceof PolynomialTerm) {
			PolynomialTerm term = (PolynomialTerm)getArgument();
			return new DualSum(new Log10(term.coefficient), new DualProduct(term.power, new Log10(term.variable))).simplify(eq);
		}
		// Variable
		
		if (getArgument() instanceof Number) return new FunctionConstant(this);
		return this;
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new DualProduct(getArgument(), new Log(TEN)).inverse().simplify(null);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Cannot find antiderivative of arbitrary log10(f(x)) dx");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return getArgument()._equalTo(ONE);
	}
	public Compare isPositive() {
		return new Difference(getArgument(), ONE).simplify(null).isPositive();
	}
	public Compare isNegative() {
		return isReal().and(new Difference(getArgument(), ONE).simplify(null).isPositive());
	}
	public Compare isReal() {
		return getArgument().isPositive();
	}
	public Compare isImag() {
		return new DualSum(new Re(getArgument()).square(), new Im(getArgument()).square()).simplify(null)._equalTo(ONE);
	}
	
}
