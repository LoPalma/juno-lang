package com.juno.stdlib;

/**
 * Standard library module for mathematical operations.
 * This class provides static methods that will be called from compiled C-like code.
 * 
 * In C-like code:
 * import math;
 * math.sqrt(42.0);
 * 
 * Compiles to JVM bytecode:
 * invokestatic com/clikejvm/stdlib/MathModule/sqrt(D)D
 */
public class MathModule {
    
    // Mathematical constants
    public static final double PI = Math.PI;
    public static final double E = Math.E;
    
    /**
     * Return the square root of a number.
     * @param x the number
     * @return the square root of x
     */
    public static double sqrt(double x) {
        return Math.sqrt(x);
    }
    
    /**
     * Return the sine of an angle in radians.
     * @param x the angle in radians
     * @return the sine of x
     */
    public static double sin(double x) {
        return Math.sin(x);
    }
    
    /**
     * Return the cosine of an angle in radians.
     * @param x the angle in radians
     * @return the cosine of x
     */
    public static double cos(double x) {
        return Math.cos(x);
    }
    
    /**
     * Return the tangent of an angle in radians.
     * @param x the angle in radians
     * @return the tangent of x
     */
    public static double tan(double x) {
        return Math.tan(x);
    }
    
    /**
     * Return the natural logarithm of a number.
     * @param x the number
     * @return the natural logarithm of x
     */
    public static double log(double x) {
        return Math.log(x);
    }
    
    /**
     * Return the base-10 logarithm of a number.
     * @param x the number
     * @return the base-10 logarithm of x
     */
    public static double log10(double x) {
        return Math.log10(x);
    }
    
    /**
     * Return e raised to the power of x.
     * @param x the exponent
     * @return e^x
     */
    public static double exp(double x) {
        return Math.exp(x);
    }
    
    /**
     * Return x raised to the power of y.
     * @param x the base
     * @param y the exponent
     * @return x^y
     */
    public static double pow(double x, double y) {
        return Math.pow(x, y);
    }
    
    /**
     * Return the absolute value of a number.
     * @param x the number
     * @return the absolute value of x
     */
    public static double abs(double x) {
        return Math.abs(x);
    }
    
    /**
     * Return the absolute value of an integer.
     * @param x the integer
     * @return the absolute value of x
     */
    public static int abs(int x) {
        return Math.abs(x);
    }
    
    /**
     * Return the minimum of two numbers.
     * @param a first number
     * @param b second number
     * @return the minimum of a and b
     */
    public static double min(double a, double b) {
        return Math.min(a, b);
    }
    
    /**
     * Return the minimum of two integers.
     * @param a first integer
     * @param b second integer
     * @return the minimum of a and b
     */
    public static int min(int a, int b) {
        return Math.min(a, b);
    }
    
    /**
     * Return the maximum of two numbers.
     * @param a first number
     * @param b second number
     * @return the maximum of a and b
     */
    public static double max(double a, double b) {
        return Math.max(a, b);
    }
    
    /**
     * Return the maximum of two integers.
     * @param a first integer
     * @param b second integer
     * @return the maximum of a and b
     */
    public static int max(int a, int b) {
        return Math.max(a, b);
    }
    
    /**
     * Return the floor of a number (largest integer <= x).
     * @param x the number
     * @return the floor of x
     */
    public static double floor(double x) {
        return Math.floor(x);
    }
    
    /**
     * Return the ceiling of a number (smallest integer >= x).
     * @param x the number
     * @return the ceiling of x
     */
    public static double ceil(double x) {
        return Math.ceil(x);
    }
    
    /**
     * Round a number to the nearest integer.
     * @param x the number
     * @return the rounded value of x
     */
    public static long round(double x) {
        return Math.round(x);
    }
}