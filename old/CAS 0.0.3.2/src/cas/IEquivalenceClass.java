package cas;

import java.util.List;

public interface IEquivalenceClass extends Iterable<Expr> {
	
	boolean contains(Expr expr);
	Number getNumber();
	Expr getSimplest();
	boolean addExpr(Expr expr);
	List<Expr> toList();
	
}
