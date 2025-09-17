package com.clikejvm.ast;

// No need to import ast classes since we're in the same package
import com.clikejvm.error.CompilerError;
import com.clikejvm.error.ErrorCode;
import com.clikejvm.error.ErrorCollector;
import com.clikejvm.error.ErrorReporter;
import com.clikejvm.lexer.Token;
import com.clikejvm.lexer.TokenType;
import com.clikejvm.types.PrimitiveType;
import com.clikejvm.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Recursive descent parser for the C-like language with sophisticated error reporting.
 */
public class Parser {
    private final List<Token> tokens;
    private final String sourceFile;
    private final String[] sourceLines;
    private final ErrorCollector errorCollector;
    private int current = 0;

    public Parser(List<Token> tokens, String sourceFile, String[] sourceLines, ErrorCollector errorCollector) {
        this.tokens = tokens;
        this.sourceFile = sourceFile;
        this.sourceLines = sourceLines;
        this.errorCollector = errorCollector;
    }

    public Program parseProgram() {
        List<Statement> statements = new ArrayList<>();
        
        while (!isAtEnd()) {
            try {
                Statement stmt = parseStatement();
                if (stmt != null) {
                    statements.add(stmt);
                }
            } catch (CompilerError e) {
                // Add error to collector and synchronize
                errorCollector.addError(e);
                synchronize();
            }
        }
        
        return new Program(statements, 1, 1);
    }

    // ========== PARSING METHODS ==========
    
    /**
     * Parse a top-level statement.
     */
    private Statement parseStatement() throws CompilerError {
        // Handle import statements
        if (match(TokenType.IMPORT)) {
            return parseImportStatement();
        }
        
        // Handle variable declarations (type identifier ...)
        if (isTypeToken(peek().getType())) {
            return parseVariableDeclaration();
        }
        
        throw error(ErrorCode.BAD_SYNTAX, peek(), "Expected import statement or variable declaration.");
    }
    
    private ImportStatement parseImportStatement() throws CompilerError {
        Token startToken = previous();
        Token moduleToken = consumeModuleName("Expected module name after 'import'.");
        String moduleName = moduleToken.getLexeme();
        
        List<String> importedItems = null;
        
        // Check for specific imports: import module.{item1, item2}
        if (match(TokenType.DOT)) {
            consume(TokenType.LEFT_BRACE, "Expected '{' after module name in selective import.");
            importedItems = new ArrayList<>();
            
            do {
                Token item = consumeModuleName("Expected import item name.");
                importedItems.add(item.getLexeme());
            } while (match(TokenType.COMMA));
            
            consume(TokenType.RIGHT_BRACE, "Expected '}' after import list.");
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after import statement.");
        return new ImportStatement(moduleName, importedItems, startToken.getLine(), startToken.getColumn());
    }
    
    private VariableDeclaration parseVariableDeclaration() throws CompilerError {
        Token typeToken = consumeType("Expected type.");
        Type type = getTypeFromToken(typeToken);
        
        Token nameToken = consume(TokenType.IDENTIFIER, "Expected variable name after type.");
        String name = nameToken.getLexeme();
        
        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration.");
        return new VariableDeclaration(type, name, initializer, false, typeToken.getLine(), typeToken.getColumn());
    }
    
    // ========== EXPRESSION PARSING ==========
    
    private Expression parseExpression() throws CompilerError {
        return parseAssignment();
    }
    
    private Expression parseAssignment() throws CompilerError {
        Expression expr = parseAdditive();
        
        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            Expression value = parseAssignment();
            return new AssignmentExpression(expr, value, equals.getLine(), equals.getColumn());
        }
        
        return expr;
    }
    
    private Expression parseAdditive() throws CompilerError {
        Expression expr = parseMultiplicative();
        
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseMultiplicative();
            expr = new BinaryExpression(expr, operator.getLexeme(), right, operator.getLine(), operator.getColumn());
        }
        
