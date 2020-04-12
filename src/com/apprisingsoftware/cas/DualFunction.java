package com.apprisingsoftware.cas;

import java.lang.reflect.InvocationTargetException;

abstract class DualFunction extends DoubleArgumentFunction {
	
	public DualFunction(int derivativeLeft, int derivativeRight, Expr left,
			Expr right) {
		super(derivativeLeft, derivativeRight, left, right);
	}
	public DualFunction(Expr left, Expr right) {
		super(left, right);
	}
	
	@Override public final Expr getAlternate() {
		if (this instanceof DualProduct || this instanceof DualSum) {
			try {
				Expr alternateL = getClass().getConstructor(Expr.class, Expr.class).newInstance(getLeft(), getRight()).getAlternateWithoutCommutivity();
				Expr alternateR = getClass().getConstructor(Expr.class, Expr.class).newInstance(getRight(), getLeft()).getAlternateWithoutCommutivity();
				boolean excludeL = (this instanceof DualProduct && alternateL instanceof Multiple && !(alternateR instanceof DualProduct)) ||
						(this instanceof DualSum && alternateL instanceof NumberSum && !(alternateR instanceof DualSum));
				boolean excludeR = (this instanceof DualProduct && alternateR instanceof Multiple && !(alternateL instanceof DualProduct)) ||
						(this instanceof DualSum && alternateR instanceof NumberSum && !(alternateL instanceof DualSum));
				if (excludeL && !excludeR) {
					return alternateR;
				}
				else if (excludeR && !excludeL) {
					return alternateL;
				}
				else if (alternateL.equals(alternateR)) {
					return alternateL;
				}
				else {
					alternateL.alternateResults.add(alternateR);
					return alternateL;
				}
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		else { // The function is non-commutative, i.e. DualExponentiation
			return getAlternateWithoutCommutivity();
		}
	}
	public abstract Expr getAlternateWithoutCommutivity();
	
	@Override public abstract Expr derivative(Variable var);
	@Override public abstract Expr antiderivative(Variable var);
	@Override public abstract Compare compare(Expr other);
	
}
