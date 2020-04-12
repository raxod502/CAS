package com.apprisingsoftware.cas;


class PolynomialTerm extends Expr {
	
	public NumNumSum coefficient;
	public Variable variable;
	public Integer power;
	
	public PolynomialTerm(NumNumSum coefficient, Variable variable, Integer power) {
		if (power.value < 0) throw new IllegalArgumentException();
		// Creating a new HashSet lets me trash the values. My functions assume that
		// all HashSets have already been added to the set list.
		if (variable.isReal() != YES) throw new AssertionError();
		this.coefficient = coefficient;
		this.variable = variable;
		this.power = power;
	}
	
	@Override public Expr getAlternate() {
		return new DualProduct(coefficient, new DualExponentiation(variable, power));
	}
	
	@Override public Expr derivative(Variable var)  {
		if (var.equalTo(variable)) {
			Expr newcoef = new DualProduct(coefficient, power).getSimplest();
			if (newcoef instanceof Number) {
				Expr newpower = new Difference(power, ONE).getSimplest();
				if (newpower instanceof Integer) {
					return new PolynomialTerm((NumNumSum)newcoef, variable, (Integer)newpower);
				}
				throw new AssertionError("Could not reduce difference " + newpower + " to an Integer.");
			}
			throw new AssertionError("Could not reduce product " + newcoef + " to a Number.");
		}
		else {
			return ZERO;
		}
	}
	@Override public Expr antiderivative(Variable var) {
		if (var.equalTo(variable)) {
			Expr newcoef = new Quotient(coefficient, new Difference(power, ONE)).getSimplest();
			if (newcoef instanceof Number) {
				Expr newpower = new DualSum(power, ONE).getSimplest();
				if (newpower instanceof Integer) {
					return new PolynomialTerm((NumNumSum)newcoef, variable, (Integer)newpower);
				}
				throw new AssertionError("Could not reduce difference " + newpower + " to an Integer.");
			}
			throw new AssertionError("Could not reduce product " + newcoef + " to a Number.");
		}
		else {
			return new DualProduct(this, var);
		}
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		return coefficient.isZero().or(variable.isZero().and(power.isZero().not()));
	}
	@Override public Compare isPositive() {
		boolean sgn = true; // sgn ? positive : negative
		if (coefficient.isNegative() == YES) sgn = !sgn;
		Compare comp = variable.isNegative();
		if (comp == MAYBE) return MAYBE;
		if (power.value % 2 != 0 && comp == YES) {
			sgn = !sgn;
		}
		return (sgn ? YES : NO).and(isZero().not());
	}
	@Override public Compare isNegative() {
		return isZero().or(isPositive()).not();
	}
	@Override public Compare isReal() {
		return YES;
	}
	@Override public Compare isImag() {
		return NO;
	}
	
	@Override public String toString() {
		return coefficient + "*" + variable + "^" + power;
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((coefficient == null) ? 0 : coefficient.hashCode());
		result = prime * result + ((power == null) ? 0 : power.hashCode());
		result = prime * result
				+ ((variable == null) ? 0 : variable.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolynomialTerm other = (PolynomialTerm) obj;
		if (coefficient == null) {
			if (other.coefficient != null)
				return false;
		} else if (!coefficient.equals(other.coefficient))
			return false;
		if (power == null) {
			if (other.power != null)
				return false;
		} else if (!power.equals(other.power))
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}
	
	@Override public final int nodeCount() {
		return 1;
	}
	
}
