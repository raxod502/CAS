package cas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class AssociativeFunction extends Function {
	
	public AssociativeFunction(ArrayList<Expr> args) {
		super(args);
	}
	public AssociativeFunction(Expr... args) {
		super(args);
	}
	public AssociativeFunction(ArrayList<Expr> args, int[] derivatives) {
		super(args, derivatives);
	}
	public AssociativeFunction(int[] derivatives, Expr... args) {
		super(derivatives, args);
	}
	
	@Override
	public final void expandFunction() {
		class LR {
			public int l, r;
			public LR(int l, int r) {
				this.l = l; this.r = r;
			}
		}
		List<LR> reducible = new ArrayList<LR>();
		for (int l=0; l<args.size(); l++) {
			for (int r=l+1; r<args.size(); r++) {
				Expr left = args.get(l), right = args.get(r);
				DualFunction df = getDualFunction(left, right);
				df.expand();
				if (df.equivalenceClass.size() > 1) {
					reducible.add(new LR(l, r));
				}
			}
		}
		for (LR lr : reducible) {
			Expr left = args.get(lr.l), right = args.get(lr.r);
			DualFunction df = getDualFunction(left, right);
			try {
				Constructor<? extends AssociativeFunction> cctor = getClass().getConstructor(ArrayList.class);
				for (Expr alternate : df.equivalenceClass) {
					if (alternate != this) {
						ArrayList<Expr> newArgs = new ArrayList<Expr>(args);
						newArgs.set(lr.l, left);
						newArgs.set(lr.r, right);
						AssociativeFunction instance = cctor.newInstance(newArgs);
						equivalenceClass.addExpr(instance);
						instance.expand();
					}
				}
			}
			catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	public void expandAssociativeFunction() {}
	@Override
	public abstract IEquivalenceClass transformExpr();
	
	public Expr derivative(Variable var) {
		ArrayList<Expr> terms = new ArrayList<Expr>();
		for (int i=0; i<args.size()-1; i++) {
			terms.set(i, args.get(i+1));
		}
		try {
			AssociativeFunction slice = getClass().getDeclaredConstructor(ArrayList.class).newInstance(terms);
			return getDualFunction(args.get(0), slice).derivative(var);
		}
		catch (InvocationTargetException e) { e.printStackTrace(); throw new RuntimeException("InvocationTargetException encountered."); }
		catch (InstantiationException e) { e.printStackTrace(); throw new RuntimeException("InstantiationException encountered."); }
		catch (NoSuchMethodException e) { e.printStackTrace(); throw new RuntimeException("NoSuchMethodException encountered."); }
		catch (IllegalAccessException e) { e.printStackTrace(); throw new RuntimeException("IllegalAccessException encountered."); }
	}
	public abstract Expr derivativePartial(int var);
	public abstract Expr antiderivative(Variable var);
	
	public abstract Compare compare(Expr other);
	public abstract Compare isZero();
	public abstract Compare isPositive();
	public abstract Compare isNegative();
	public abstract Compare isReal();
	public abstract Compare isImag();
	
	public final int getMinimumArguments() { return 3; }
	public final int getMaximumArguments() { return -1; }
	public abstract DualFunction getDualFunction(Expr left, Expr right);
	
}
