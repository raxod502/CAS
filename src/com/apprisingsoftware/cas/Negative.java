package com.apprisingsoftware.cas;

import java.util.ArrayList;

class Negative extends SingleArgumentFunction {
	
	public Negative(Expr arg, int derivative) {
		super(arg, derivative);
	}
	public Negative(Expr argument) {
		super(argument);
	}
	
	@Override public Expr getAlternate() {
		if (getArgument() instanceof ArgumentContainer) {
			throw new UnsupportedOperationException("Cannot negate an ArgumentContainer!");
		}
		if (getArgument() instanceof Sum) {
			Sum sum = (Sum)getArgument();
			ArrayList<Expr> newArgs = new ArrayList<Expr>();
			for (Expr arg : sum.args) {
				newArgs.add(arg.negative());
			}
			return new Sum(newArgs);
		}
		if (getArgument() instanceof ComplexExpr) {
			ComplexExpr ce = (ComplexExpr)getArgument();
			return new ComplexExpr(ce.getReal().negative(), ce.getImag().negative());
		}
		if (getArgument() instanceof Difference) {
			Difference diff = (Difference)getArgument();
			return new Difference(diff.getRight(), diff.getLeft());
		}
		if (getArgument() instanceof DualSum) {
			DualSum ds = (DualSum)getArgument();
			return new DualSum(ds.getLeft().negative(), ds.getRight().negative());
		}
		if (getArgument() instanceof LogBase) {
			LogBase log = (LogBase)getArgument();
			return new LogBase(log.getBase(), log.getArgument().inverse());
		}
		if (getArgument() instanceof Cis) {
			return new Cis(new DualSum(((Cis)getArgument()).getArgument(), pi));
		}
		if (getArgument() instanceof Im) {
			return new Im(((Im)getArgument()).getArgument().negative());
		}
		if (getArgument() instanceof ImaginaryExpr) {
			return new ImaginaryExpr(((ImaginaryExpr)getArgument()).getArgument().negative());
		}
		if (getArgument() instanceof Inverse) {
			return ((Inverse)getArgument()).getArgument().negative().inverse();
		}
		if (getArgument() instanceof Log) {
			return new Log(((Log)getArgument()).getArgument().inverse());
		}
		if (getArgument() instanceof Log10) {
			return new Log10(((Log10)getArgument()).getArgument().inverse());
		}
		if (getArgument() instanceof Negative) {
			return ((Negative)getArgument()).getArgument();
		}
		if (getArgument() instanceof Re) {
			return new Re(((Re)getArgument()).getArgument().negative());
		}
		if (getArgument() instanceof Complex) {
			Complex c = (Complex)getArgument();
			return new Complex((NumNumSum)c.real.negative().getSimplest(), (NumNumSum)c.imag.negative().getSimplest());
		}
		if (getArgument() instanceof Exponent) {
			Exponent exp = (Exponent)getArgument();
			return new Exponent(exp.base, exp.power, !exp.negative);
		}
		if (getArgument() instanceof Fraction) {
			Fraction frac = (Fraction)getArgument();
			return new Fraction((NumNumSum)frac.numerator.negative().getSimplest(), frac.denominator);
		}
		if (getArgument() instanceof Imaginary) {
			return new Imaginary((NumNumSum)((Imaginary)getArgument()).value.negative().getSimplest());
		}
		if (getArgument() instanceof Integer) {
			Integer n = (Integer)getArgument();
			if (n.value == Long.MIN_VALUE) {
				throw new ArithmeticException("Overflow while negating integer <" + n + "> (" + n.value + ")");
			}
			return new Integer(-n.value);
		}
		if (getArgument() instanceof Multiple) {
			Multiple R = new Multiple(new ArrayList<NumExponent>(((Multiple)getArgument()).values));
			R.values.set(0, (NumExponent)R.values.get(0).negative().getSimplest());
			return R;
		}
		if (getArgument() instanceof NumberSum) {
			NumberSum R = new NumberSum(new ArrayList<NumMultiple>(((NumberSum)getArgument()).values));
			for (int i=0; i<R.values.size(); i++) {
				R.values.set(i, (NumMultiple)R.values.get(i).negative().getSimplest());
			}
			return R;
		}
		if (getArgument() instanceof Transcendental) {
			return new Transcendental(((Transcendental)getArgument()).value, !((Transcendental)getArgument()).negative);
		}
		if (getArgument() instanceof PolynomialTerm) {
			PolynomialTerm poly = (PolynomialTerm)getArgument();
			return new PolynomialTerm((NumNumSum)poly.coefficient.negative().getSimplest(), poly.variable, poly.power);
		}
		
		return this;
	}
	@Override public Expr derivativePartial(int var) {
		if (var == 0) return NEG_ONE;
		else return ZERO;
	}
	@Override public Expr antiderivative(Variable var) {
		return getArgument().antiderivative(var).negative().getSimplest();
	}
	
	@Override public Compare compare(Expr other) {
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
		default: throw new AssertionError("Received an invalid Compare state '" + compare + "'.");
		}
	}
	@Override public Compare isZero() {
		return getArgument().isZero();
	}
	@Override public Compare isPositive() {
		return getArgument().isNegative();
	}
	@Override public Compare isNegative() {
		return getArgument().isPositive();
	}
	@Override public Compare isReal() {
		return getArgument().isReal();
	}
	@Override public Compare isImag() {
		return getArgument().isImag();
	}
	
	@Override public Expr makeUseful() {
		Negative newExpr = (Negative)super.makeUseful();
		if (newExpr.getArgument() instanceof Integer) {
			Integer arg = (Integer)newExpr.getArgument();
			if (arg.value == Long.MIN_VALUE) {
				throw new ArithmeticException("Overflow error while computing " + newExpr);
			}
			return new Integer(-arg.value);
		}
		if (newExpr.getArgument() instanceof Negative) {
			return newExpr.getArgument();
		}
		return newExpr;
	}
	
}
