package cas;

import java.util.*;

public class Variable extends Expr {
	
	public String name;
	public Expr value; // = null
	public Dictionary<String, Boolean> constraints;
	
	public Variable(String name) {
		this.name = name;
	}
	public Variable(String name, Dictionary<String, Boolean> constraints) {
		this(name);
		this.constraints = constraints;
	}
	public Variable(String name, Expr value) {
		this(name);
		this.value = value;
	}
	public Variable(String name, Expr value, Dictionary<String, Boolean> constraints) {
		this(name);
		this.value = value;
		this.constraints = constraints;
	}
	
	public Expr simplifyExpr() {
		if (value != null && constraints.get("shouldSimplify") == null) {
			return value;
		}
		else {
			return this;
		}
	}
	public Expr derivative(Variable var) {
		if (var.equalTo(this)) {
			return ONE;
		}
		else {
			return ZERO;
		}
	}
	public Expr antiderivative(Variable var) {
		if (var.equalTo(this)) {
			return new PolynomialTerm((NumIntegerTranscendentalFunctionConstant)new Inverse(TWO).simplify(), this, TWO).simplify();
		}
		else {
			return new DualProduct(var, this).simplify();
		}
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		Boolean constraint = constraints.get("isZero");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	public Compare isPositive() {
		Boolean constraint = constraints.get("isPositive");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	public Compare isNegative() {
		Boolean constraint = constraints.get("isNegative");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	public Compare isReal() {
		Boolean constraint = constraints.get("isReal");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	public Compare isImag() {
		Boolean constraint = constraints.get("isImag");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	
	public String toString() {
		return name;
	}
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((constraints == null) ? 0 : constraints.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable other = (Variable) obj;
		if (constraints == null) {
			if (other.constraints != null)
				return false;
		} else if (!constraints.equals(other.constraints))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
