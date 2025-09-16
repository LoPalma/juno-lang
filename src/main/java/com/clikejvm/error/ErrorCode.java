package com.clikejvm.error;

/**
 * Error codes for the compiler.
 * Categorized by type of issue, not discovery phase.
 * Colors indicate severity: red for errors, yellow for warnings.
 */
public enum ErrorCode {
    // ERRORS (Red)
    
    // Syntax errors - typos, missing tokens, malformed syntax
    BAD_SYNTAX("BadSyntax", true),
    
    // Argument errors - type mismatches, malformed arguments
    BAD_ARG("BadArg", true),
    
    // Arity errors - wrong number of arguments
    BAD_ARITY("BadArity", true),
    
    // Function errors - trying to call something that isn't a function
    BAD_FUN("BadFun", true),
    
    // Undefined errors - referencing something that doesn't exist at all
    UNDEF("Undef", true),
    
    // Implicit cast errors - invalid automatic type conversions
    IMP_CAST("ImpCast", true),
    
    // WARNINGS (Yellow)
    
    // Unused variable warnings
    UNUSED_VAR("UnusedVar", false),
    
    // Deprecated feature warnings
    DEPRECATED("Deprecated", false),
    
    // Potential precision loss in numeric conversions
    PRECISION_LOSS("PrecisionLoss", false),
    
    // Unreachable code warnings
    UNREACHABLE("Unreachable", false);
    
    private final String code;
    private final boolean isError; // true for errors, false for warnings
    
    ErrorCode(String code, boolean isError) {
        this.code = code;
        this.isError = isError;
    }
    
    public String getCode() {
        return code;
    }
    
    public boolean isError() {
        return isError;
    }
    
    public boolean isWarning() {
        return !isError;
    }
    
    @Override
    public String toString() {
        return code;
    }
}