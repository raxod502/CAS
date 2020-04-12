package cas;

import java.util.ArrayList;
import java.util.HashSet;

public class LogBase extends DoubleArgumentFunction {
	
	public LogBase(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public LogBase(Expr base, Expr power) {
		super(base, power);
	}
	
	public Expr simplifyFunction() {
		// SPECIAL CASES
		
		if (getBase().equalTo(ONE) || getArgument().isZero() == YES) {
			throw new ArithmeticException("LogBase[1, x] and LogBase[x, 0] are undefined.");
		}
		if (getBase().isZero() == YES || getArgument().equalTo(ONE)) {
			return ZERO;
		}
		if (getBase().equalTo(getArgument())) {
			return ONE;
		}
		if (getBase().equalTo(new Transcendental(Transcendent.E))) {
			return new Log(getArgument()).simplify();
		}
		if (getBase().equalTo(TEN)) {
			return new Log10(getArgument()).simplify();
		}
		
		// ARGUMENT SIMPLIFICATIONS
		
		// ArgumentContainer
		if (getArgument() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot take the LogBase of an ArgumentContainer.");
		}
		// Product
		// logbase(b, xyz) = logbase(b, x) + logbase(b, y) + logbase(b, z)
		if (getArgument() instanceof Product) {
			Product arg = (Product)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.args);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new LogBase(getBase(), newArgs.get(i)));
			}
			return new Sum(newArgs).simplify();
		}
		// Sum
		// ArcTan2
		// ComplexExpr
		// logbase(c, a+bi) = log(a+bi)/log(c)
		if (getArgument() instanceof ComplexExpr) {
			return new Quotient(new Log(getArgument()), new Log(getBase())).simplify();
		}
		// Difference
		// DualExponentiation
		// logbase(b, x^y) = y logbase(b, x)
		if (getArgument() instanceof DualExponentiation) {
			DualExponentiation arg = (DualExponentiation)getArgument();
			return new DualProduct(arg.getPower(), new LogBase(getBase(), arg.getBase())).simplify();
		}
		// DualProduct
		// logbase(b, xy) = logbase(b, x) + logbase(b, y)
		if (getArgument() instanceof DualProduct) {
			DualProduct arg = (DualProduct)getArgument();
			return new DualSum(new LogBase(getBase(), arg.getLeft()), new LogBase(getBase(), arg.getRight())).simplify();
		}
		// DualSum
		// LogBase
		// Quotient
		// logbase(b, x/y) = logbase(b, x) - logbase(b, y)
		if (getArgument() instanceof Quotient) {
			Quotient arg = (Quotient)getArgument();
			return new Difference(new LogBase(getBase(), arg.getNumerator()), new LogBase(getBase(), arg.getDenominator())).simplify();
		}
		// Exponentiation
		// logbase(b, x^y^z) = yz logbase(b, x)
		if (getArgument() instanceof Exponentiation) {
			Exponentiation arg = (Exponentiation)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.args);
			newArgs.remove(0);
			newArgs.add(new LogBase(getBase(), arg.args.get(0)));
			return new Product(newArgs).simplify();
		}
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// logbase(b, e^x) = x logbase(b, e)
		if (getArgument() instanceof Exp) {
			Exp arg = (Exp)getArgument();
			return new DualProduct(arg.getArgument(), new LogBase(getBase(), new Transcendental(Transcendent.E))).simplify();
		}
		// Im
		// ImaginaryExpr
		// logbase(b, xi) = log(xi)/log(b)
		if (getArgument() instanceof ImaginaryExpr) {
			return new Quotient(new Log(getArgument()), new Log(getBase())).simplify();
		}
		// Inverse
		// logbase(b, 1/x) = -logbase(b, x)
		if (getArgument() instanceof Inverse) {
			Inverse arg = (Inverse)getArgument();
			return new LogBase(getBase(), arg.getArgument()).negative().simplify();
		}
		// Log
		// Log10
		// Negative
		// logbase(b, -x) = logbase(b, -1) + logbase(b, x)
		if (getArgument() instanceof Negative) {
			Negative arg = (Negative)getArgument();
			return new DualSum(new LogBase(getBase(), NEG_ONE), new LogBase(getBase(), arg.getArgument())).simplify();
		}
		// Re
		// Sin
		// Sgn
		// Sqrt
		// logbase(b, √x) = (1/2)logbase(b, x)
		if (getArgument() instanceof Sqrt) {
			Sqrt arg = (Sqrt)getArgument();
			return new DualProduct(TWO.inverse(), new LogBase(getBase(), arg.getArgument())).simplify();
		}
		// Tan
		// Complex
		// logbase(c, a+bi) = log(a+bi)/log(c)
		if (getArgument() instanceof Complex) {
			return new Quotient(new Log(getArgument()), new Log(getBase())).simplify();
		}
		// Exponent
		// logbase(b, x^y) = y logbase(b, x)
		if (getArgument() instanceof Exponent) {
			Exponent arg = (Exponent)getArgument();
			return new DualProduct(arg.power, new LogBase(getBase(), arg.base)).simplify();
		}
		// Fraction
		// logbase(b, x/y) = logbase(b, x) - logbase(b, y)
		if (getArgument() instanceof Fraction) {
			Fraction arg = (Fraction)getArgument();
			return new Difference(new LogBase(getBase(), arg.numerator), new LogBase(getBase(), arg.denominator)).simplify();
		}
		// FunctionConstant
		// Integer
		// logbase(27/8, 9/4) = 2/3
		if (getArgument() instanceof Integer && getBase() instanceof Integer) {
			Integer base = (Integer)getBase();
			Integer arg = (Integer)getArgument();
			ArrayList<Long> baseFactors = Util.primeFactorList(base.value);
			ArrayList<Long> argFactors = Util.primeFactorList(arg.value);
			// Iterate through factor lists
			// Condition: for both lists, each unique factor must be repeated the same number of times, e.g. [x, x, x, y, y, y, z, z, z, w, w, w]
			// Condition: each list must contain the same factors
			// Result: new Quotient(# times arg factors repeated / # times base factors repeated
			boolean failure = false;
			int count = 0, lastCount = -1;
			long lastNum = baseFactors.get(0);
			readFactorLists: {
				for (@SuppressWarnings("unchecked") ArrayList<Long> currentFactorList : new ArrayList[] {baseFactors, argFactors}) {
					for (long factor : currentFactorList) {
						if (factor == lastNum) {
							count += 1;
						}
						else {
							lastNum = factor;
							if (lastCount != -1 && count != lastCount) {
								failure = true;
								break readFactorLists;
							}
							else {
								lastCount = count;
								count = 1;
							}
						}
					}
					if (count != lastCount) {
						failure = true;
						break readFactorLists;
					}
				}
			}
			int baseFactorsCount = 0, argFactorsCount = 0;
			long firstNum = baseFactors.get(0);
			for (long factor : baseFactors) {
				if (factor != firstNum) break;
				else baseFactorsCount += 1;
			}
			firstNum = argFactors.get(0);
			for (long factor : argFactors) {
				if (factor != firstNum) break;
				else argFactorsCount += 1;
			}
			if (!failure) {
				return new Quotient(new Integer(argFactorsCount), new Integer(baseFactorsCount)).simplify();
			}
		}
		// Could not evaluate directly, so bring down the exponent:
		// logbase(b, 216) = 3*logbase(b, 6)
		if (getArgument() instanceof Integer) {
			Integer arg = (Integer)getArgument();
			long value = arg.value;
			ArrayList<Long> factorList = Util.primeFactorList(arg.value);
			Product result = new Product();
			long num = factorList.get(0);
			int count = 0;
			for (int i=0; i<factorList.size(); i++) {
				if (factorList.get(i) == num) {
					count += 1;
				}
				else {
					if (count >= 2) {
						result.args.add(new Product(new Integer(count), new LogBase(getBase(), new Integer(num))));
						value /= Math.pow(num, count);
					}
					count = 1;
					num = factorList.get(i);
				}
			}
			if (result.args.size() > 0) {
				result.args.add(new LogBase(getBase(), new Integer(value)));
				return result.simplify();
			}
		}
		// Multiple
		// logbase(b, xyz) = logbase(b, x) + logbase(b, y) + logbase(b, z)
		if (getArgument() instanceof Multiple) {
			Multiple arg = (Multiple)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(arg.values);
			for (int i=0; i<newArgs.size(); i++) {
				newArgs.set(i, new LogBase(getBase(), newArgs.get(i)));
			}
			return new Sum(newArgs).simplify();
		}
		// NumNumSum
		// Transcendental
		
		// BASE SIMPLIFICATIONS
		
		// ArgumentContainer
		if (getBase() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot take the LogBase of an ArgumentContainer.");
		}
		// Product
		// Sum
		// ArcTan2
		// ComplexExpr
		// logbase(a+bi, x) = log(x)/log(a+bi)
		if (getBase() instanceof ComplexExpr) {
			return new Quotient(new Log(getArgument()), new Log(getBase())).simplify();
		}
		// Difference
		// DualExponentiation
		// logbase(b^c, x) = 1/c * logbase(b, x)
		if (getBase() instanceof DualExponentiation) {
			DualExponentiation base = (DualExponentiation)getBase();
			return new DualProduct(new Inverse(base.getPower()), new LogBase(base.getBase(), getArgument())).simplify();
		}
		// DualProduct
		// logbase(x/y, c)
		// DualSum
		// LogBase
		// Quotient
		// logbase(x/y, c)
		// Exponentiation
		// logbase(a^b^c, x) = 1/bc * logbase(a, x)
		if (getBase() instanceof Exponentiation) {
			Exponentiation base = (Exponentiation)getBase();
			ArrayList<Expr> newArgs = new ArrayList<Expr>(base.args);
			newArgs.remove(0);
			return new DualProduct(new Product(newArgs).inverse(), new LogBase(base.args.get(0), getArgument())).simplify();
		}
		// Arg
		// ArcCos
		// ArcSin
		// ArcTan
		// Cis
		// Cos
		// Exp
		// logbase(e^x, y) = log(y)/x
		if (getBase() instanceof Exp) {
			Exp base = (Exp)getBase();
			return new Quotient(new Log(getArgument()), base.getArgument()).simplify();
		}
		// Im
		// ImaginaryExpr
		// logbase(xi, y) = log(y)/log(xi)
		if (getBase() instanceof ImaginaryExpr) {
			return new Quotient(new Log(getArgument()), new Log(getBase())).simplify();
		}
		// Inverse
		// logbase(1/b, c) = logbase(b, 1/c)
		if (getBase() instanceof Inverse) {
			Inverse base = (Inverse)getBase();
			return new LogBase(base.getArgument(), new Inverse(getArgument())).simplify();
		}
		// Log
		// Log10
		// Negative
		// Re
		// logbase(-b, c)
		// Sin
		// Sgn
		// Sqrt
		// logbase(√b, c) = 2 logbase(b, c)
		if (getBase() instanceof Sqrt) {
			Sqrt base = (Sqrt)getBase();
			return new DualProduct(TWO, new LogBase(base.getArgument(), getArgument())).simplify();
		}
		// Tan
		// Complex
		// logbase(a+bi, c) = log(c)/log(a+bi)
		if (getBase() instanceof Complex) {
			return new Quotient(new Log(getArgument()), new Log(getBase())).simplify();
		}
		// Exponent
		// logbase(x^y, z) = 1/y * logbase(x, z)
		if (getBase() instanceof Exponent) {
			Exponent base = (Exponent)getBase();
			return new DualProduct(new Inverse(base.power), new LogBase(base.base, getArgument())).simplify();
		}
		// Fraction
		// FunctionConstant
		// logbase(x/y, z)
		// Integer - see ARGUMENT SIMPLIFICATIONS > Integer
		// Multiple
		// NumNumSum
		// Transcendental
		
		if (getBase() instanceof Number && getArgument() instanceof Number) {
			return new FunctionConstant(this);
		}
		// Otherwise, we might want to split it up
		// logbase(b, c) = log(c)/log(b)
		return new Quotient(new Log(getArgument()), new Log(getBase())).simplify();
	}
	public Expr derivative(Variable var) {
		return new Quotient(new Log(getArgument()), new Log(getBase())).derivative(var).simplify(null);
	}
	public Expr derivativePartial(int var) {
		Expr b = getBase(), c = getArgument();
		if (var == 0)
			return new Quotient(new Log(c), new DualProduct(b, new Log(b).square())).negative().simplify(null);
		if (var == 1)
			return new DualProduct(c, new Log(b)).inverse().simplify(null);
		return ZERO;
	}
	public Expr antiderivative(Variable var) {
		throw new UnsupportedOperationException("Cannot find the antiderivative of an arbitrary logbase (yet).");
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		Compare baseOne = getBase()._equalTo(ONE);
		Compare argOne = getArgument()._equalTo(ONE);
		if (baseOne != NO) return baseOne;
		return argOne;
	}
	public Compare isPositive() {
		Compare isReal = isReal();
		if (isReal != YES) return isReal;
		Expr b = getBase(), c = getArgument();
		Compare cond1 = b.greaterThan(ONE),
				cond2 = c.greaterThan(ONE),
				cond3 = b.isPositive(),
				cond4 = c.isPositive(),
				cond5 = b.lessThan(ONE),
				cond6 = c.lessThan(ONE);
		if (cond1 == YES && cond2 == YES ||
				cond3 == YES && cond4 == YES && cond5 == YES && cond6 == YES)
			return YES;
		if ((cond1 == NO || cond2 == NO) &&
				(cond3 == NO || cond4 == NO || cond5 == NO || cond6 == NO))
			return NO;
		return MAYBE;
	}
	public Compare isNegative() {
		Compare isReal = isReal();
		if (isReal != YES) return isReal;
		Compare cond1 = isZero(),
				cond2 = isPositive();
		if (cond1 == YES || cond2 == YES) return NO;
		if (cond1 == NO && cond2 == NO) return YES;
		return MAYBE;
	}
	public Compare isReal() {
		return MAYBE;
	}
	public Compare isImag() {
		return MAYBE;
	}
	
	public Expr getBase() { return args.get(0); }
	public Expr getArgument() { return args.get(1); }
	
}
