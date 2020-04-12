package com.apprisingsoftware.cas;

import java.util.ArrayList;
import java.util.List;


abstract class Expr {
	
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
	
	public List<Expr> alternateResults = new ArrayList<Expr>();
	
	EquivalenceClass equivalenceClass;
	public EquivalenceClass equivalenceClass() {
		return equivalenceClass;
	}
	public void setEquivalent(Expr expr) {
//		if (expr instanceof DualProduct && ((DualProduct) expr).args.size() != 2) {
//			System.out.println("[ERROR] OH NOES!!!");
//		}
//		System.out.println(expr);
		equivalenceClass.add(expr);
		for (EquivalenceClass dupe : EquivalenceClass.getDuplicateEquivalenceClasses(expr, equivalenceClass)) {
			equivalenceClass.absorb(dupe);
		}
	}
	public final Expr getSimplest() {
		expand();
		return equivalenceClass.getSimplest();
	}
	
	/**
	 * Fills out the equivalence class of the current Expr
	 * with all possible alternates. This method is recursive
	 * and calls both expandExpr() and getAlternate(), but
	 * is protected against redundant computations or infinite
	 * recursion.
	 */
	public final void expand() {
		// Prevent infinite recursion
		if (EquivalenceClass.hasEquivalenceClass(this)) {
			equivalenceClass = EquivalenceClass.getEquivalenceClass(this);
			return;
		}
		RecursionPrint.push(this);
		equivalenceClass = EquivalenceClass.newEquivalenceClass(this);
		RecursionPrint.log("Created new equivalence class " + equivalenceClass);
		// Expand further, i.e. by introducing new Function variants based
		// on argument EquivalenceClasses.
		RecursionPrint.log("Calling expandExpr:");
		equivalenceClass.getOnlyExpr().expandExpr();
		RecursionPrint.log("expandExpr yielded updated equivalence class " + equivalenceClass);
		// For every Expr in the current equivalenceClass, add its alternate()
		// variants.
		// This will also call alternate() for the current Expr.
		for (Expr expr : equivalenceClass.copy()) { // To avoid ConcurrentModification
			RecursionPrint.log("Getting alternate form of equivalent expr " + expr + ":");
			Expr alternate = expr.getAlternate();
			RecursionPrint.log("Alternate form of " + expr + " is " + alternate + (alternate.alternateResults.size() > 0 ? " " + alternate.alternateResults : ""));
			// Get any alternate forms of each alternate, recursively.
			// Infinite recursion is prevented by the line at the beginning of
			// this method.
			// Even if getAlternate() just returns `this`, recursion is prevented.
			RecursionPrint.log("Expanding main alternate " + alternate);
			if (alternate.equivalenceClass != null)
				RecursionPrint.log("[WARNING] main alternate already has an equivalency class: " + alternate.equivalenceClass);
			alternate.expand();
			RecursionPrint.log("Expansion yielded equivalence class for " + alternate + ": " + alternate.equivalenceClass);
			// Adding this Expr will automagically add all of its equivalent Exprs.
			// (See EquivalenceClass)
			RecursionPrint.logWithoutNewline("Combining class transforms this equivalence class from " + equivalenceClass);
			setEquivalent(alternate);
			RecursionPrint.logRawLine(" to " + equivalenceClass);
			// Option for getAlternate() to pass back multiple results:
			for (Expr anotherAlternate : alternate.alternateResults) {
				RecursionPrint.log("Expanding another alternate " + anotherAlternate);
				if (anotherAlternate.equivalenceClass != null)
					RecursionPrint.log("[WARNING] main alternate already has an equivalency class: " + anotherAlternate.equivalenceClass);
				anotherAlternate.expand();
				RecursionPrint.log("Expansion yielded equivalence class for " + anotherAlternate + ": " + anotherAlternate.equivalenceClass);
				RecursionPrint.logWithoutNewline("Combining class transforms this equivalence class from " + equivalenceClass);
				setEquivalent(anotherAlternate);
				RecursionPrint.logRawLine(" to " + equivalenceClass);
			}
		}
		RecursionPrint.pop();
	}
	/**
	 * Override this method to add additional variants of
	 * an Expr to its equivalency class before getAlternate()
	 * is called on each.
	 */
	public void expandExpr() {}
	/**
	 * Override this method to return an alternate form of
	 * an expression, or several alternate forms (simply
	 * add extra alternates to the equivalence group of the
	 * returned Expr).
	 * @return another form of the current Expr (unexpanded)
	 */
	public abstract Expr getAlternate();
	
	public abstract Expr derivative(Variable var);
	public abstract Expr antiderivative(Variable var);
	// Method can be overridden, e.g. by functions that go to infinity at some points but have defined definite integrals
	public Expr integrate(Variable var, Expr lower, Expr upper)  {
		return new Difference(upper.antiderivative(var).getSimplest(), lower.antiderivative(var).getSimplest()).getSimplest();
	}
	
	/**
	 * Returns the additive inverse of the current Expr, without
	 * simplifying it. This method makes use of the Negative class.
	 */
	public final Expr negative() {
		/*
		// Optimizations, recursion prevention:
		if (this instanceof Number) {
			if (this instanceof Exponent) {
				Exponent expr = (Exponent)this;
				return new Exponent(expr.base, expr.power, !expr.negative);
			}
			if (this instanceof Integer) {
				Integer expr = (Integer)this;
				if (expr.value == java.lang.Long.MIN_VALUE) {
					throw new ArithmeticException("Overflow while computing the negative of " + this);
				}
				return new Integer(-expr.value);
			}
			if (this instanceof Transcendental) {
				Transcendental expr = (Transcendental)this;
				return new Transcendental(expr.value, !expr.negative);
			}
		}
		if (this instanceof Negative) {
			return ((Negative)this).getArgument();
		}
		*/
		return new Negative(this);
	}
	/**
	 * Returns the multiplicative inverse of the current Expr,
	 * without simplifying it. This method makes use of the
	 * Inverse class.
	 */
	public final Expr inverse() {
		/*
		// Optimizations, recursion prevention:
		if (this instanceof Integer) {
			Integer expr = (Integer)this;
			if (expr.value == 1) return ONE;
			return new Fraction(ONE, expr);
		}
		if (this instanceof Fraction) {
			Fraction expr = (Fraction)this;
			if (expr.numerator.equalTo(ONE)) {
				return expr.denominator;
			}
			return new Fraction(expr.denominator, expr.numerator);
		}
		if (this instanceof Inverse) {
			return ((Inverse)this).getArgument();
		}
		*/
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
	@Override public abstract String toString();
	/**
	 * Default hashCode implementation.
	 */
	@Override public int hashCode() {
		return 1;
	}
	/**
	 * The values of all instance variables
	 * are equal. The Exprs are and will display identically.
	 */
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
	
	public abstract int nodeCount();
	
}
