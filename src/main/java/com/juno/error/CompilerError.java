package com.juno.error;

/**
 * Represents a compiler error with source location and detailed information.
 */
public class CompilerError extends Exception {
    private final ErrorCode errorCode;
    private final int line;
    private final int column;
    private final String sourceFile;
    private final String sourceLine;
    private final String message;
    private final int errorStart;
    private final int errorLength;
    
    // Convenience constructor for simple error creation
    public CompilerError(String message, ErrorCode errorCode, int line, int column) {
        this(errorCode, line, column, "<unknown>", "<unknown>", message, column, 1);
    }
    
    public CompilerError(ErrorCode errorCode, int line, int column, 
                        String sourceFile, String sourceLine, String message) {
        this(errorCode, line, column, sourceFile, sourceLine, message, column, 1);
    }
    
    public CompilerError(ErrorCode errorCode, int line, int column, 
                        String sourceFile, String sourceLine, String message,
                        int errorStart, int errorLength) {
        super(message);
        this.errorCode = errorCode;
        this.line = line;
        this.column = column;
        this.sourceFile = sourceFile;
        this.sourceLine = sourceLine;
        this.message = message;
        this.errorStart = errorStart;
        this.errorLength = errorLength;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public String getSourceFile() {
        return sourceFile;
    }
    
    public String getSourceLine() {
        return sourceLine;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public int getErrorStart() {
        return errorStart;
    }
    
    public int getErrorLength() {
        return errorLength;
    }
}