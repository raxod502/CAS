package com.apprisingsoftware.cas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

final class EquivalenceClass implements Iterable<Expr>, Copyable {
	
	// All equivalence classes in the current session's knowledge-base
	private static HashSet<EquivalenceClass> equivalenceClasses = new HashSet<EquivalenceClass>();
	
	// The exprs in a single equivalence class
	private HashSet<Expr> exprs = new HashSet<Expr>();
	
	// Constructors
	private EquivalenceClass() {}
	private EquivalenceClass(Expr expr) {
		add(expr);
	}
	
	public static boolean hasEquivalenceClass(Expr expr) {
		for (EquivalenceClass ec : equivalenceClasses) {
			if (ec.contains(expr)) {
				return true;
			}
		}
		return false;
	}
	public static List<EquivalenceClass> getDuplicateEquivalenceClasses(Expr expr, EquivalenceClass designatedClass) {
		ArrayList<EquivalenceClass> dupes = new ArrayList<>();
		if (expr.equivalenceClass() == null) return dupes;
		for (EquivalenceClass ec : equivalenceClasses) {
			if (ec != designatedClass && ec.contains(expr)) {
				dupes.add(ec);
			}
		}
		return dupes;
	}
	public static EquivalenceClass newEquivalenceClass(Expr expr) {
		EquivalenceClass ec = new EquivalenceClass(expr);
		equivalenceClasses.add(ec);
		return ec;
	}
	public static EquivalenceClass getEquivalenceClass(Expr expr) {
		for (EquivalenceClass ec : equivalenceClasses) {
			if (ec.contains(expr)) return ec;
		}
		throw new AssertionError();
	}
	
	public boolean add(Expr expr) {
/*		if (expr instanceof MaybeUseful) {
			Expr fixed = ((MaybeUseful)expr).makeUseful();
			fixed.equivalenceClass = this;
			return exprs.add(fixed);
		}
		else {
*/			return exprs.add(expr);
//		}
	}
	public void absorb(EquivalenceClass ec) {
		// We have no way of changing the reference in Expr objects that
		// are using the obsolete EquivalenceClass, so we can just make
		// them identical by setting the exprs reference from one to point
		// to the other.
		exprs.addAll(ec.exprs);
		ec.exprs = exprs;
		equivalenceClasses.remove(ec);
		// If one EquivalenceClass is absorbed by another, and then it
		// absorbs a third EquivalenceClass, then it will remove the
		// third one from the equivalenceClasses set; now neither version
		// of the class is in the set -> big problems.
		// So:
		equivalenceClasses.add(this);
	}
	
	public Expr getSimplest() {
		ArrayList<Expr> minExprs = new ArrayList<Expr>();
		
		ArrayList<Expr> numExprs = new ArrayList<Expr>();
		for (Expr expr : exprs) {
			if (expr instanceof Number) {
				numExprs.add(expr);
			}
		}
		HashSet<Expr> validExprs = numExprs.size() > 0 ? new HashSet<>(numExprs) : exprs;
		
		int minNodes = java.lang.Integer.MAX_VALUE;
		for (Expr expr : validExprs) {
			if (expr.nodeCount() < minNodes) {
				minNodes = expr.nodeCount();
				minExprs = new ArrayList<>();
			}
			if (expr.nodeCount() <= minNodes) {
				minExprs.add(expr);
			}
		}
		return minExprs.get(0);
	}
	
	public boolean contains(Expr expr) {
/*		if (expr instanceof MaybeUseful) {
			return exprs.contains(((MaybeUseful)expr).makeUseful());
		}
		else {
*/			return exprs.contains(expr);
//		}
	}
	
	public List<Expr> toList() {
		ArrayList<Expr> list = new ArrayList<Expr>();
		for (Expr expr : this) {
			list.add(expr);
		}
		return list;
	}
	
	public Expr getOnlyExpr() {
		if (exprs.size() == 1) return exprs.iterator().next();
		throw new AssertionError();
	}
	
	@Override public Iterator<Expr> iterator() {
		return exprs.iterator();
	}
	
	public int size() {
		return exprs.size();
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder("{");
		Iterator<Expr> iter = this.iterator();
		sb.append(iter.next().toString());
		while (iter.hasNext()) {
			sb.append(", ").append(iter.next().toString());
		}
		return sb.append("}").toString();
	}
	
	@Override public EquivalenceClass copy() {
		EquivalenceClass copy = new EquivalenceClass();
		copy.exprs = new HashSet<Expr>(exprs);
		return copy;
	}
	
}
