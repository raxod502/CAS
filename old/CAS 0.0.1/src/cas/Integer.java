package cas;

public class Integer extends NumIntegerTranscendentalFunctionConstant implements Applet.NodeData {
	
	public long value;
	
	public Integer(long value) {
		this.value = value;
	}
	public Integer(int value) {
		this.value = value;
	}
	public Integer(short value) {
		this.value = value;
	}
	public Integer(byte value) {
		this.value = value;
	}
	public Integer(char value) {
		this.value = value;
	}
	
	public double doubleValue() {
		return value;
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return value == 0 ? YES : NO;
	}
	public Compare isPositive() {
		return value > 0 ? YES : NO;
	}
	public Compare isNegative() {
		return value < 0 ? YES : NO;
	}
	
	public String toString() {
		return String.valueOf(value);
	}
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Integer other = (Integer) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
}