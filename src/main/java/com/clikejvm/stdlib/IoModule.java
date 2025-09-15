package com.clikejvm.stdlib;

/**
 * Standard library module for input/output operations.
 * This class provides static methods that will be called from compiled C-like code.
 * 
 * In C-like code:
 * import io;
 * io.print("Hello");
 * 
 * Compiles to JVM bytecode:
 * invokestatic com/clikejvm/stdlib/IoModule/print(Ljava/lang/String;)V
 */
public class IoModule {
    
    /**
     * Print a string to standard output without newline.
     * @param message the string to print
     */
    public static void print(String message) {
        System.out.print(message);
    }
    
    /**
     * Print a string to standard output with newline.
     * @param message the string to print
     */
    public static void println(String message) {
        System.out.println(message);
    }
    
    /**
     * Print an integer to standard output without newline.
     * @param value the integer to print
     */
    public static void print(int value) {
        System.out.print(value);
    }
    
    /**
     * Print an integer to standard output with newline.
     * @param value the integer to print
     */
    public static void println(int value) {
        System.out.println(value);
    }
    
    /**
     * Print a float to standard output without newline.
     * @param value the float to print
     */
    public static void print(float value) {
        System.out.print(value);
    }
    
    /**
     * Print a float to standard output with newline.
     * @param value the float to print
     */
    public static void println(float value) {
        System.out.println(value);
    }
    
    /**
     * Print a boolean to standard output without newline.
     * @param value the boolean to print
     */
    public static void print(boolean value) {
        System.out.print(value);
    }
    
    /**
     * Print a boolean to standard output with newline.
     * @param value the boolean to print
     */
    public static void println(boolean value) {
        System.out.println(value);
    }
    
    /**
     * Read a line from standard input.
     * @return the line read, or null if EOF
     */
    public static String readLine() {
        try {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}