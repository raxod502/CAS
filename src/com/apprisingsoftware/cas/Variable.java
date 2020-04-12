package com.apprisingsoftware.cas;

import java.util.HashMap;
import java.util.Map;

class Variable extends Expr implements Applet.NodeData {
	
	public String name;
	public Expr value; // = null
	public Map<String, Boolean> constraints = new HashMap<String, Boolean>();
	
	public Variable(String name) {
		this.name = name;
	}
	public Variable(String name, Map<String, Boolean> constraints) {
		this(name);
		this.constraints = constraints;
	}
	public Variable(String name, Expr value) {
		this(name);
		this.value = value;
	}
	public Variable(String name, Expr value, Map<String, Boolean> constraints) {
		this(name);
		this.value = value;
		this.constraints = constraints;
	}
	
	@Override public Expr getAlternate() {
		if (value != null && constraints.get("shouldSimplify") == null) {
			return value;
		}
		else {
			return this;
		}
	}
	@Override public Expr derivative(Variable var) {
		if (var.equalTo(this)) {
			return ONE;
		}
		else {
			return ZERO;
		}
	}
	@Override public Expr antiderivative(Variable var) {
		if (var.equalTo(this)) {
			return new PolynomialTerm((NumNumSum)new Inverse(TWO).getSimplest(), this, TWO).getSimplest();
		}
		else {
			return new DualProduct(var, this).getSimplest();
		}
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		Boolean constraint = constraints.get("isZero");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	@Override public Compare isPositive() {
		Boolean constraint = constraints.get("isPositive");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	@Override public Compare isNegative() {
		Boolean constraint = constraints.get("isNegative");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	@Override public Compare isReal() {
		Boolean constraint = constraints.get("isReal");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	@Override public Compare isImag() {
		Boolean constraint = constraints.get("isImag");
		return constraint == null ? MAYBE :
			constraint ? YES : NO;
	}
	
	@Override public String toString() {
		return name;
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override public final int nodeCount() {
		return 1;
	}
	
}