        return expr;
    }
    
    private Expression parseMultiplicative() throws CompilerError {
        Expression expr = parseUnary();
        
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            Token operator = previous();
            Expression right = parseUnary();
            expr = new BinaryExpression(expr, operator.getLexeme(), right, operator.getLine(), operator.getColumn());
        }
        
        return expr;
    }
    
    private Expression parseUnary() throws CompilerError {
        if (match(TokenType.MINUS, TokenType.PLUS, TokenType.LOGICAL_NOT)) {
            Token operator = previous();
            Expression right = parseUnary();
            return new UnaryExpression(operator.getLexeme(), right, operator.getLine(), operator.getColumn());
        }
        
        return parsePrimary();
    }
    
    private Expression parsePrimary() throws CompilerError {
        // Boolean literals
        if (match(TokenType.TRUE)) {
            Token token = previous();
            return new LiteralExpression(true, token.getLine(), token.getColumn());
        }
        
        if (match(TokenType.FALSE)) {
            Token token = previous();
            return new LiteralExpression(false, token.getLine(), token.getColumn());
        }
        
        // Numeric literals
        if (match(TokenType.INTEGER_LITERAL)) {
            Token token = previous();
            Object value = parseNumberLiteral(token.getLexeme());
            return new LiteralExpression(value, token.getLine(), token.getColumn());
        }
        
        if (match(TokenType.FLOAT_LITERAL)) {
            Token token = previous();
            Object value = parseNumberLiteral(token.getLexeme());
            return new LiteralExpression(value, token.getLine(), token.getColumn());
        }
        
        // String literals
        if (match(TokenType.STRING_LITERAL)) {
            Token token = previous();
            String value = token.getLexeme().substring(1, token.getLexeme().length() - 1); // Remove quotes
            return new LiteralExpression(value, token.getLine(), token.getColumn());
        }
        
        // Character literals
        if (match(TokenType.CHAR_LITERAL)) {
            Token token = previous();
            String lexeme = token.getLexeme();
            char value = lexeme.charAt(1); // Get character between single quotes
            return new LiteralExpression(value, token.getLine(), token.getColumn());
        }
        
        // Identifiers
        if (match(TokenType.IDENTIFIER)) {
            Token name = previous();
            return new IdentifierExpression(name.getLexeme(), name.getLine(), name.getColumn());
        }
        
        // Parenthesized expressions
        if (match(TokenType.LEFT_PAREN)) {
            Expression expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
            return expr;
        }
        
        throw error(ErrorCode.BAD_SYNTAX, peek(), "Expected expression.");
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
    
    private Token consumeModuleName(String message) throws CompilerError {
        // Allow identifiers and keywords as module names
        if (check(TokenType.IDENTIFIER) || isKeywordToken(peek().getType())) {
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
    
    private boolean isKeywordToken(TokenType type) {
        return isTypeToken(type) || 
               type == TokenType.IF || type == TokenType.ELSE || type == TokenType.WHILE ||
               type == TokenType.FOR || type == TokenType.RETURN || type == TokenType.BREAK ||
               type == TokenType.CONTINUE || type == TokenType.STRUCT || type == TokenType.PUBLIC ||
               type == TokenType.IMPORT || type == TokenType.MODULE || type == TokenType.TRUE ||
               type == TokenType.FALSE;
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
    
    private boolean checkStringConcat() {
        return current + 1 < tokens.size() && 
               peek().getType() == TokenType.BITWISE_XOR && 
               tokens.get(current + 1).getType() == TokenType.BITWISE_XOR;
    }
    
    private Type getTypeFromToken(Token token) throws CompilerError {
        PrimitiveType type = PrimitiveType.fromName(token.getLexeme());
        if (type == null) {
            throw error(ErrorCode.BAD_SYNTAX, token, "Unknown type: " + token.getLexeme());
        }
        return type;
    }
    
    private Object parseNumberLiteral(String lexeme) {
        try {
            // Check for floating point
            if (lexeme.contains(".")) {
                if (lexeme.endsWith("f") || lexeme.endsWith("F")) {
                    return Float.parseFloat(lexeme.substring(0, lexeme.length() - 1));
                } else {
                    return Double.parseDouble(lexeme);
                }
            } else {
                // Integer literal - parse as long to handle all integer types
                if (lexeme.endsWith("u") || lexeme.endsWith("U")) {
                    // Unsigned integer literal
                    return Long.parseUnsignedLong(lexeme.substring(0, lexeme.length() - 1));
                } else {
                    return Long.parseLong(lexeme);
                }
            }
        } catch (NumberFormatException e) {
            // This should not happen if lexer is working correctly
            return 0L;
        }
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
                case IMPORT:
                case PUBLIC:
                    return;
            }
            
            advance();
        }
    }
    
}
