package cas;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public final class Util {
	
	private Util() {}
	
	public static boolean isFactor(long num, long factor) {
		return num % factor == 0;
	}
	
	private static ArrayList<Long> factorCore(long num, boolean primeFactors, boolean includeNum) {
		long originalNum = num;
		ArrayList<Long> factors = new ArrayList<Long>();
		long factor = 2;
		long limit = (long)Math.ceil(num / 2.0);
		while (factor <= limit && num != 1) {
			if (isFactor(num, factor)) {
				factors.add(factor);
				if (primeFactors) {
					num /= factor;
				}
				else {
					factor += 1;
				}
			}
			else {
				factor += 1;
			}
		}
		if (num == originalNum && includeNum && num != 1) {
			factors.add(num);
		}
		return factors;
	}
	
	public static ArrayList<Long> primeFactorList(long num) {
		ArrayList<Long> factors = factorCore(num, true, true);
		return factors;
	}
	
	public static ArrayList<Long> uniquePrimeFactorList(long num) {
		ArrayList<Long> factors = factorCore(num, true, true);
		return new ArrayList<Long>(new LinkedHashSet<Long>(factors));
	}
	
}