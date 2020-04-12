package cas;

import java.util.ArrayList;
import java.util.Arrays;

public class Multiple extends NumMultiple {
	
	public ArrayList<NumExponent> values;
	
	public Multiple(ArrayList<NumExponent> args) {
		if (args.size() == 0) throw new IllegalArgumentException();
		if (args.size() == 1) throw new IllegalArgumentException();
		values = args;
	}
	public Multiple(NumExponent... args) {
		this(new ArrayList<NumExponent>(Arrays.asList(args)));
	}
	
	public double doubleValue() {
		double total = 1;
		for (NumExponent value : values) {
			total *= value.doubleValue();
		}
		return total;
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		for (Expr arg : values) {
			Compare isZero = arg.isZero();
			if (isZero == YES) {
				return YES;
			}
			else if (isZero == MAYBE) {
				return MAYBE;
			}
		}
		return NO;
	}
	public Compare isPositive() {
		boolean sgn = true;
		for (Expr arg : values) {
			if (arg.isZero() == YES) {
				return NO;
			}
			Compare isPositive = arg.isPositive();
			if (isPositive == NO) {
				sgn = !sgn;
			}
			else if (isPositive == MAYBE) {
				return MAYBE;
			}
		}
		if (sgn) return YES;
		else return NO;
	}
	public Compare isNegative() {
		boolean sgn = true;
		for (Expr arg : values) {
			if (arg.isZero() == YES) {
				return NO;
			}
			Compare isPositive = arg.isPositive();
			if (isPositive == NO) {
				sgn = !sgn;
			}
			else if (isPositive == MAYBE) {
				return MAYBE;
			}
		}
		if (!sgn) return YES;
		else return NO;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(values.get(0).toString());
		for (int i=1; i<values.size(); i++) {
			sb.append("*" + values.get(i).toString());
		}
		return sb.toString();
	}
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Multiple other = (Multiple) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
}
