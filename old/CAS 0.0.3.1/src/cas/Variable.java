package cas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Variable extends Expr implements Applet.NodeData {
	
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
	
	public Expr simplifyExpr(HashSet<Expr> eq) {
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
			return new PolynomialTerm((NumIntegerTranscendentalFunctionConstant)new Inverse(TWO).simplify(null), this, TWO).simplify(null);
		}
		else {
			return new DualProduct(var, this).simplify(null);
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
