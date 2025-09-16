package com.clikejvm.parser;

import com.clikejvm.ast.*;
import com.clikejvm.error.CompilerError;
import com.clikejvm.error.ErrorCode;
import com.clikejvm.error.ErrorReporter;
import com.clikejvm.lexer.Token;
import com.clikejvm.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser for the C-like language with sophisticated error reporting.
 */
public class Parser {
    private final List<Token> tokens;
    private final String sourceFile;
    private final String[] sourceLines;
    private int current = 0;

    public Parser(List<Token> tokens, String sourceFile, String[] sourceLines) {
        this.tokens = tokens;
        this.sourceFile = sourceFile;
        this.sourceLines = sourceLines;
    }

    public Program parseProgram() throws CompilerError {
        // Simple implementation that works with existing AST nodes
        // TODO: Implement full parser with existing AST structure
        return new Program(new ArrayList<>(), 1, 1);
    }

    // ========== UTILITY METHODS ==========
    
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    private Token consume(TokenType type, String message) throws CompilerError {
        if (check(type)) return advance();
        throw error(ErrorCode.BAD_SYNTAX, peek(), message);
    }
    
    private Token consumeType(String message) throws CompilerError {
        if (isTypeToken(peek().getType())) {
            return advance();
        }
        throw error(ErrorCode.BAD_SYNTAX, peek(), message);
    }
    
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    private boolean isTypeToken(TokenType type) {
        return type == TokenType.INT || type == TokenType.FLOAT || type == TokenType.CHAR || 
               type == TokenType.STRING || type == TokenType.BOOL || type == TokenType.VOID ||
               type == TokenType.BYTE || type == TokenType.UBYTE || type == TokenType.SHORT ||
               type == TokenType.USHORT || type == TokenType.UINT || type == TokenType.ULONG ||
               type == TokenType.LONG || type == TokenType.DOUBLE;
    }
    
    private boolean checkFunctionDecl() {
        // Look ahead to see if this is type name ( ... indicating function
        int ahead = current + 1;
        if (ahead < tokens.size() && tokens.get(ahead).getType() == TokenType.IDENTIFIER) {
            ahead++;
            if (ahead < tokens.size() && tokens.get(ahead).getType() == TokenType.LEFT_PAREN) {
                return true;
            }
        }
        return false;
    }
    
    private CompilerError error(ErrorCode errorCode, Token token, String message) {
        return ErrorReporter.parseError(errorCode, sourceFile, sourceLines, token, message);
    }
    
    private void synchronize() {
        advance();
        
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;
            
            switch (peek().getType()) {
                case IF:
                case FOR:
                case WHILE:
                case RETURN:
                case INT:
                case FLOAT:
                case CHAR:
                case STRING:
                case BOOL:
                case VOID:
                    return;
            }
            
            advance();
        }
    }
    
}
