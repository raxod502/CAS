package cas;

import java.util.ArrayList;
import java.util.HashSet;

public class DualSum extends DualFunction {
	
	public DualSum(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualSum(Expr left, Expr right) {
		super(left, right);
	}
	
	public Expr simplifyDualFunction(HashSet<Expr> eq) {
		// SPECIAL CASES
		
		// 0 + x = x
		if (getLeft().isZero() == YES) {
			return getRight();
		}
		// (-x) + x = 0
		if (getLeft().negative().simplify(null).equalTo(getRight())) {
			return ZERO;
		}
		// x + x = 2*x
		if (getLeft().equalTo(getRight())) {
			return new DualProduct(TWO, getLeft()).simplify(eq);
		}
		
		// ArgumentContainer
		if (getLeft() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot add an ArgumentContainer.");
		}
		// Product
		// [ Combine like terms ]
		// ac + bc = c(a + b)
		if ((getLeft() instanceof Product || getLeft() instanceof DualProduct) &&
				(getRight() instanceof Product || getRight() instanceof DualProduct)) {
			ArrayList<Expr> largs = ((Function)getLeft()).args, rargs = ((Function)getRight()).args;
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
			factoredTerms.add(new DualSum(left, right));
			if (factoredTerms.size() < 2) throw new IllegalStateException();
			else if (factoredTerms.size() == 2) return new DualProduct(factoredTerms.get(0), factoredTerms.get(1));
			else return new Product(factoredTerms);
		}
		
		// Sum
		// (a+b+c)+d = a+b+c+d
		if (getLeft() instanceof Sum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((Sum)getLeft()).args);
			newArgs.add(getRight());
			return new Sum(newArgs).simplify(eq);
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
			return new Sum(left.getLeft(), left.getRight().negative(), getRight()).simplify(eq);
		}
		// DualExponentiation
		// DualProduct - see Product
		// DualSum
		// (a+b)+c = a+b+c
		if (getLeft() instanceof DualSum) {
			DualSum left = (DualSum)getLeft();
			return new Sum(left.getLeft(), left.getRight(), getRight()).simplify(eq);
		}
		// LogBase
		// logbase(b, x) + logbase(b, y) = logbase(b, xy)
		if (getLeft() instanceof LogBase && getRight() instanceof LogBase &&
				((LogBase)getLeft()).getBase().equalTo(((LogBase)getRight()).getBase())) {
			LogBase left = (LogBase)getLeft(), right = (LogBase)getRight();
			return new LogBase(left.getBase(), new DualProduct(left.getArgument(), right.getArgument())).simplify(eq);
		}
		// Quotient
		// a/c + b/c = (a+b)/c
		// a/b + c/d = (ad+bc)/(bd)
		if (getLeft() instanceof Quotient && getRight() instanceof Quotient) {
			Quotient left = (Quotient)getLeft(), right = (Quotient)getRight();
			if (left.getDenominator().equalTo(right.getDenominator())) {
				return new Quotient(new DualSum(left.getNumerator(), right.getNumerator()), left.getDenominator()).simplify(eq);
			}
			else {
				Expr a = left.getNumerator(), b = left.getDenominator(), c = right.getNumerator(), d = right.getDenominator();
				return new Quotient(new DualSum(new DualProduct(a, d), new DualProduct(b, c)), new DualProduct(b, d)).simplify(eq);
			}
		}
		// a/b + c = (a + bc)/b
		if (getLeft() instanceof Quotient) {
			Quotient left = (Quotient)getLeft();
			return new Quotient(new DualSum(left.getNumerator(),
					new DualProduct(left.getDenominator(), getRight())), left.getDenominator()).simplify(eq);
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
			return new ComplexExpr(getRight(), ((ImaginaryExpr)getLeft()).getArgument()).simplify(eq);
		}
		// Inverse
		// 1/b + c = (1+bc)/b
		if (getLeft() instanceof Inverse) {
			Inverse left = (Inverse)getLeft();
			return new Quotient(new DualSum(ONE,
					new DualProduct(left.getArgument(), getRight())), left.getArgument()).simplify(eq);
		}
		// Log
		if (getLeft() instanceof Log && getRight() instanceof Log) {
			Log left = (Log)getLeft(), right = (Log)getRight();
			return new Log(new DualProduct(left.getArgument(), right.getArgument())).simplify(eq);
		}
		// Log10
		if (getLeft() instanceof Log10 && getRight() instanceof Log10) {
			Log10 left = (Log10)getLeft(), right = (Log10)getRight();
			return new Log10(new DualProduct(left.getArgument(), right.getArgument())).simplify(eq);
		}
		// Negative
		if (getLeft() instanceof Negative) {
			return new Difference(getRight(), ((Negative)getLeft()).getArgument()).simplify(eq);
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
			return new ComplexExpr(new DualSum(left.real, getRight()), left.imag).simplify(eq);
		}
		// Exponent
		// Fraction
		// a/b + c = (a+bc)/b
		if (getLeft() instanceof Fraction) {
			Fraction left = (Fraction)getLeft();
			return new Quotient(new DualSum(left.numerator,
					new DualProduct(left.denominator, getRight())), left.denominator).simplify(eq);
		}
		// FunctionConstant
		// Imaginary
		// bi + c = c + bi
		if (getLeft() instanceof Imaginary) {
			return new ComplexExpr(getRight(), ((Imaginary)getLeft()).value).simplify(eq);
		}
		// Integer
		if (getLeft() instanceof Integer && getRight() instanceof Integer) {
			Integer left = (Integer)getLeft(), right = (Integer)getRight();
			if ((long)left.value + (long)right.value > (long)Long.MAX_VALUE) {
				throw new ArithmeticException("Overflow error while computing sum " + this + ".");
			}
			else {
				return new Integer(left.value + right.value);
			}
		}
		// Multiple
		// [ Combine like terms ]
		// ac + bc = c(a + b)
		if (getLeft() instanceof NumberSum && getRight() instanceof NumberSum) {
			Product left = new Product(new ArrayList<Expr>(((NumberSum)getLeft()).values));
			Product right = new Product(new ArrayList<Expr>(((NumberSum)getRight()).values));
			ArrayList<Expr> factoredTerms = new ArrayList<Expr>();
			int i = 0;
			while (i < left.args.size()) {
				Expr arg = left.args.get(i);
				int j = 0;
				while (j < right.args.size()) {
					if (right.args.get(i).equalTo(arg)) {
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
			factoredTerms.add(new DualSum(left, right));
			return new Product(factoredTerms);
		}
		
		// NumNumSum
		// (a+b+c)+d = a+b+c+d
		if (getLeft() instanceof NumberSum) {
			ArrayList<Expr> newArgs = new ArrayList<Expr>(((NumberSum)getLeft()).values);
			newArgs.add(getRight());
			return new Sum(newArgs).simplify(eq);
		}
		// Transcendental
		// PolynomialTerm
		if (getLeft() instanceof PolynomialTerm && getRight() instanceof PolynomialTerm) {
			PolynomialTerm left = (PolynomialTerm)getLeft(), right = (PolynomialTerm)getRight();
			if (left.variable.equalTo(right.variable) && left.power.equalTo(right.power)) {
				return new PolynomialTerm((NumIntegerTranscendentalFunctionConstant)new DualSum(left.coefficient, right.coefficient).simplify(null), left.variable, left.power);
			}
		}
		// Variable
		
		if (getLeft() instanceof Number && getRight() instanceof Number) {
			return new NumberSum((NumMultiple)getLeft(), (NumMultiple)getRight()).reduce(eq);
		}
		return this;
	}
	public Expr derivative(Variable var) {
		return new DualSum(getLeft().derivative(var), getRight().derivative(var));
	}
	public Expr derivativePartial(int var) {
		if (var >= 2) return ZERO;
		return ONE;
	}
	public Expr antiderivative(Variable var) {
		return new DualSum(getLeft().antiderivative(var), getRight().antiderivative(var));
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
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
	public Compare isPositive() {
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
	public Compare isNegative() {
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
	public Compare isReal() {
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
	public Compare isImag() {
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
	
	public String toString() {
		String left = getLeft().toString(),
				right = getRight().toString();
		return left + " + " + right;
	}
	
}
