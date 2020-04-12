package cas;

import java.util.*;

public class Negative extends SingleArgumentFunction {
	
	public Negative(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Negative(Expr argument) {
		super(argument);
	}
	
	public Expr simplifyFunction() {
		if (getArgument() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot negate an ArgumentContainer!");
		}
		if (getArgument() instanceof Sum) {
			Sum sum = (Sum)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr arg : sum.args) {
				newArgs.add(arg.negative());
			}
			return new Sum(newArgs).simplify();
		}
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr ce = (ComplexExpr)getArgument();
			return new ComplexExpr(ce.getReal().negative(), ce.getImag().negative()).simplify();
		}
		if (getArgument() instanceof Difference) {
			Difference diff = (Difference)getArgument();
			return new Difference(diff.getRight(), diff.getLeft()).simplify();
		}
		if (getArgument() instanceof DualSum) {
			DualSum ds = (DualSum)getArgument();
			return new DualSum(ds.getLeft().negative(), ds.getRight().negative()).simplify();
		}
		if (getArgument() instanceof LogBase) {
			LogBase log = (LogBase)getArgument();
			return new LogBase(log.getBase(), log.getArgument().inverse());
		}
		if (getArgument() instanceof Cis) {
			return new Cis(new DualSum(((Cis)getArgument()).getArgument(), pi)).simplify();
		}
		if (getArgument() instanceof Im) {
			return new Im(((Im)getArgument()).getArgument().negative()).simplify();
		}
		if (getArgument() instanceof ImaginaryExpr) {
			return new ImaginaryExpr(((ImaginaryExpr)getArgument()).getArgument().negative()).simplify();
		}
		if (getArgument() instanceof Inverse) {
			return new Inverse(((Inverse)getArgument()).getArgument().negative()).simplify();
		}
		if (getArgument() instanceof Log) {
			return new Log(((Log)getArgument()).getArgument().inverse()).simplify();
		}
		if (getArgument() instanceof Log10) {
			return new Log10(((Log10)getArgument()).getArgument().inverse()).simplify();
		}
		if (getArgument() instanceof Negative) {
			return ((Negative)getArgument()).getArgument();
		}
		if (getArgument() instanceof Re) {
			return new Re(((Re)getArgument()).getArgument().negative()).simplify();
		}
		if (getArgument() instanceof Complex) {
			Complex c = (Complex)getArgument();
			return new Complex((NumNumSum)c.real.negative().simplify(), (NumNumSum)c.imag.negative().simplify());
		}
		if (getArgument() instanceof Exponent) {
			Exponent exp = (Exponent)getArgument();
			return new Exponent(exp.base, exp.power, !exp.negative);
		}
		if (getArgument() instanceof Fraction) {
			Fraction frac = (Fraction)getArgument();
			return new Fraction((NumExponent)frac.numerator.negative().simplify(), frac.denominator);
		}
		if (getArgument() instanceof Imaginary) {
			return new Imaginary((NumNumSum)((Imaginary)getArgument()).value.negative().simplify());
		}
		if (getArgument() instanceof Integer) {
			Integer n = (Integer)getArgument();
			if (n.value == Long.MIN_VALUE) {
				throw new ArithmeticException("Overflow while negating integer <" + n + "> (" + n.value + ")");
			}
			return new Integer(-n.value);
		}
		if (getArgument() instanceof Transcendental) {
			return new Transcendental(((Transcendental)getArgument()).value, !((Transcendental)getArgument()).negative);
		}
		if (getArgument() instanceof PolynomialTerm) {
			PolynomialTerm poly = (PolynomialTerm)getArgument();
			return new PolynomialTerm((NumIntegerTranscendentalFunctionConstant)poly.coefficient.negative().simplify(), poly.variable, poly.power).simplify();
		}
		
		return this;
	}
	public Expr derivativePartial(int var) {
		if (var == 0) return NEG_ONE;
		else return ZERO;
	}
	public Expr antiderivative(Variable var) {
		return new Negative(getArgument().antiderivative(var)).simplify();
	}
	
	public Compare compare(Expr other) {
		Compare compare = getArgument().compare(other);
		switch (compare) {
		case EQUAL: return EQUAL;
		case GREATER: return LESSER;
		case LESSER: return GREATER;
		case UNEQUAL: return UNEQUAL;
		case GREATER_OR_EQUAL: return LESSER_OR_EQUAL;
		case LESSER_OR_EQUAL: return GREATER_OR_EQUAL;
		case UNKNOWN: return UNKNOWN;
		case INDETERMINATE: return INDETERMINATE;
		default: throw new IllegalStateException("Received an invalid Compare state '" + compare + "'.");
		}
	}
	public Compare isZero() {
		return getArgument().isZero();
	}
	public Compare isPositive() {
		return getArgument().isNegative();
	}
	public Compare isNegative() {
		return getArgument().isPositive();
	}
	public Compare isReal() {
		return getArgument().isReal();
	}
	public Compare isImag() {
		return getArgument().isImag();
	}
	
}
