package cas;

public enum Compare {
	EQUAL(), GREATER(), LESSER(), UNEQUAL(), GREATER_OR_EQUAL(), LESSER_OR_EQUAL(), UNKNOWN(), INDETERMINATE(),
	YES(), NO(), MAYBE();
	
	Compare() {}
	public Compare and(Compare... comps) {
		return _and(_and(comps), this);
	}
	public Compare or(Compare... comps) {
		return _or(_or(comps), this);
	}
	public Compare xor(Compare other) {
		return this == MAYBE || other == MAYBE ? MAYBE :
			this != other ? YES : NO;
	}
	public Compare not() {
		return _not(this);
	}
	
	public static Compare _and(Compare... comps) {
		if (comps.length == 0) throw new IllegalArgumentException("Need at least one argument.");
		if (comps.length == 1) return comps[0];
		boolean yes = true, maybe = false;
		for (Compare comp : comps) {
			if (comp != YES) yes = false;
			if (comp == NO) return NO;
			if (comp == MAYBE) maybe = true;
		}
		if (yes) return YES;
		if (maybe) return MAYBE;
		throw new IllegalStateException("Something went wrong in Compare.and.");
	}
	public static Compare _or(Compare... comps) {
		if (comps.length == 0) throw new IllegalArgumentException("Need at least one argument.");
		if (comps.length == 1) return comps[0];
		boolean no = true, maybe = false;
		for (Compare comp : comps) {
			if (comp != NO) no = false;
			if (comp == YES) return YES;
			if (comp == MAYBE) maybe = true;
		}
		if (no) return NO;
		if (maybe) return MAYBE;
		throw new IllegalStateException("Something went wrong in Compare.or.");
	}
	public static Compare _not(Compare comp) {
		switch (comp) {
		case YES: return NO;
		case NO: return YES;
		case MAYBE: return MAYBE;
		default: throw new IllegalStateException("Something went wrong in Compare.not.");
		}
	}
}
