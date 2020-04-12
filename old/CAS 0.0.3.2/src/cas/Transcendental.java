package cas;

public class Transcendental extends NumIntegerTranscendentalFunctionConstant implements Applet.NodeData {
	
	public Transcendent value;
	public boolean negative;
	
	public Transcendental(Transcendent value, boolean negative) {
		this.value = value;
		this.negative = negative;
	}
	public Transcendental(Transcendent value) {
		this(value, false);
	}
	
	public double doubleValue() {
		double res;
		switch (value) {
		case PI: res = Math.PI; break;
		case E: res = Math.E; break;
		default: throw new IllegalStateException();
		}
		if (negative) res *= -1;
		return res;
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return NO; // As of yet!
	}
	public Compare isPositive() {
		return negative ? NO : YES;
	}
	public Compare isNegative() {
		return negative ? YES : NO;
	}
	
	public String toString() {
		return value.toString();
	}
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (negative ? 1231 : 1237);
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
		Transcendental other = (Transcendental) obj;
		if (negative != other.negative)
			return false;
		if (value != other.value)
			return false;
		return true;
	}
	
}
