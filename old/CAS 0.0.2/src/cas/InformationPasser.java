package cas;

import java.util.*;

public final class InformationPasser {
	
	public static final Collection<HashSet<Expr>> exprGroups = new ArrayList<>();
	public static final HashMap<Expr, Boolean> alreadySimplified = new HashMap<>();
	
	private InformationPasser() {}
	
	public static HashSet<Expr> getContainingGroup(Expr expr) {
		for (HashSet<Expr> exprGroup : exprGroups) {
			if (exprGroup.contains(expr)) {
				return exprGroup;
			}
		}
		return null;
	}
	
}
