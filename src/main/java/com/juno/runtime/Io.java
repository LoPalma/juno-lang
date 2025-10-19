package com.juno.runtime;

import java.io.*;
import java.util.Scanner;

/**
 * Juno runtime I/O module providing print, scan, and file operations.
 * Accessible from Juno code as Io.print(), Io.scan(), etc.
 */
public class Io {
	private static final Scanner stdin = new Scanner(System.in);

	// ===== BASIC I/O FUNCTIONS =====

	/**
	 * Print a message to stdout without newline.
	 */
	public static void print(String message) {
		System.out.print(message);
	}

	/**
	 * Print a message to stdout with newline.
	 */
	public static void println(String message) {
		System.out.println(message);
	}

	/**
	 * Read a line from stdin and return as string.
	 * Always returns a string - cast needed for other types.
	 */
	public static String scan() {
		return stdin.nextLine();
	}

	/**
	 * Print a message to stderr (error reporting).
	 */
	public static void report(String message) {
		System.err.println(message);
	}


	/**
	 * Standard input stream handle.
	 */
	public static InputStream stdin() {
		return System.in;
	}

	/**
	 * Standard output stream handle.
	 */
	public static PrintStream stdout() {
		return System.out;
	}

	/**
	 * Standard error stream handle.
	 */
	public static PrintStream stderr() {
		return System.err;
	}

}