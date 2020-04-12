package com.apprisingsoftware.cas;

public enum Transcendent {
	PI ("π"),
	E ("e");
	
	private final String name;
	
	private Transcendent(String name) {
		this.name = name;
	}
	
	@Override public String toString() {
		return name;
	}
}
