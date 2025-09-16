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
     * Formats a compiler error in Elm-style verbose format:
     * 
     * -- ERROR TYPE ------------------------------------------------- file.cl
     * 
     * I got stuck while parsing...
     * 
     * line | source code
     *        ^
     * Detailed explanation with suggestions.
     */
    public static String formatError(CompilerError error) {
        StringBuilder sb = new StringBuilder();
        
        // Choose color based on error type
        String color = error.getErrorCode().isError() ? RED : YELLOW;
        
        // Elm-style header with dashes
        sb.append(color).append(BOLD).append("-- ")
          .append(error.getErrorCode().getCode().toUpperCase())
          .append(" ")
          .append("-".repeat(Math.max(1, 50 - error.getErrorCode().getCode().length())))
          .append(" ")
          .append(error.getSourceFile())
          .append(RESET)
          .append("\n\n");
        
        // Contextual introduction
        String intro = getElmStyleIntro(error);
        sb.append(intro).append("\n\n");
        
        // Source line with line number (no extra padding)
        sb.append(String.format("%d| %s\n", error.getLine(), error.getSourceLine()));
        
        // Error indicator line (correctly positioned)
        String indicator = buildElmStyleIndicator(error, color);
        sb.append(indicator).append(RESET).append("\n");
        
        // Detailed explanation
        String explanation = getElmStyleExplanation(error);
        sb.append(explanation);
        
        return sb.toString();
    }
    
    /**
     * Builds Elm-style error indicator positioned correctly under the error.
     */
    private static String buildElmStyleIndicator(CompilerError error, String color) {
        StringBuilder sb = new StringBuilder();
        
        // Calculate spacing: line number + "| " + position in source
        int lineNumberWidth = String.valueOf(error.getLine()).length();
        int prefixWidth = lineNumberWidth + 2; // "line| " (no extra space)
        int errorPosition = error.getErrorStart() - 1; // Convert to 0-based
        
        // Add spaces to align with the error position
        for (int i = 0; i < prefixWidth + errorPosition; i++) {
            sb.append(' ');
        }
        
        // Add simple colored caret (Elm style)
        sb.append(color).append('^');
        
        return sb.toString();
    }
    
    /**
     * Creates Elm-style contextual introduction based on error type.
     */
    private static String getElmStyleIntro(CompilerError error) {
        switch (error.getErrorCode()) {
            case BAD_SYNTAX:
                if (error.getMessage().contains("unterminated")) {
                    return "I got stuck while parsing a character or string literal:";
                } else if (error.getMessage().contains("multiple decimal points")) {
                    return "I found a malformed number while parsing:";
                } else if (error.getMessage().contains("too long")) {
                    return "I found a character literal that's too long:";
                } else if (error.getMessage().contains("empty")) {
                    return "I found an empty character literal:";
                } else {
                    return "I got stuck while parsing this syntax:";
                }
            case BAD_ARG:
                return "I found a type mismatch in this function call:";
            case BAD_ARITY:
                return "This function call has the wrong number of arguments:";
            case BAD_FUN:
                return "I'm trying to call something that isn't a function:";
            case UNDEF:
                return "I can't find this definition:";
            case IMP_CAST:
                return "I can't automatically convert between these types:";
            default:
                return "I encountered an issue while compiling:";
        }
    }
    
    /**
     * Creates Elm-style detailed explanation with suggestions.
     */
    private static String getElmStyleExplanation(CompilerError error) {
        switch (error.getErrorCode()) {
            case BAD_SYNTAX:
                if (error.getMessage().contains("unterminated character")) {
                    return "Character literals in our language must be enclosed in single quotes and contain\n" +
                           "exactly one character. Here are some examples:\n\n" +
                           "    char letter = 'a';     -- good\n" +
                           "    char digit = '5';      -- good\n" +
                           "    char escape = '\\n';    -- good (escape sequence)\n\n" +
                           "Make sure to close the character literal with a single quote (')!";
                }
                if (error.getMessage().contains("unterminated string")) {
                    return "String literals must be enclosed in double quotes. Here are some examples:\n\n" +
                           "    string greeting = \"Hello\";     -- good\n" +
                           "    string empty = \"\";            -- good\n" +
                           "    string multi = \"Hello\\nWorld\"; -- good (with escape)\n\n" +
                           "Make sure to close the string literal with a double quote (\")!";
                }
                if (error.getMessage().contains("too long")) {
                    return "Character literals can only contain a single character or escape sequence.\n" +
                           "If you need multiple characters, use a string instead:\n\n" +
                           "    string word = \"hello\";   -- use this for multiple characters\n" +
                           "    char letter = 'h';      -- use this for single characters";
                }
                if (error.getMessage().contains("multiple decimal points")) {
                    return "Numbers can only have one decimal point. Here are some valid examples:\n\n" +
                           "    float pi = 3.14;        -- good\n" +
                           "    float small = 0.5;      -- good\n" +
                           "    int whole = 42;         -- good (no decimal point)\n\n" +
                           "Did you mean to write something like 123.456 instead?";
                }
                return "Please check the syntax and try again.";
            
            case BAD_ARG:
                return "The function expects a different type than what you provided.\n" +
                       "Try checking the function signature or converting the argument to the expected type.";
            
            case IMP_CAST:
                return "Our language requires explicit type conversions for safety.\n" +
                       "You can convert types using cast expressions like:\n\n" +
                       "    string s = string(myChar);    -- convert char to string\n" +
                       "    int i = int(myFloat);         -- convert float to int";
            
            default:
                return "Please check your code and try again.";
        }
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