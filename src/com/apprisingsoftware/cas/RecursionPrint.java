package com.apprisingsoftware.cas;

import java.util.ArrayList;

public class RecursionPrint {
	
	private static final boolean DEBUG = true;
	
	public static final int tabLength = 4;
	public static final String tab = "." + repeat(" ", tabLength-1);
	
	private static int recursionDepth = 0;
	private static ArrayList<Object> stack = new ArrayList<>();
	private static Object lastRecent = null;
	
	public static void push(Object obj) {
		stack.add(obj);
	}
	public static void pop() {
		stack.remove(stack.size()-1);
		if (stack.size() == 0) logRawLine("@");
	}
	
	public static void log(String msg) {
		logWithoutNewline(msg + "\n");
	}
	
	public static void logWithoutNewline(String msg) {
		if (stack.size() == 0) {
			throw new UnsupportedOperationException("Cannot log with an empty stack!");
		}
		if (stack.size() == recursionDepth && lastRecent != stack.get(stack.size()-1)) {
			logRawLine(repeat(tab, (recursionDepth-1)) + "| --> " + stack.get(stack.size()-1));
			lastRecent = stack.get(stack.size()-1);
		}
		while (stack.size() > recursionDepth) {
			if (recursionDepth == 0) logRawLine("@");
			else logRawLine(repeat(tab, (recursionDepth-1)) + "+---+");
			recursionDepth++;
			logRawLine(repeat(tab, (recursionDepth-1)) + "| --> " + stack.get(recursionDepth-1).toString());
			lastRecent = stack.get(stack.size()-1);
		}
		while (stack.size() < recursionDepth) {
			logRawLine(repeat(tab, (recursionDepth-2)) + "+---+");
			recursionDepth--;
			logRawLine(repeat(tab, (recursionDepth-1)) + "| --> " + stack.get(recursionDepth-1).toString());
			lastRecent = stack.get(stack.size()-1);
		}
		logRaw(repeat(tab, (recursionDepth-1)) + "| " + msg);
	}
	
	public static void logRawLine(String msg) {
		logRaw(msg + "\n");
	}
	
	public static void logRaw(String msg) {
		if (!DEBUG) return;
		System.out.print(msg);
	}
	
	public static String repeat(String str, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<n; i++) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public static String spaces2(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<n; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
	
}
