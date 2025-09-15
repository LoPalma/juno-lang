package com.clikejvm.lexer;

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
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("break", TokenType.BREAK);
        KEYWORDS.put("continue", TokenType.CONTINUE);
        KEYWORDS.put("struct", TokenType.STRUCT);
        KEYWORDS.put("import", TokenType.IMPORT);
        KEYWORDS.put("module", TokenType.MODULE);
        
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
    private final List<Token> tokens;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOF, "", null, line, column));
        return tokens;
    }

    private void scanToken() {
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
                    throw new RuntimeException("Unexpected character: " + c + " at line " + line + ", column " + column);
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

    private void string() {
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
            throw new RuntimeException("Unterminated string at line " + line);
        }

        advance(); // consume closing quote
        addToken(TokenType.STRING_LITERAL, value.toString());
    }

    private void character() {
        char value;
        
        if (peek() == '\\') {
            advance(); // consume backslash
            char escaped = advance();
            switch (escaped) {
                case 'n': value = '\n'; break;
                case 't': value = '\t'; break;
                case 'r': value = '\r'; break;
                case '\\': value = '\\'; break;
                case '\'': value = '\''; break;
                default: value = escaped; break;
            }
        } else {
            value = advance();
        }

        if (peek() != '\'') {
            throw new RuntimeException("Unterminated character literal at line " + line);
        }
        advance(); // consume closing quote
        
        addToken(TokenType.CHAR_LITERAL, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Look for decimal point
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume '.'
            while (isDigit(peek())) advance();
            
            double value = Double.parseDouble(source.substring(start, current));
            addToken(TokenType.FLOAT_LITERAL, value);
        } else {
            String numberStr = source.substring(start, current);
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
                    throw new RuntimeException("Invalid number literal: " + numberStr + " at line " + line);
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