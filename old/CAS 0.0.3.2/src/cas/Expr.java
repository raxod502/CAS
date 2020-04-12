package cas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class Expr implements IEquivalenceClass {
	
	public static final Compare YES = Compare.YES;
	public static final Compare NO = Compare.NO;
	public static final Compare MAYBE = Compare.MAYBE;
	public static final Compare EQUAL = Compare.EQUAL;
	public static final Compare GREATER = Compare.GREATER;
	public static final Compare LESSER = Compare.LESSER;
	public static final Compare UNEQUAL = Compare.UNEQUAL;
	public static final Compare GREATER_OR_EQUAL = Compare.GREATER_OR_EQUAL;
	public static final Compare LESSER_OR_EQUAL = Compare.LESSER_OR_EQUAL;
	public static final Compare UNKNOWN = Compare.UNKNOWN;
	public static final Compare INDETERMINATE = Compare.INDETERMINATE;
	public static final Integer ZERO = new Integer(0);
	public static final Integer ONE = new Integer(1);
	public static final Integer TWO = new Integer(2);
	public static final Integer THREE = new Integer(3);
	public static final Integer FOUR = new Integer(4);
	public static final Integer FIVE = new Integer(5);
	public static final Integer SIX = new Integer(6);
	public static final Integer SEVEN = new Integer(7);
	public static final Integer EIGHT = new Integer(8);
	public static final Integer NINE = new Integer(9);
	public static final Integer TEN = new Integer(10);
	public static final Integer ELEVEN = new Integer(11);
	public static final Integer NEG_ONE = new Integer(-1);
	public static final Fraction oneHalf = new Fraction(ONE, TWO);
	public static final Fraction negOneHalf = new Fraction(NEG_ONE, TWO);
	public static final Fraction oneFourth = new Fraction(ONE, new Integer(4));
	public static final Fraction negOneFourth = new Fraction(NEG_ONE, new Integer(4));
	public static final Fraction threeFourths = new Fraction(THREE, new Integer(4));
	public static final Fraction negThreeFourths = new Fraction(new Integer(-3), new Integer(4));
	public static final Fraction oneSixth = new Fraction(ONE, new Integer(6));
	public static final Fraction negOneSixth = new Fraction(NEG_ONE, new Integer(6));
	public static final Fraction oneThird = new Fraction(ONE, THREE);
	public static final Fraction negOneThird = new Fraction(NEG_ONE, THREE);
	public static final Fraction twoThirds = new Fraction(TWO, THREE);
	public static final Fraction negTwoThirds = new Fraction(new Integer(-2), THREE);
	public static final Fraction fiveSixths = new Fraction(new Integer(5), new Integer(6));
	public static final Fraction negFiveSixths = new Fraction(new Integer(-5), new Integer(6));
	public static final Exponent ROOT_TWO = new Exponent(TWO, oneHalf);
	public static final Exponent NEG_ROOT_TWO = new Exponent(TWO, oneHalf, true);
	public static final Exponent ROOT_THREE = new Exponent(THREE, oneHalf);
	public static final Exponent NEG_ROOT_THREE = new Exponent(THREE, oneHalf, true);
	public static final Transcendental pi = new Transcendental(Transcendent.PI);
	public static final Transcendental negPi = new Transcendental(Transcendent.PI, true);
	public static final Transcendental E = new Transcendental(Transcendent.E);
	public static final Transcendental negE = new Transcendental(Transcendent.E, true);
	public static final Imaginary I = new Imaginary(ONE);
	public static final Imaginary negI = new Imaginary(NEG_ONE);
	
	public EquivalenceClass equivalenceClass;
	
	// Expr.expand is the manager method for dealing with an Expr's eq.class.
	// *.expandExpr is a method that can be overridden by subclasses to add additional
	// possibilities to the equivalence class, e.g. x + y -> y + x
	public final EquivalenceClass expand() {
		if (equivalenceClass == null) {
			equivalenceClass = EquivalenceClass.getEquivalenceClass(this);
		}
		expandExpr();
		for (Expr expr : equivalenceClass) {
			addTransform(expr);
		}
		return equivalenceClass;
	}
	private final void addTransform(Expr expr) {
		IEquivalenceClass alternate = expr.transform();
		if (alternate == null) return;
		if (alternate instanceof EquivalenceClass)
			equivalenceClass.combine((EquivalenceClass)alternate);
		else if (alternate instanceof Expr)
			equivalenceClass.addExpr((Expr)alternate);
		else
			throw new IllegalStateException();
		for (Expr alt : alternate) {
			alt.expand();
		}
	}
	public void expandExpr() {}
	// In case I need to add special Expr logic later...
	public final IEquivalenceClass transform() {
		return transformExpr();
	}
	public abstract IEquivalenceClass transformExpr();
	public final EquivalenceClass getReduced() {
		expand();
		return equivalenceClass;
	}
	public final Number getSimplifiedNumber() {
		expand();
		return equivalenceClass.getNumber();
	}
	public final Expr getSimplified() {
		expand();
		return equivalenceClass.getSimplest();
	}
	
	public abstract IEquivalenceClass derivative(Variable var);
	public abstract IEquivalenceClass antiderivative(Variable var);
	// Method can be overridden, e.g. by functions that go to infinity at some points but have defined definite integrals
	public EquivalenceClass integrate(Variable var, Expr lower, Expr upper)  {
		return new Difference(upper.antiderivative(var).getSimplest(), lower.antiderivative(var).getSimplest()).getReduced();
	}
	/**
	 * Returns the additive inverse of the current Expr, without
	 * simplifying it. This method makes use of the Negative class.
	 */
	public final Expr negative() {
		return new Negative(this);
	}
	/**
	 * Returns the multiplicative inverse of the current Expr,
	 * without simplifying it. This method makes use of the
	 * Inverse class.
	 */
	public final Expr inverse() {
		return new Inverse(this);
	}
	/**
	 * Returns the second power of the current Expr, without
	 * simplifying it. This method makes use of the
	 * DualExponentiation class.
	 */
	public final Expr square() {
		return new DualExponentiation(this, TWO);
	}
	/**
	 * Returns the third power of the current Expr, without
	 * simplifying it. This method makes use of the
	 * DualExponentiation class.
	 */
	public final Expr cube() {
		return new DualExponentiation(this, THREE);
	}
	/**
	 * Returns the DualSum of this Expr and the Expr argument.
	 */
	public final Expr plus(Expr other) {
		return new DualSum(this, other);
	}
	/**
	 * Returns the Difference of this Expr and the Expr argument.
	 */
	public final Expr minus(Expr other) {
		return new Difference(this, other);
	}
	/**
	 * Returns the DualProduct of this Expr and the Expr argument.
	 */
	public final Expr times(Expr other) {
		return new DualProduct(this, other);
	}
	/**
	 * Returns the Quotient of this Expr and the Expr argument.
	 */
	public final Expr dividedBy(Expr other) {
		return new Quotient(this, other);
	}
	/**
	 * Returns the DualExponentiation of this Expr and the Expr argument.
	 */
	public final Expr toThe(Expr other) {
		return new DualExponentiation(this, other);
	}
	
	/**
	 * Returns a Compare value representing the relation of
	 * the current Expr to the argument Expr. The algorithm
	 * is "smart" and will attempt to determine equality even
	 * if the Exprs are of different forms. The _equalTo(),
	 * notEqualTo(), greaterThan(), lessThan(), etc. methods
	 * translate the return value of this return into a YES/
	 * NO/MAYBE value.
	 * 
	 * As of yet, very few compare() methods have been implemented.
	 */
	public abstract Compare compare(Expr other);
	public boolean equalTo(Expr other) {
		return equals(other) || _equalTo(other) == YES;
	}
	public Compare _equalTo(Expr other) {
		Compare compare = compare(other);
		if (compare == UNKNOWN) return MAYBE;
		return compare(other) == EQUAL ? YES : NO;
	}
	public Compare notEqualTo(Expr other) {
		Compare compare = compare(other);
		if (compare == UNKNOWN) return MAYBE;
		return compare == GREATER || compare == LESSER || compare == UNEQUAL ? YES : NO;
	}
	public Compare lessThan(Expr other) {
		Compare compare = compare(other);
		if (compare == UNKNOWN) return MAYBE;
		return compare(other) == LESSER ? YES : NO;
	}
	public Compare greaterThan(Expr other) {
		Compare compare = compare(other);
		if (compare == UNKNOWN) return MAYBE;
		return compare(other) == GREATER ? YES : NO;
	}
	public Compare lessThanOrEqualTo(Expr other) {
		Compare compare = compare(other);
		if (compare == UNKNOWN) return MAYBE;
		return compare == EQUAL || compare == LESSER || compare == LESSER_OR_EQUAL ? YES : NO;
	}
	public Compare greaterThanOrEqualTo(Expr other) {
		Compare compare = compare(other);
		if (compare == UNKNOWN) return MAYBE;
		return compare == EQUAL || compare == GREATER || compare == GREATER_OR_EQUAL ? YES : NO;
	}
	/**
	 * The value is equal to zero.
	 */
	public abstract Compare isZero();
	/**
	 * The value isReal() and greater than zero.
	 */
	public abstract Compare isPositive();
	/**
	 * The value isReal() and less than zero.
	 */
	public abstract Compare isNegative();
	/**
	 * The value has an imaginary component of zero.
	 */
	public abstract Compare isReal();
	/**
	 * The value has a real component of zero.
	 */
	public abstract Compare isImag();
	/**
	 * The value is neither wholly real nor wholly imaginary.
	 */
	public final Compare isComplex() {
		// See deMorgan's Law.
		return isReal().or(isImag()).not();
	}
	/**
	 * Returns a human-readable, but not necessarily nicely
	 * formatted, version of the Expr.
	 */
	@Override
	public abstract String toString();
	/**
	 * Default hashCode implementation.
	 */
	@Override
	public int hashCode() {
		return 1;
	}
	/**
	 * The values of all instance variables except for "array"
	 * are equal. The Exprs are and will display identically.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
	
	@Override
	public boolean contains(Expr expr) {
		return EquivalenceClass.getEquivalenceClass(this).contains(expr);
	}
	@Override
	public Number getNumber() {
		return EquivalenceClass.getEquivalenceClass(this).getNumber();
	}
	@Override
	public Expr getSimplest() {
		return this;
	}
	@Override
	public boolean addExpr(Expr expr) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<Expr> toList() {
		List<Expr> list = new ArrayList<Expr>();
		list.add(this);
		return list;
	}
	@Override
	public Iterator<Expr> iterator() {
		return new Iterator<Expr>() {
			private boolean hasNext = true;
			public boolean hasNext() {
				return hasNext;
			}
			public Expr next() {
				hasNext = false;
				return Expr.this;
			}
		};
	}
	
}
