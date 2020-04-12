package cas;

import java.util.ArrayList;

public class FunctionConstant extends NumIntegerTranscendentalFunctionConstant {
	
	public Function func;
	
	public FunctionConstant(Function func) {
		for (Expr arg : func.args) {
			if (!(arg instanceof Number)) {
				throw new IllegalArgumentException("FunctionConstant's Function's arguments must all be Numbers (one or more is not)");
			}
		}
		this.func = func;
	}
	
	public double doubleValue() {
		if (func instanceof ArcCos) {
			return Math.acos(func.getDouble(0));
		}
		if (func instanceof ArcSin) {
			return Math.asin(func.getDouble(0));
		}
		if (func instanceof ArcTan) {
			return Math.atan(func.getDouble(0));
		}
		if (func instanceof ArcTan2) {
			return Math.atan2(func.getDouble(1), func.getDouble(0));
		}
		if (func instanceof Arg) {
			return Math.atan2(((NumNumSum)new Im(func.getArgument(0)).simplify(null)).doubleValue(),
					((NumNumSum)new Re(func.getArgument(0)).simplify(null)).doubleValue());
		}
		if (func instanceof Cis) {
//			return new ComplexDouble(((NumNumSum)new Im(func.getArgument(0)).simplify(null)).doubleValue(),
//					((NumNumSum)new Re(func.getArgument(0)).simplify(null)).doubleValue());
		}
		if (func instanceof ComplexExpr) {
//			return new ComplexDouble(func.getDouble(0), func.getDouble(1));
		}
		if (func instanceof Cos) {
			return Math.cos(func.getDouble(0));
		}
		if (func instanceof Difference) {
			return func.getDouble(0) - func.getDouble(1);
		}
		if (func instanceof DualExponentiation) {
			return Math.pow(func.getDouble(0), func.getDouble(1));
		}
		if (func instanceof DualProduct) {
			return func.getDouble(0) * func.getDouble(1);
		}
		if (func instanceof DualSum) {
			return func.getDouble(0) + func.getDouble(1);
		}
		if (func instanceof Exp) {
			return Math.exp(func.getDouble(0));
		}
		if (func instanceof Exponentiation) {
			double res = func.getDouble(0);
			for (int i=1; i<func.args.size(); i++) {
				res = Math.pow(res, func.getDouble(i));
			}
			return res;
		}
		if (func instanceof Im) {
			return ((NumNumSum)func.simplify(null)).doubleValue();
		}
		if (func instanceof ImaginaryExpr) {
//			return new ComplexDouble(0, func.getDouble(0));
		}
		if (func instanceof Inverse) {
			return 1 / func.getDouble(0);
		}
		if (func instanceof Log) {
			return Math.log(func.getDouble(0));
		}
		if (func instanceof Log10) {
			return Math.log10(func.getDouble(0));
		}
		if (func instanceof LogBase) {
			return Math.log(func.getDouble(1)) / Math.log(func.getDouble(0));
		}
		if (func instanceof Negative) {
			return -func.getDouble(0);
		}
		if (func instanceof Product) {
			double total = 1;
			for (int i=0; i<func.args.size(); i++) {
				total *= func.getDouble(i);
			}
			return total;
		}
		if (func instanceof Quotient) {
			return func.getDouble(0) / func.getDouble(1);
		}
		if (func instanceof Re) {
			return ((NumNumSum)func.simplify(null)).doubleValue();
		}
		if (func instanceof Sgn) {
			return ((NumNumSum)func.simplify(null)).doubleValue();
		}
		if (func instanceof Sin) {
			return Math.sin(func.getDouble(0));
		}
		if (func instanceof Sqrt) {
			return Math.sqrt(func.getDouble(0));
		}
		if (func instanceof Sum) {
			double total = 0;
			for (int i=0; i<func.args.size(); i++) {
				total += func.getDouble(i);
			}
			return total;
		}
		if (func instanceof Tan) {
			return Math.tan(func.getDouble(0));
		}
		throw new IllegalStateException("FunctionConstant received invalid function: " + func);
	}
	
	public ArrayList<Expr> args() {
		return func.args;
	}
	
	public Compare compare(Expr other) {
		return UNKNOWN;
	}
	public Compare isZero() {
		return func.isZero();
	}
	public Compare isPositive() {
		return func.isPositive();
	}
	public Compare isNegative() {
		return func.isNegative();
	}
	
	public String toString() {
		return func.toString();
	}
	
}
