package cas;

import java.util.*;

public class Quotient extends DoubleArgumentFunction {
	
	public Quotient(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public Quotient(Expr numerator, Expr denominator) {
		super(numerator, denominator);
	}
	
	public Expr simplifyFunction() {
		// SPECIAL CASES
		
		// x/1 = x
		if (getDenominator().equalTo(ONE)) {
			return getNumerator();
		}
		// x/0 is undefined
		if (getDenominator().isZero() == YES) {
			throw new IllegalStateException("The denominator of a Quotient cannot be zero.");
		}
		// 1/x
		if (getNumerator().equalTo(ONE)) {
			return new Inverse(getDenominator()).simplify();
		}
		// 0/x = 0
		if (getNumerator().isZero() == YES) {
			return ZERO;
		}
		
		// CANCELLING
		
		// Numerator and denominator both are single
		if (getNumerator().equalTo(getDenominator())) {
			return ONE;
		}
		if (getNumerator().negative().equalTo(getDenominator())) {
			return NEG_ONE;
		}
		// Numerator only is single
		if (getDenominator() instanceof Product || getDenominator() instanceof DualProduct ||
				getDenominator() instanceof Multiple) {
			ArrayList<? extends Expr> originalArgs = getDenominator() instanceof Multiple ?
					((Function)getDenominator()).args : ((Multiple)getDenominator()).values;
			for (Expr arg : originalArgs) {
				if (arg.equalTo(getNumerator())) {
					ArrayList<Expr> newArgs = new ArrayList<Expr>();
					newArgs.remove(arg);
					if (newArgs.size() == 0) {
						throw new IllegalStateException("There was only one argument in the denominator's product.\nThis should have triggered a previous check.");
					}
					else if (newArgs.size() == 1) {
						return new Inverse(newArgs.get(0)).simplify();
					}
					else if (newArgs.size() == 2) {
						return new Inverse(new DualProduct(newArgs.get(0), newArgs.get(1))).simplify();
					}
					else {
						return new Inverse(new Product(newArgs)).simplify();
					}
				}
			}
		}
		// Denominator only is single
		if (getNumerator() instanceof Product || getNumerator() instanceof DualProduct ||
				getNumerator() instanceof Multiple) {
			ArrayList<? extends Expr> originalArgs = getNumerator() instanceof Multiple ?
					((Function)getNumerator()).args : ((Multiple)getNumerator()).values;
			for (Expr arg : originalArgs){
				if (arg.equalTo(getDenominator())) {
					ArrayList<Expr> newArgs = new ArrayList<Expr>();
					newArgs.remove(arg);
					if (newArgs.size() == 0) {
						throw new IllegalStateException("There was only one argument in the numerator's product.\nThis should have triggered a previous check.");
					}
					else if (newArgs.size() == 1) {
						return newArgs.get(0).simplify();
					}
					else if (newArgs.size() == 2) {
						return new DualProduct(newArgs.get(0), newArgs.get(1)).simplify();
					}
					else {
						return new Product(newArgs).simplify();
					}
				}
			}
		}
		// Numerator nor denominator neither are single
		if ((getNumerator() instanceof Product || getNumerator() instanceof DualProduct ||
				getNumerator() instanceof Multiple) &&
				(getDenominator() instanceof Product || getDenominator() instanceof DualProduct ||
						getDenominator() instanceof Multiple)) {
			ArrayList<? extends Expr> numDotArgs = getNumerator() instanceof Multiple ?
					((Function)getNumerator()).args : ((Multiple)getNumerator()).values;
			ArrayList<? extends Expr> denDotArgs = getNumerator() instanceof Multiple ?
					((Function)getNumerator()).args : ((Multiple)getNumerator()).values;
			ArrayList<Expr> numArgs = new ArrayList<Expr>(numDotArgs);
			ArrayList<Expr> denArgs = new ArrayList<Expr>(denDotArgs);
			for (Expr argn : numDotArgs) {
				for (Expr argd : denDotArgs) {
					if (argn.equalTo(argd)) {
						numArgs.remove(argn); denArgs.remove(argd);
					}
				}
			}
			if (numDotArgs.size() != numArgs.size() && denDotArgs.size() != denArgs.size()) {
				Expr newNum, newDen;
				if (numArgs.size() == 0) {
					throw new IllegalStateException("There was only one argument in the numerator's product.\nThis should have triggered a previous check.");
				}
				else if (numArgs.size() == 1) {
					newNum = numArgs.get(0);
				}
				else if (numArgs.size() == 2) {
					newNum = new DualProduct(numArgs.get(0), numArgs.get(1));
				}
				else {
					newNum = new Product(numArgs);
				}
				if (denArgs.size() == 0) {
					throw new IllegalStateException("There was only one argument in the denominator's product.\nThis should have triggered a previous check.");
				}
				else if (denArgs.size() == 1) {
					newDen = numArgs.get(0);
				}
				else if (denArgs.size() == 2) {
					newDen = new DualProduct(denArgs.get(0), denArgs.get(1));
				}
				else {
					newDen = new Product(denArgs);
				}
				return new Quotient(newNum, newDen).simplify();
			}
		}
		
		// OTHER SIMPLIFICATIONS
		
		// ArgumentContainer
		if (getNumerator() instanceof ArgumentContainer || getDenominator() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot divide an ArgumentContainer.");
		}
		// Product - see above (cancelling)
		// Sum - see below (splitting fraction)
		// ArcTan2
		// ComplexExpr
		// x/(a+bi) = [x*(a-bi)]/[a^2+b^2]
		if (getDenominator() instanceof ComplexExpr) {
			ComplexExpr den = (ComplexExpr)getDenominator();
			return new Quotient(new DualProduct(getNumerator(), den.conjugate()), new DualSum(
					new DualExponentiation(den.getReal(), TWO), new DualExponentiation(den.getImag(), TWO))).simplify();
		}
		// (a+bi)/x = [a/x] + [b/x]i
		if (getNumerator() instanceof ComplexExpr) {
			ComplexExpr num = (ComplexExpr)getNumerator();
			return new ComplexExpr(new Quotient(num.getReal(), getDenominator()), new Quotient(num.getImag(), getDenominator())).simplify();
		}
		// Difference
		// DualExponentiation
		// x^a/x^b = x^(a-b)
		if (getNumerator() instanceof DualExponentiation && getDenominator() instanceof DualExponentiation) {
			DualExponentiation num = (DualExponentiation)getNumerator(), den = (DualExponentiation)getDenominator();
			if (num.getBase().equalTo(den.getBase())) {
				return new DualExponentiation(num.getBase(), new Difference(num.getPower(), den.getPower())).simplify();
			}
		}
		// DualProduct - see above (cancelling)
		// DualSum - see below (splitting fraction)
		// LogBase
		// logbase(x, c)/logbase(x, b) = logbase(b, c)
		if (getNumerator() instanceof LogBase && getDenominator() instanceof LogBase) {
			LogBase num = (LogBase)getNumerator(), den = (LogBase)getDenominator();
			if (num.getBase().equalTo(den.getBase())) {
				return new LogBase(den.getBase(), num.getBase()).simplify();
			}
		}
		// Quotient
		// (a/b)/x = a/(b*x)
		if (getNumerator() instanceof Quotient) {
			Quotient num = (Quotient)getNumerator();
			return new Quotient(num.getNumerator(), new DualProduct(num.getDenominator(), getDenominator())).simplify();
		}
		// x/(a/b) = (x*b)/a
		if (getDenominator() instanceof Quotient) {
			Quotient den = (Quotient)getDenominator();
			return new Quotient(new DualProduct(getNumerator(), den.getDenominator()), den.getNumerator()).simplify();
		}
		// Exponentiation
		// x^a^b/x^a^c = x^a^(b-c)
		// A DualExponentiation could not have a power equal to an Exponentiation; if so, the
		// Exponentiation's power would be able to immediately reduce to a single power.
		if (getNumerator() instanceof Exponentiation && getDenominator() instanceof Exponentiation) {
			Exponentiation num = (Exponentiation)getNumerator(), den = (Exponentiation)getDenominator();
			ArrayList<Expr> powers = new ArrayList<Expr>(num.args);
			ArrayList<Expr> powersCheck = den.args;
			boolean success = true;
			if (powers.size() != powersCheck.size()) success = false;
			for (int i=0; i<powers.size()-1; i++) {
				if (!powers.get(i).equalTo(powersCheck.get(i))) {
					success = false;
					break;
				}
			}
			if (success) {
				Expr numPower = powers.get(powers.size()-1), denPower = powersCheck.get(powers.size()-1);
				powers.remove(powers.size()-1);
				powers.add(new Difference(numPower, denPower));
				return new Exponentiation(powers).simplify();
			}
		}
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// [cosx + isinx]/[cosy + isiny] = cos(x-y) + isin(x-y)
		if (getNumerator() instanceof Cis && getDenominator() instanceof Cis) {
			Cis num = (Cis)getNumerator(), den = (Cis)getDenominator();
			return new Cis(new Difference(num.getArgument(), den.getArgument())).simplify();
		}
		// Cos - see Tan
		// Exp
		// (e^y)/(e^x) = e^(y-x)
		if (getNumerator() instanceof Exp && getDenominator() instanceof Exp) {
			Exp num = (Exp)getNumerator(), den = (Exp)getDenominator();
			return new Exp(new Difference(num.getArgument(), den.getArgument())).simplify();
		}
		// Im
		// ImaginaryExpr
		// a/(bi) = (-ai)/b
		if (getNumerator() instanceof ImaginaryExpr) {
			ImaginaryExpr num = (ImaginaryExpr)getNumerator();
			return new ImaginaryExpr(new Quotient(num.getArgument(), getDenominator())).simplify();
		}
		if (getDenominator() instanceof ImaginaryExpr) {
			ImaginaryExpr den = (ImaginaryExpr)getDenominator();
			return new Quotient(new Negative(new ImaginaryExpr(getNumerator())), den.getArgument()).simplify();
		}
		// Inverse
		// (1/a)/b = 1/(ab)
		if (getNumerator() instanceof Inverse) {
			Inverse num = (Inverse)getNumerator();
			return new Inverse(new DualProduct(num.getArgument(), getDenominator())).simplify();
		}
		// a/(1/b) = a/b
		if (getDenominator() instanceof Inverse) {
			Inverse den = (Inverse)getDenominator();
			return new Quotient(getNumerator(), den.getArgument()).simplify();
		}
		// Log
		// log(c)/log(b) = logbase(b, c)
		if (getNumerator() instanceof Log && getDenominator() instanceof Log) {
			Log num = (Log)getNumerator(), den = (Log)getDenominator();
			return new LogBase(den.getArgument(), num.getArgument()).simplify();
		}
		// Log10
		// log10(c)/log10(b) = logbase(b, c)
		if (getNumerator() instanceof Log10 && getDenominator() instanceof Log10) {
			Log10 num = (Log10)getNumerator(), den = (Log10)getDenominator();
			return new LogBase(den.getArgument(), num.getArgument()).simplify();
		}
		// Negative
		if (getNumerator() instanceof Negative) {
			return new Negative(new Quotient(((Negative)getNumerator()).getArgument(), getDenominator())).simplify();
		}
		if (getDenominator() instanceof Negative) {
			return new Negative(new Quotient(getNumerator(), ((Negative)getDenominator()).getArgument())).simplify();
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// √x/√y = √(x/y)
		if (getNumerator() instanceof Sqrt && getDenominator() instanceof Sqrt) {
			Sqrt num = (Sqrt)getNumerator(), den = (Sqrt)getDenominator();
			return new Sqrt(new Quotient(num.getArgument(), den.getArgument()));
		}
		// Tan
		// Complex
		// x/(a+bi) = [x*(a-bi)]/[a^2+b^2]
		if (getDenominator() instanceof Complex) {
			Complex den = (Complex)getDenominator();
			return new Quotient(new DualProduct(getNumerator(), den.conjugate()), new DualSum(
					new DualExponentiation(den.real, TWO), new DualExponentiation(den.imag, TWO))).simplify();
		}
		// Exponent
		// x^a/x^b = x^(a-b)
		if (getNumerator() instanceof Exponent && getDenominator() instanceof Exponent) {
			Exponent num = (Exponent)getNumerator(), den = (Exponent)getDenominator();
			if (num.base.equalTo(den.base)) {
				return new Exponent(num.base, (NumFraction)new Difference(num.power, den.power).simplify()).simplify();
			}
		}
		// Fraction
		// (a/b)/x = a/(b*x)
		if (getNumerator() instanceof Fraction) {
			Fraction num = (Fraction)getNumerator();
			return new Fraction(num.numerator, (NumExponent)new DualProduct(num.denominator, getDenominator()).simplify()).simplify();
		}
		// x/(a/b) = (x*b)/a
		if (getDenominator() instanceof Fraction) {
			Fraction den = (Fraction)getDenominator();
			return new Fraction((NumExponent)new DualProduct(getNumerator(), den.denominator).simplify(), den.numerator).simplify();
		}
		// FunctionConstant
		// Imaginary
		if (getNumerator() instanceof Imaginary) {
			Imaginary num = (Imaginary)getNumerator();
			return new ImaginaryExpr(new Quotient(num.value, getDenominator())).simplify();
		}
		if (getDenominator() instanceof Imaginary) {
			Imaginary den = (Imaginary)getDenominator();
			return new Quotient(new Negative(new ImaginaryExpr(getNumerator())), den.value).simplify();
		}
		// Integer
		if (getNumerator() instanceof Integer && getDenominator() instanceof Integer) {
			Integer num = (Integer)getNumerator(), den = (Integer)getDenominator();
			ArrayList<Long> numFactors = Util.primeFactorList(num.value);
			ArrayList<Long> denFactors = Util.primeFactorList(den.value);
			boolean reduced = false;
			int n=0, d=0;
			while (n < numFactors.size() && d < denFactors.size()) {
				if (numFactors.get(n) > denFactors.get(d)) {
					d += 1;
					continue;
				}
				if (denFactors.get(d) > numFactors.get(n)) {
					n += 1;
					continue;
				}
				numFactors.remove(0);
				denFactors.remove(0);
				d += 1;
				n += 1;
				reduced = true;
			}
			if (reduced) {
				int numValue = 1, denValue = 1;
				for (long factor : numFactors) numValue *= factor;
				for (long factor : denFactors) denValue *= factor;
				return new Quotient(new Integer(numValue), new Integer(denValue)).simplify();
			}
			else {
				return new Fraction(num, den);
			}
		}
		// Multiple - see above (cancelling)
		// NumNumSum - see below (splitting fraction)
		// Transcendental
		// Cannot simplify
		if (getNumerator() instanceof Number && getDenominator() instanceof Number) {
			return new Fraction((NumExponent)getNumerator(), (NumExponent)getDenominator());
		}
		
		// SPLITTING FRACTION
		
		// (a+b)/c = a/c + b/c
		if (getNumerator() instanceof DualSum) {
			DualSum num = (DualSum)getNumerator();
			return new DualSum(new Quotient(num.getLeft(), getDenominator()), new Quotient(num.getRight(), getDenominator())).simplify();
		}
		if (getNumerator() instanceof Sum) {
			Sum num = (Sum)getNumerator();
			ArrayList<Expr> fractions = new ArrayList<Expr>();
			for (Expr arg : num.args) {
				fractions.add(new Quotient(arg, getDenominator()));
			}
			return new Sum(fractions).simplify();
		}
		if (getNumerator() instanceof NumberSum) {
			NumberSum num = (NumberSum)getNumerator();
			ArrayList<Expr> fractions = new ArrayList<Expr>();
			for (Expr arg : num.values) {
				fractions.add(new Quotient(arg, getDenominator()));
			}
			return new Sum(fractions).simplify();
		}
		
		if (getNumerator() instanceof Number && getDenominator() instanceof Number) {
			return new Fraction((NumIntegerTranscendentalFunctionConstant)getNumerator(),
					(NumIntegerTranscendentalFunctionConstant)getDenominator()).reduce();
		}
		return this;
	}
	public Expr derivative(Variable var) {
		return new Quotient(new Difference(new DualProduct(getNumerator().derivative(var), getDenominator()),
				new DualProduct(getNumerator(), getDenominator().derivative(var))), new DualExponentiation(getDenominator(), TWO));
	}
	public Expr derivativePartial(int var) {
		if (var == 0) return getDenominator().inverse().simplify();
		if (var == 1) return new Quotient(getNumerator(), getDenominator().square()).negative().simplify();
		return ZERO;
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Can't find the antiderivative of an arbitrary quotient (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		if (getNumerator().isZero() == YES) return NO;
		return getDenominator().isZero();
	}
	public Compare isPositive() {
		if (getNumerator().isZero() == YES || getDenominator().isZero() == YES) return NO;
		Compare numPos = getNumerator().isPositive(), denPos = getDenominator().isPositive();
		if (numPos == YES && denPos == YES || numPos == NO && denPos == NO) return YES;
		if (numPos == YES && denPos == NO || numPos == NO && denPos == YES) return NO;
		return MAYBE;
	}
	public Compare isNegative() {
		if (getNumerator().isZero() == YES || getDenominator().isZero() == YES) return NO;
		Compare numPos = getNumerator().isPositive(), denPos = getDenominator().isPositive();
		if (numPos == YES && denPos == YES || numPos == NO && denPos == NO) return NO;
		if (numPos == YES && denPos == NO || numPos == NO && denPos == YES) return YES;
		return MAYBE;
	}
	public Compare isReal() {
		Compare numReal = getNumerator().isReal(), denReal = getDenominator().isReal(),
				numImag = getNumerator().isImag(), denImag = getDenominator().isImag();
		if (numReal == YES && denReal == YES || numImag == YES && denImag == YES) return YES; // Real
		if (numReal == YES && denImag == YES || numImag == YES && denReal == YES) return NO; // Imag
		if (numReal == NO && numImag == NO || denReal == NO && denImag == NO) return NO; // Complex
		return MAYBE;
	}
	public Compare isImag() {
		Compare numReal = getNumerator().isReal(), denReal = getDenominator().isReal(),
				numImag = getNumerator().isImag(), denImag = getDenominator().isImag();
		if (numReal == YES && denReal == YES || numImag == YES && denImag == YES) return NO; // Real
		if (numReal == YES && denImag == YES || numImag == YES && denReal == YES) return YES; // Imag
		if (numReal == NO && numImag == NO || denReal == NO && denImag == NO) return NO; // Complex
		return MAYBE;
	}
	
	public Expr getNumerator() { return args.get(0); }
	public Expr getDenominator() { return args.get(1); }
	
	public String toString() {
		String left = getLeft().toString(),
				right = getRight().toString();
		if (getLeft() instanceof DualSum || getLeft() instanceof Sum) {
			left = "(" + left + ")";
		}
		if (getRight() instanceof DualSum || getRight() instanceof Sum) {
			right = "(" + right + ")";
		}
		return left + " / " + right;
	}
	
}
