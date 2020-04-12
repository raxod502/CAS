package com.apprisingsoftware.cas;

import java.util.ArrayList;
import java.util.Arrays;

class NumberSum extends NumNumSum {
	
	public ArrayList<NumMultiple> values;
	
	public NumberSum(ArrayList<NumMultiple> args) {
		if (args.size() == 0) throw new IllegalArgumentException();
		if (args.size() == 1) throw new IllegalArgumentException();
		values = args;
	}
	public NumberSum(NumMultiple... args) {
		this(new ArrayList<NumMultiple>(Arrays.asList(args)));
	}
	
	@Override public double doubleValue() {
		double total = 0;
		for (NumMultiple arg : values) {
			total += arg.doubleValue();
		}
		return total;
	}
	
	@Override public Compare compare(Expr other) {
		return UNKNOWN;
	}
	@Override public Compare isZero() {
		boolean allPositive = true, allNegative = true, allZero = true;
		for (Expr arg : values) {
			if (arg.isPositive() != YES) allPositive = false;
			if (arg.isNegative() != YES) allNegative = false;
			if (arg.isZero() != YES) allZero = false;
		}
		if (allPositive || allNegative) return NO;
		if (allZero) return YES;
		return MAYBE;
	}
	@Override public Compare isPositive() {
		boolean allPositive = true, allNegative = true, allZero = true;
		for (Expr arg : values) {
			if (arg.isPositive() != YES) allPositive = false;
			if (arg.isNegative() != YES) allNegative = false;
			if (arg.isZero() != YES) allZero = false;
		}
		if (allZero || allNegative) return NO;
		if (allPositive) return YES;
		return MAYBE;
	}
	@Override public Compare isNegative() {
		boolean allPositive = true, allNegative = true, allZero = true;
		for (Expr arg : values) {
			if (arg.isPositive() != YES) allPositive = false;
			if (arg.isNegative() != YES) allNegative = false;
			if (arg.isZero() != YES) allZero = false;
		}
		if (allZero || allPositive) return NO;
		if (allNegative) return YES;
		return MAYBE;
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder(values.get(0).toString());
		for (int i=1; i<values.size(); i++) {
			sb.append("+" + values.get(i).toString());
		}
		return sb.toString();
	}
	@Override public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NumberSum other = (NumberSum) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
}
