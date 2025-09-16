package com.clikejvm.error;

/**
 * Utility for formatting compiler errors with source code snippets and visual indicators.
 * Supports colored output for better readability.
 */
public class ErrorReporter {
    
    // ANSI color codes
    private static final String RED = "\033[31m";     // Red for errors
    private static final String YELLOW = "\033[33m";  // Yellow for warnings
    private static final String RESET = "\033[0m";    // Reset color
    private static final String BOLD = "\033[1m";     // Bold text
    
    /**
     * Formats a compiler error in the standard format with colors:
     * 
     * ErrorType, line:    (colored red for errors, yellow for warnings)
     * 
     * line | source code
     *        ^~~ error message
     */
    public static String formatError(CompilerError error) {
        StringBuilder sb = new StringBuilder();
        
        // Choose color based on error type
        String color = error.getErrorCode().isError() ? RED : YELLOW;
        
        // Header: ErrorType, line: (colored)
        sb.append(color).append(BOLD)
          .append(error.getErrorCode().getCode())
          .append(", ")
          .append(error.getLine())
          .append(":")
          .append(RESET)
          .append("\n\n");
        
        // Source line with line number
        sb.append(String.format("%d | %s\n", error.getLine(), error.getSourceLine()));
        
        // Error indicator line (colored)
        String indicator = buildErrorIndicator(error, color);
        sb.append("    ").append(indicator).append(RESET);
        
        return sb.toString();
    }
    
    /**
     * Builds the visual error indicator (^~~ message) positioned correctly under the error.
     */
    private static String buildErrorIndicator(CompilerError error, String color) {
        StringBuilder sb = new StringBuilder();
        
        // Calculate the correct position: line number width + " | " + error position
        int lineNumberWidth = String.valueOf(error.getLine()).length();
        int prefixWidth = lineNumberWidth + 3; // "line | "
        int errorPosition = error.getErrorStart() - 1; // Convert to 0-based (columns are 1-based)
        
        // Add spaces to align with the error position in the source line
        for (int i = 0; i < prefixWidth + errorPosition; i++) {
            sb.append(' ');
        }
        
        // Add the error indicator: ^ followed by ~~ (colored)
        sb.append(color).append('^');
        
        // Always add at least two tildes for visual consistency
        int tildeCount = Math.max(2, error.getErrorLength() - 1);
        for (int i = 0; i < tildeCount; i++) {
            sb.append('~');
        }
        
        // Add the error message
        sb.append(" ").append(error.getMessage());
        
        return sb.toString();
    }
    
    /**
     * Creates a CompilerError for lexical analysis errors.
     */
    public static CompilerError lexError(ErrorCode errorCode, String sourceFile,
                                       String[] sourceLines, int line, int column, 
                                       String message) {
        String sourceLine = (line <= sourceLines.length) ? sourceLines[line - 1] : "";
        return new CompilerError(errorCode, line, column, sourceFile, sourceLine, message);
    }
    
    /**
     * Creates a CompilerError for lexical analysis errors with specific error span.
     */
    public static CompilerError lexError(ErrorCode errorCode, String sourceFile, 
                                       String[] sourceLines, int line, int column, 
                                       String message, int errorStart, int errorLength) {
        String sourceLine = (line <= sourceLines.length) ? sourceLines[line - 1] : "";
        return new CompilerError(errorCode, line, column, sourceFile, sourceLine, 
                               message, errorStart, errorLength);
    }
    
    /**
     * Creates a CompilerError for parsing errors from a token.
     */
    public static CompilerError parseError(ErrorCode errorCode, String sourceFile, 
                                         String[] sourceLines, com.clikejvm.lexer.Token token, 
                                         String message) {
        String sourceLine = (token.getLine() <= sourceLines.length) ? 
                           sourceLines[token.getLine() - 1] : "";
        
        // Calculate error span based on token
        int errorStart = token.getColumn();
        int errorLength = token.getLexeme().length();
        if (errorLength == 0) errorLength = 1; // For EOF or missing tokens
        
        return new CompilerError(errorCode, token.getLine(), token.getColumn(), 
                               sourceFile, sourceLine, message, errorStart, errorLength);
    }
    
    /**
     * Creates a CompilerError for semantic analysis errors.
     */
    public static CompilerError semanticError(ErrorCode errorCode, String sourceFile, 
                                            String[] sourceLines, int line, int column,
                                            String message, int errorStart, int errorLength) {
        String sourceLine = (line <= sourceLines.length) ? sourceLines[line - 1] : "";
        return new CompilerError(errorCode, line, column, sourceFile, sourceLine, 
                               message, errorStart, errorLength);
    }
}