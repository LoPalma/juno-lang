package com.juno.lexer;

import com.juno.error.CompilerError;
import com.juno.error.ErrorCode;
import com.juno.error.ErrorCollector;
import com.juno.error.ErrorReporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lexical analyzer for the C-like language.
 */
public class Lexer {
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
        KEYWORDS.put("int", TokenType.INT);
        KEYWORDS.put("float", TokenType.FLOAT);
        KEYWORDS.put("char", TokenType.CHAR);
        KEYWORDS.put("string", TokenType.STRING);
        KEYWORDS.put("bool", TokenType.BOOL);
        KEYWORDS.put("void", TokenType.VOID);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("in", TokenType.IN);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("break", TokenType.BREAK);
        KEYWORDS.put("continue", TokenType.CONTINUE);
        KEYWORDS.put("struct", TokenType.STRUCT);
        KEYWORDS.put("public", TokenType.PUBLIC);
        KEYWORDS.put("import", TokenType.IMPORT);
        KEYWORDS.put("module", TokenType.MODULE);
        KEYWORDS.put("opt", TokenType.OPTIONAL);
        KEYWORDS.put("auto", TokenType.AUTO);
        KEYWORDS.put("any", TokenType.ANY);
        KEYWORDS.put("type", TokenType.TYPE);
        KEYWORDS.put("variant", TokenType.VARIANT);
        KEYWORDS.put("match", TokenType.MATCH);
        KEYWORDS.put("case", TokenType.CASE);
        KEYWORDS.put("default", TokenType.DEFAULT);
        
