package cas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

final class EquivalenceClass implements IEquivalenceClass {
	
	private HashSet<Expr> exprs = new HashSet<Expr>();
	
	static HashSet<EquivalenceClass> equivalenceClasses = new HashSet<EquivalenceClass>();
	
	private EquivalenceClass() {}
	private EquivalenceClass(Expr expr) {
		exprs.add(expr);
	}
	
	static EquivalenceClass getEquivalenceClass(Expr expr) {
		for (EquivalenceClass ec : equivalenceClasses) {
			if (ec.contains(expr)) {
				return ec;
			}
		}
		EquivalenceClass ec = new EquivalenceClass(expr);
		equivalenceClasses.add(ec);
		return ec;
	}
	
	public boolean contains(Expr expr) {
		return exprs.contains(expr);
	}
	
	public Number getNumber() {
		for (Expr expr : exprs) {
			if (expr instanceof Number) {
				return (Number)expr;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public Expr getSimplest() {
		throw new UnsupportedOperationException();
	}
	
	public boolean addExpr(Expr expr) {
		if (expr == null) throw new NullPointerException();
		boolean success = exprs.add(expr);
		for (EquivalenceClass ec : equivalenceClasses) {
			if (ec != this && ec.contains(expr)) {
				success = false;
				this.combine(ec);
			}
		}
		return success;
	}
	public void combine(EquivalenceClass ec) {
		// We have no way of changing the reference in Expr objects that
		// are using the obsolete EquivalenceClass, so we can just make
		// them identical by setting the exprs reference from one to point
		// to the other.
		exprs.addAll(ec.exprs);
		ec.exprs = exprs;
		equivalenceClasses.remove(ec);
	}
	
	public List<Expr> toList() {
		return new ArrayList<Expr>(exprs);
	}
	
	public Iterator<Expr> iterator() {
		return exprs.iterator();
	}
	
	public int size() {
		return exprs.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		Iterator<Expr> iter = exprs.iterator();
		sb.append(iter.next().toString());
		while (iter.hasNext()) {
			sb.append(", ").append(iter.next().toString());
		}
		return sb.append("}").toString();
	}
	
}
