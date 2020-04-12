package cas;

import java.util.ArrayList;
import java.util.HashSet;

public class Inverse extends SingleArgumentFunction {
	
	public Inverse(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Inverse(Expr arg) {
		super(arg);
	}
	
	public Expr simplifyFunction(HashSet<Expr> eq) {
		// 1/1 = 1
		if (getArgument().equalTo(ONE)) {
			return ONE;
		}
		// Product
		// 1/(xyz) = 1/x * 1/y * 1/z
		if (getArgument() instanceof Product) {
			Product arg = (Product)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr expr : arg.args) {
				newArgs.add(new Inverse(expr));
			}
			return new Product(newArgs).simplify(eq);
		}
		// Sum
		// ArcTan2
		// ComplexExpr
		// 1/(a+bi) = a/(a^2+b^2) - ib/(a^2+b^2)
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr arg = (ComplexExpr)getArgument();
			Expr a = arg.getReal(), b = arg.getImag();
			return new ComplexExpr(
					new Quotient(a, new DualSum(a.square(), b.square())),
					new Quotient(b, new DualSum(a.square(), b.square())).negative()).simplify(eq);
		}
		// Difference
		// DualExponentiation
		// 1/(x^y) = x^(-y)
		if (getArgument() instanceof DualExponentiation) {
			DualExponentiation arg = (DualExponentiation)getArgument();
			return new DualExponentiation(arg.getBase(), arg.getPower().negative()).simplify(eq);
		}
		// DualProduct
		// 1/(xy) = 1/x * 1/y
		if (getArgument() instanceof DualProduct) {
			DualProduct arg = (DualProduct)getArgument();
			return new DualProduct(arg.getLeft().inverse(), arg.getRight().inverse()).simplify(eq);
		}
		// LogBase
		// 1/logbase(b, c) = logbase(c, b)
		if (getArgument() instanceof LogBase) {
			LogBase arg = (LogBase)getArgument();
			return new LogBase(arg.getArgument(), arg.getBase()).simplify(eq);
		}
		// Quotient
		// 1/(x/y) = y/x
		if (getArgument() instanceof Quotient) {
			Quotient arg = (Quotient)getArgument();
			return new Quotient(arg.getDenominator(), arg.getNumerator()).simplify(eq);
		}
		// Exponentiation
		// 1/(x^y^z) = (x^y)^(-z)
		if (getArgument() instanceof Exponentiation) {
			Exponentiation arg = (Exponentiation)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.args);
			Expr power = newArgs.get(newArgs.size()-1); newArgs.remove(newArgs.size()-1);
			return new DualExponentiation(newArgs.size() == 2 ? new DualExponentiation(newArgs.get(0), newArgs.get(1)) : new Exponentiation(newArgs),
					power.negative()).simplify(eq);
		}
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		if (getArgument() instanceof Cis) {
			Cis arg = (Cis)getArgument();
			return new ComplexExpr(new Cos(arg.getArgument()), new Sin(arg.getArgument()).negative()).simplify(eq);
		}
		// Cos
		// Exp
		// 1/e^x = e^(-x)
		if (getArgument() instanceof Exp) {
			Exp arg = (Exp)getArgument();
			return new Exp(arg.getArgument().negative()).simplify(eq);
		}
		// Im
		// ImaginaryExpr
		// 1/(ix) = -i/x
		if (getArgument() instanceof ImaginaryExpr) {
			ImaginaryExpr arg = (ImaginaryExpr)getArgument();
			return new ImaginaryExpr(arg.getArgument().inverse().negative()).simplify(eq);
		}
		// Inverse
		// 1/(1/x)
		if (getArgument() instanceof Inverse) {
			return ((Inverse)getArgument()).getArgument();
		}
		// Log
		// Log10
		// Negative
		// 1/-x = -1/x
		if (getArgument() instanceof Negative) {
			return ((Negative)getArgument()).getArgument().inverse().negative().simplify(eq);
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// 1/√x = x^(-1/2)
		if (getArgument() instanceof Sqrt) {
			return new DualExponentiation(((Sqrt)getArgument()).getArgument(), negOneHalf).simplify(eq);
		}
		// Tan
		// Complex
		// 1/(a+bi) = a/(a^2+b^2) - ib/(a^2+b^2)
		if (getArgument() instanceof ComplexExpr) {
			Complex arg = (Complex)getArgument();
			Expr a = arg.real, b = arg.imag;
			return new Complex(
					(NumNumSum)new Quotient(a, new DualSum(a.square(), b.square())).simplify(null),
					(NumNumSum)new Quotient(b, new DualSum(a.square(), b.square())).negative().simplify(null));
		}
		// Exponent
		// 1/(e^π) = e^(-π)
		if (getArgument() instanceof Exponent) {
			Exponent arg = (Exponent)getArgument();
			return new Exponent(arg.base, (NumIntegerTranscendentalFunctionConstant)arg.power.negative().simplify(null));
		}
		// Fraction
		// 1/(a/b) = b/a
		if (getArgument() instanceof Fraction) {
			Fraction arg = (Fraction)getArgument();
			if (arg.numerator.equalTo(ONE)) {
				return arg.denominator;
			}
			return new Fraction(arg.denominator, arg.numerator);
		}
		// FunctionConstant
		// Imaginary
		// 1/(ix) = -i/x
		if (getArgument() instanceof Imaginary) {
			Imaginary arg = (Imaginary)getArgument();
			return new Imaginary((NumNumSum)arg.value.inverse().negative()).simplify(eq);
		}
		// Integer
		if (getArgument() instanceof Integer) {
			return new Fraction(ONE, (Integer)getArgument());
		}
		// Multiple
		// 1/(xyz) = 1/x * 1/y * 1/z
		if (getArgument() instanceof Multiple) {
			Multiple arg = (Multiple)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr expr : arg.values) {
				newArgs.add(new Inverse(expr));
			}
			return new Product(newArgs).simplify(eq);
		}
		// NumNumSum
		// Transcendental
		// PolynomialTerm
		if (getArgument() instanceof PolynomialTerm) {
			PolynomialTerm arg = (PolynomialTerm)getArgument();
			return new PolynomialTerm((NumIntegerTranscendentalFunctionConstant)arg.coefficient.inverse().simplify(null), arg.variable, (Integer)arg.power.negative().simplify(null)).simplify(eq);
		}
		// Variable
		
		if (getArgument() instanceof Number) return new Fraction(ONE, (NumIntegerTranscendentalFunctionConstant)getArgument()).reduce(eq);
		return new Quotient(ONE, getArgument()).simplify(eq);
	}
	public Expr derivativePartial(int var) {
		if (var != 0) return ZERO;
		return new DualExponentiation(getArgument(), TWO).inverse().negative().simplify(null);
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Do not know how to take the antiderivative of an arbitrary inverse (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return NO;
	}
	public Compare isPositive() {
		return getArgument().isPositive();
	}
	public Compare isNegative() {
		return getArgument().isNegative();
	}
	public Compare isReal() {
		return new Im(getArgument()).simplify(null).isZero().and(new Re(getArgument()).simplify(null).isZero().not());
	}
	public Compare isImag() {
		return new Re(getArgument()).simplify(null).isZero().and(new Im(getArgument()).simplify(null).isZero().not());
	}
}