        // Extended type keywords
        KEYWORDS.put("byte", TokenType.BYTE);
        KEYWORDS.put("ubyte", TokenType.UBYTE);
        KEYWORDS.put("short", TokenType.SHORT);
        KEYWORDS.put("ushort", TokenType.USHORT);
        KEYWORDS.put("uint", TokenType.UINT);
        KEYWORDS.put("ulong", TokenType.ULONG);
        KEYWORDS.put("long", TokenType.LONG);
        KEYWORDS.put("double", TokenType.DOUBLE);
    }
    
    private final String source;
    private final String[] sourceLines;
    private final String sourceFile;
    private final List<Token> tokens;
    private final ErrorCollector errorCollector;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this(source, "<input>");
    }
    
    public Lexer(String source, String sourceFile, ErrorCollector errorCollector) {
        this.source = source;
        this.sourceFile = sourceFile;
        this.sourceLines = source.split("\n");
        this.tokens = new ArrayList<>();
        this.errorCollector = errorCollector;
    }
    
    public Lexer(String source, String sourceFile) {
        this(source, sourceFile, new ErrorCollector());
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            try {
                scanToken();
            } catch (CompilerError e) {
                errorCollector.addError(e);
                // Continue tokenizing after error
                if (current == start) {
                    advance(); // Avoid infinite loop
                }
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "", null, line, column));
        return tokens;
    }
    
    public ErrorCollector getErrorCollector() {
        return errorCollector;
    }

    private void scanToken() throws CompilerError {
        char c = advance();
        
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                // Skip whitespace
                break;
            case '\n':
                line++;
                column = 1;
                break;
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '[':
                addToken(TokenType.LEFT_BRACKET);
                break;
            case ']':
                addToken(TokenType.RIGHT_BRACKET);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '*':
                addToken(TokenType.MULTIPLY);
                break;
            case '%':
                addToken(TokenType.MODULO);
                break;
            case '-':
                addToken(match('>') ? TokenType.ARROW : TokenType.MINUS);
                break;
            case '/':
                if (match('/')) {
                    // Line comment
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // Block comment
                    blockComment();
                } else {
                    addToken(TokenType.DIVIDE);
                }
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUALS : TokenType.ASSIGN);
                break;
            case '!':
                addToken(match('=') ? TokenType.NOT_EQUALS : TokenType.LOGICAL_NOT);
                break;
            case '<':
                if (match('=')) {
                    addToken(TokenType.LESS_EQUAL);
                } else if (match('<')) {
                    addToken(TokenType.LEFT_SHIFT);
                } else {
                    addToken(TokenType.LESS_THAN);
                }
                break;
            case '>':
                if (match('=')) {
                    addToken(TokenType.GREATER_EQUAL);
                } else if (match('>')) {
                    addToken(TokenType.RIGHT_SHIFT);
                } else {
                    addToken(TokenType.GREATER_THAN);
                }
                break;
            case '&':
                addToken(match('&') ? TokenType.LOGICAL_AND : TokenType.BITWISE_AND);
                break;
            case '|':
                addToken(match('|') ? TokenType.LOGICAL_OR : TokenType.BITWISE_OR);
                break;
            case '^':
                addToken(TokenType.BITWISE_XOR);
                break;
            case '~':
                addToken(TokenType.BITWISE_NOT);
                break;
            case '"':
                string();
                break;
            case '\'':
                character();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                                line, column, "unexpected character '" + c + "'");
                }
                break;
        }
    }

    private void blockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance(); // consume '*'
                advance(); // consume '/'
                break;
            }
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }
    }

    private void string() throws CompilerError {
        StringBuilder value = new StringBuilder();
        
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            if (peek() == '\\') {
                advance(); // consume backslash
                char escaped = advance();
                switch (escaped) {
                    case 'n': value.append('\n'); break;
                    case 't': value.append('\t'); break;
                    case 'r': value.append('\r'); break;
                    case '\\': value.append('\\'); break;
                    case '"': value.append('"'); break;
                    default: value.append(escaped); break;
                }
            } else {
                value.append(advance());
            }
        }

        if (isAtEnd()) {
            // Find the line where the string started
            int startLine = 1;
            int startColumn = 1;
            for (int i = 0; i < start; i++) {
                if (source.charAt(i) == '\n') {
                    startLine++;
                    startColumn = 1;
                } else {
                    startColumn++;
                }
            }
            
            // Report error but add a partial string token for recovery
            throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                        startLine, startColumn, "unterminated string literal", 
                                        startColumn, 1);
        }

        advance(); // consume closing quote
        addToken(TokenType.STRING_LITERAL, value.toString());
    }

    private void character() throws CompilerError {
        StringBuilder content = new StringBuilder();
        int startLine = line;
        int startColumn = column - 1; // Adjust for the opening quote
        
        // Read everything until closing quote or end of file
        while (peek() != '\'' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            if (peek() == '\\') {
                advance(); // consume backslash
                if (isAtEnd()) break;
                char escaped = advance();
                switch (escaped) {
                    case 'n': content.append('\n'); break;
                    case 't': content.append('\t'); break;
                    case 'r': content.append('\r'); break;
                    case '\\': content.append('\\'); break;
                    case '\'': content.append('\''); break;
                    default: content.append(escaped); break;
                }
            } else {
                content.append(advance());
            }
        }

        // Check if we reached end of file without closing quote
        if (isAtEnd()) {
            throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                        startLine, startColumn, "unterminated character literal", 
                                        startColumn, 1);
        }
        
        advance(); // consume closing quote
        
        // Validate character literal length
        if (content.isEmpty()) {
            throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                        startLine, startColumn, "empty character literal", 
                                        startColumn, current - start);
        } else if (content.length() > 1) {
            throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                        startLine, startColumn, "character literal too long (contains " + content.length() + " characters)", 
                                        startColumn, current - start);
        }
        
        addToken(TokenType.CHAR_LITERAL, content.charAt(0));
    }

    private void number() throws CompilerError {
        boolean hasDecimal = false;
        
        while (isDigit(peek())) advance();

        // Look for decimal point
        if (peek() == '.' && isDigit(peekNext())) {
            hasDecimal = true;
            advance(); // consume '.'
            while (isDigit(peek())) advance();
            
            // Check for additional decimal points (invalid)
            if (peek() == '.' && isDigit(peekNext())) {
                // Found another decimal point - this is an error
                int errorStart = column - 1;
                while (peek() == '.' || isDigit(peek())) {
                    advance(); // consume the malformed part
                }
                
                String invalidNumber = source.substring(start, current);
                throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                            line, start, "invalid number literal '" + invalidNumber + "' (multiple decimal points)", 
                                            start, invalidNumber.length());
            }
        }
        
        String numberStr = source.substring(start, current);
        
        if (hasDecimal) {
            try {
                double value = Double.parseDouble(numberStr);
                addToken(TokenType.FLOAT_LITERAL, value);
            } catch (NumberFormatException e) {
                throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                            line, start, "invalid float literal '" + numberStr + "'", 
                                            start, numberStr.length());
            }
        } else {
            try {
                // Try to parse as int first
                int intValue = Integer.parseInt(numberStr);
                addToken(TokenType.INTEGER_LITERAL, intValue);
            } catch (NumberFormatException e) {
                try {
                    // If too large for int, parse as long
                    long longValue = Long.parseLong(numberStr);
                    addToken(TokenType.INTEGER_LITERAL, longValue);
                } catch (NumberFormatException e2) {
                    throw ErrorReporter.lexError(ErrorCode.BAD_SYNTAX, sourceFile, sourceLines, 
                                                line, start, "invalid number literal '" + numberStr + "'", 
                                                start, numberStr.length());
                }
            }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
        
        if (type == TokenType.TRUE) {
            addToken(type, true);
        } else if (type == TokenType.FALSE) {
            addToken(type, false);
        } else {
            addToken(type);
        }
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        column++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char advance() {
        column++;
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, column - text.length()));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}