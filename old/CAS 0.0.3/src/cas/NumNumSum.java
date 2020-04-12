package cas;

public abstract class NumNumSum extends NumComplexImaginary {
	
	public final ComplexDouble complexdoubleValue() {
		return new ComplexDouble(doubleValue());
	}
	public abstract double doubleValue();
	
	public final Compare isReal() {
		return YES;
	}
	public final Compare isImag() {
		return NO;
	}
	
}
