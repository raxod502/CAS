package cas;

import java.util.ArrayList;
import java.util.HashSet;

public class DualExponentiation extends DualFunction {
	
	public DualExponentiation(int derivativeLeft, int derivativeRight,
			Expr left, Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualExponentiation(Expr left, Expr right) {
		super(left, right);
	}
	
	public Expr simplifyDualFunction(HashSet<Expr> eq) {
		// SPECIAL CASES
		
		// 0^0 is undefined.
		if (getBase().isZero() == YES && getPower().isZero() == YES) {
			throw new IllegalStateException("0^0 is undefined.");
		}
		// x^0 = 1
		if (getPower().isZero() == YES) {
			return ONE;
		}
		// x^1 = x
		if (getPower().equalTo(ONE)) {
			return getBase(); // Already simplified
		}
		// 0^x = 0
		if (getBase().isZero() == YES) {
			return ZERO;
		}
		// 1^x = 1
		if (getBase().equalTo(ONE)) {
			return ONE;
		}
		
		// POWER SIMPLIFICATION
		
		// ArgumentContainer
		if (getPower() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot exponentiate an ArgumentContainer.");
		}
		// Product
		// x^(a*b*c) = x^a^b^c [ = ((x^a)^b)^c ]
		if (getPower() instanceof Product) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
			newArgs.add(0, getBase());
			return new Exponentiation(newArgs).simplify(eq);
		}
		// Sum
		// x^(a+b+c) = x^a * x^b * x^c
		if (getPower() instanceof Sum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new DualExponentiation(getBase(), newArgs.get(i)));
			}
			return new Product(newArgs).simplify(eq);
		}
		// ArcTan2
		// ComplexExpr
		// x^(a+bi) = x^a * x^(bi)
		if (getPower() instanceof ComplexExpr) {
			ComplexExpr pow = (ComplexExpr)getPower();
			return new DualProduct(new DualExponentiation(getBase(), pow.getReal()),
					new DualExponentiation(getBase(), new ImaginaryExpr(pow.getImag()))).simplify(eq);
		}
		// Difference
		// x^(a-b) = x^a / x^b
		if (getPower() instanceof Difference) {
			Difference pow = (Difference)getPower();
			return new Quotient(new DualExponentiation(getBase(), pow.getLeft()), new DualExponentiation(getBase(), pow.getRight())).simplify(eq);
		}
		// DualExponentiation
		// x^(a^b)
		// DualProduct
		// x^(a*b) = x^a^b
		if (getPower() instanceof DualProduct) {
			DualProduct pow = (DualProduct)getPower();
			return new Exponentiation(getBase(), pow.getLeft(), pow.getRight()).simplify(eq);
		}
		// DualSum
		// x^(a+b) = x^a * x^b
		if (getPower() instanceof DualSum) {
			DualSum pow = (DualSum)getPower();
			return new DualProduct(new DualExponentiation(getBase(), pow.getLeft()), new DualExponentiation(getBase(), pow.getRight())).simplify(eq);
		}
		// LogBase
		if (getPower() instanceof LogBase) {
			// x^logbase(x, y) = y
			if (getBase().equalTo(((LogBase)getPower()).getBase())) {
				return ((LogBase)getPower()).getArgument().simplify(eq);
			}
		}
		// Quotient
		// x^(a/b) = x^a^(1/b)
		if (getPower() instanceof Quotient) {
			Quotient pow = (Quotient)getPower();
			// Prevent recursion
			if (!pow.getNumerator().equalTo(ONE)) {
				return new Exponentiation(getBase(), pow.getNumerator(), pow.getDenominator()).inverse().simplify(eq);
			}
		}
		// Exponentiation
		// x^(a^b^c)
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// x^(e^y)
		// Im
		// ImaginaryExpr
		// (a+bi)^(c+di) = (a^2+b^2)^(c/2) * e^(-d*arg(a+bi)) * cis(c*arg(a+bi) + d/2 ln(a^2+b^2))
		// (a+bi)^(di) = e^(-d*arg(a+bi)) * cis(d/2 ln(a^2+b^2))
		// a^(di) = e^(-d*arg[a]) * cis(d ln a)
		if (getPower() instanceof ImaginaryExpr) {
			Expr a = getBase();
			Expr d = ((ImaginaryExpr)getPower()).getArgument();
			return new DualProduct(new Exp(new DualProduct(d.negative(), new Arg(a))), new Cis(new DualProduct(d, new Log(a)))).simplify(eq);
		}
		// Inverse
		// x^(1/y)
		// Log
		if (getPower() instanceof Log) {
			// e^log(x) = x
			if (getBase().equalTo(new Transcendental(Transcendent.E))) {
				return ((Log)getPower()).getArgument().simplify(eq);
			}
		}
		// Log10
		if (getPower() instanceof Log10) {
			// 10^log10(x) = x
			if (getBase().equalTo(TEN)) {
				return ((Log10)getPower()).getArgument().simplify(eq);
			}
		}
		// Negative
		// x^(-y) = 1/(x^y)
		if (getPower() instanceof Negative) {
			Negative pow = (Negative)getPower();
			return new DualExponentiation(getBase(), pow.getArgument()).inverse().simplify(eq);
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// x^(√y)
		// Tan
		// Complex
		// x^(a+bi) = x^a * x^(bi)
		if (getPower() instanceof Complex) {
			Complex pow = (Complex)getPower();
			return new DualProduct(new DualExponentiation(getBase(), pow.real), new DualExponentiation(getBase(), new Imaginary(pow.imag))).simplify(eq);
		}
		// Exponent
		// x^(a^b)
		// Fraction
		// x^(a/b) = x^a^(1/b)
		if (getPower() instanceof Fraction) {
			Fraction pow = (Fraction)getPower();
			return new Exponentiation(getBase(), pow.numerator, pow.denominator).inverse().simplify(eq);
		}
		// FunctionConstant
		// Imaginary
		// a^(di) = e^(-d*arg[a]) * cis(d ln a)
		if (getPower() instanceof Imaginary) {
			Expr a = getBase();
			Expr d = ((Imaginary)getPower()).value;
			return new DualProduct(new Exp(new DualProduct(d.negative(), new Arg(a))), new Cis(new DualProduct(d, new Log(a)))).simplify(eq);
		}
		// Integer
		if (getPower() instanceof Integer) {
			Integer pow = (Integer)getPower();
			// x^(-y) = 1/x^y
			if (pow.isNegative() == YES) {
				return new DualExponentiation(getBase(), pow.negative()).inverse().simplify(eq);
			}
		}
		// Multiple
		// x^(a*b*c) = x^a^b^c [ = ((x^a)^b)^c ]
		if (getPower() instanceof Multiple) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
			newArgs.add(0, getBase());
			return new Exponentiation(newArgs).simplify(eq);
		}
		// NumNumSum
		// x^(a+b+c) = x^a * x^b * x^c
		if (getPower() instanceof NumberSum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new DualExponentiation(getBase(), newArgs.get(i)));
			}
			return new Product(newArgs).simplify(eq);
		}
		// Transcendental
		// PolynomialTerm
		// z^(ax^n) = z^a^(x^n)
		if (getPower() instanceof PolynomialTerm) {
			PolynomialTerm pow = (PolynomialTerm)getPower();
			return new Exponentiation(getBase(), pow.coefficient, new DualExponentiation(pow.variable, pow.power)).simplify(eq);
		}
		// Variable
		
		// BASE SIMPLIFICATION
		
		// ArgumentContainer
		if (getBase() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot exponentiate an ArgumentContainer.");
		}
		// Product
		// (a*b*c)^x = a^x * b^x * c^x
		if (getBase() instanceof Product) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((Product)getBase()).args);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new DualExponentiation(newArgs.get(i), getPower()));
			}
			return new Product(newArgs).simplify(eq);
		}
		// Sum
		// (a+b+c)^x
		// ArcTan2
		// ComplexExpr
		// (a+bi)^(c+di) = (a^2+b^2)^(c/2) * e^(-d*arg(a+bi)) * cis(c*arg(a+bi) + d/2 ln(a^2+b^2))
		// (a+bi)^c = (a^2+b^2)^(c/2) * cis(c*arg(a+bi))
		if (getBase() instanceof ComplexExpr) {
			ComplexExpr base = (ComplexExpr)getBase();
			Expr a = base.getReal();
			Expr b = base.getImag();
			Expr c = getPower();
			DualExponentiation firstTerm = new DualExponentiation(new DualSum(new DualExponentiation(a, TWO),
					new DualExponentiation(b, TWO)), new Quotient(c, TWO));
			Cis secondTerm = new Cis(new DualProduct(c, new Arg(base)));
			return new DualProduct(firstTerm, secondTerm).simplify(eq);
		}
		// Difference
		// (a-b)^x
		// DualExponentiation
		// (a^b)^x = a^b^x
		if (getBase() instanceof DualExponentiation) {
			return new Exponentiation(((DualExponentiation)getBase()).getBase(),
					((DualExponentiation)getBase()).getPower(),
					getPower()).simplify(eq);
		}
		// DualProduct
		// (a*b)^x = a^x * b^x
		if (getBase() instanceof DualProduct) {
			return new DualProduct(new DualExponentiation(getBase(), ((DualProduct)getBase()).getLeft()),
					new DualExponentiation(getBase(), ((DualProduct)getBase()).getRight())).simplify(eq);
		}
		// DualSum
		// (a+b)^x
		// LogBase
		// Quotient
		// (a/b)^x = (a^x)/(b^x)
		if (getBase() instanceof Quotient) {
			return new Quotient(new DualExponentiation(((Quotient)getBase()).getNumerator(), getPower()),
					new DualExponentiation(((Quotient)getBase()).getDenominator(), getPower())).simplify(eq);
		}
		// Exponentiation
		// (a^b^c)^x = a^b^c^x
		if (getBase() instanceof Exponentiation) {
			ArrayList<Expr> terms = new ArrayList<Expr>(((Exponentiation)getBase()).args);
			terms.add(getPower());
			return new Exponentiation(terms).simplify(eq);
		}
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		if (getBase() instanceof Cis) {
			return new Cis(new DualProduct(((Cis)getBase()).getArgument(), getPower())).simplify(eq);
		}
		// Cos
		// Exp
		// (e^x)^y = e^x^y
		if (getBase() instanceof Exp) {
			Exp base = (Exp)getBase();
			return new Exponentiation(new Transcendental(Transcendent.E), base.getArgument(), getPower()).simplify(eq);
		}
		// Im
		// ImaginaryExpr
		// (a+bi)^(c+di) = (a^2+b^2)^(c/2) * e^(-d*arg(a+bi)) * cis(c*arg(a+bi) + d/2 ln(a^2+b^2))
		// (bi)^(c+di) = b^c * e^(-d*arg(bi)) * cis(c*arg(bi) + d ln b)
		// (bi)^c = b^c * cis(c*arg(bi))
		if (getBase() instanceof ImaginaryExpr) {
			ImaginaryExpr base = (ImaginaryExpr)getBase();
			Expr b = base.getArgument();
			Expr c = getPower();
			return new DualProduct(new DualExponentiation(b, c), new Cis(new DualProduct(c, new Arg(new ImaginaryExpr(b))))).simplify(eq);
		}
		// Inverse
		// (1/a)^b = 1/a^b
		if (getBase() instanceof Inverse) {
			Inverse base = (Inverse)getBase();
			return new DualExponentiation(base.getArgument(), getPower()).inverse().simplify(eq);
		}
		// Log
		// Log10
		// Negative
		// (-a)^x = (-1)^x * a^x
		if (getBase() instanceof Negative) {
			return new DualProduct(new DualExponentiation(NEG_ONE, getPower()),
					new DualExponentiation(((Negative)getBase()).getArgument(), getPower())).simplify(eq);
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// (√x)^y
		if (getBase() instanceof Sqrt) {
			Sqrt base = (Sqrt)getBase();
			return new Exponentiation(base.getArgument(), TWO.inverse(), getPower()).simplify(eq);
		}
		// Tan
		// Complex
		// (a+bi)^(c+di) = (a^2+b^2)^(c/2) * e^(-d*arg(a+bi)) * cis(c*arg(a+bi) + d/2 ln(a^2+b^2))
		// (a+bi)^c = (a^2+b^2)^(c/2) * cis(c*arg(a+bi))
		if (getBase() instanceof Complex) {
			Complex base = (Complex)getBase();
			Expr a = base.real;
			Expr b = base.imag;
			Expr c = getPower();
			DualExponentiation firstTerm = new DualExponentiation(new DualSum(new DualExponentiation(a, TWO),
					new DualExponentiation(b, TWO)), new Quotient(c, TWO));
			Cis secondTerm = new Cis(new DualProduct(c, new Arg(base)));
			return new DualProduct(firstTerm, secondTerm).simplify(eq);
		}
		// Exponent
		// (x^y)^z = x^y^z
		if (getBase() instanceof Exponent) {
			Exponent base = (Exponent)getBase();
			return new Exponentiation(base.base, base.power, getPower());
		}
		// Fraction
		// (a/b)^x = a^x / b^x
		if (getBase() instanceof Fraction) {
			return new Quotient(new DualExponentiation(((Fraction)getBase()).numerator, getPower()),
					new DualExponentiation(((Fraction)getBase()).denominator, getPower())).simplify(eq);
		}
		// FunctionConstant
		// Imaginary
		// (bi)^c = b^c * cis(c*arg(bi))
		if (getBase() instanceof Imaginary) {
			Imaginary base = (Imaginary)getBase();
			Expr b = base.value;
			Expr c = getPower();
			return new DualProduct(new DualExponentiation(b, c), new Cis(new DualProduct(c, new Arg(new ImaginaryExpr(b))))).simplify(eq);
		}
		// Integer
		if (getBase() instanceof Integer) {
			if (getPower() instanceof Integer) {
				// Exponentiation defined, finally:
				if (((Integer)getPower()).value * Math.log10(((Integer)getBase()).value) > Math.log10(Long.MAX_VALUE)) {
					throw new ArithmeticException("Overflow error while computing exponentiation " + this + ".");
				}
				else {
					return new Integer((long)Math.pow(((Integer)getBase()).value, ((Integer)getPower()).value));
				}
			}
		}
		// Multiple
		// (a*b*c)^x = a^x * b^x * c^x
		if (getBase() instanceof Product) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((Product)getBase()).args);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new DualExponentiation(newArgs.get(i), getPower()));
			}
			return new Product(newArgs).simplify(eq);
		}
		// NumNumSum
		// (a+b+c)^x
		// Transcendental
		// PolynomialTerm
		// (c*x^a)^b = c^b * x^a^b
		if (getBase() instanceof PolynomialTerm) {
			PolynomialTerm poly = (PolynomialTerm)getBase();
			return new DualProduct(new DualExponentiation(poly.coefficient, getPower()), new Exponentiation(poly.variable, poly.power, getPower())).simplify(eq);
		}
		// Variable
		
		if (getBase() instanceof Number && getPower() instanceof Number) {
			return new Exponent((NumIntegerTranscendentalFunctionConstant)getBase(), (NumIntegerTranscendentalFunctionConstant)getPower());
		}
		return this;
	}
	public Expr derivative(Variable var) {
		return new DualProduct(new DualExponentiation(getBase(), new Difference(getPower(), ONE)),
				new DualSum(new DualProduct(getPower(), getBase().derivative(var)),
						new Product(getBase(), new Log(getBase()), getPower().derivative(var))));
	}
	public Expr derivativePartial(int var) {
		if (var == 0) return new DualProduct(getPower(), new DualExponentiation(getBase(), new Difference(getPower(), ONE))).simplify(null);
		if (var == 1) return new DualProduct(new DualExponentiation(getBase(), getPower()), new Log(getBase())).simplify(null);
		return ZERO;
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Do not know how to take the antiderivative of an arbitrary exponentiation (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return NO;
	}
	public Compare isPositive() {
		Compare isReal = isReal();
		if (isReal != YES) return isReal;
		Compare basePositive = getBase().isPositive();
		if (basePositive != NO) return basePositive;
		if (getPower() instanceof Integer) {
			Integer power = (Integer)getPower();
			if (power.value % 2 == 0) return YES;
			else return NO;
		}
		return MAYBE;
	}
	public Compare isNegative() {
		Compare isReal = isReal();
		if (isReal != YES) return isReal;
		Compare basePositive = getBase().isPositive();
		if (basePositive == YES) return NO;
		if (basePositive == MAYBE) return MAYBE;
		if (getPower() instanceof Integer) {
			Integer power = (Integer)getPower();
			if (power.value % 2 == 0) return NO;
			else return YES;
		}
		return MAYBE;
	}
	public Compare isReal() {
		return MAYBE;
	}
	public Compare isImag() {
		return MAYBE;
	}
	
	public String toString() {
		String left = getLeft().toString(),
				right = getRight().toString();
		if (getLeft() instanceof DualSum || getLeft() instanceof Sum ||
				getLeft() instanceof DualProduct || getLeft() instanceof Product ||
				getLeft() instanceof Quotient) {
			left = "(" + left + ")";
		}
		if (getRight() instanceof DualSum || getRight() instanceof Sum ||
				getRight() instanceof DualProduct || getRight() instanceof Product ||
				getRight() instanceof Quotient) {
			right = "(" + right + ")";
		}
		return left + " ^ " + right;
	}
	
	public Expr getBase() { return getLeft(); }
	public Expr getPower() { return getRight(); }
	
}
