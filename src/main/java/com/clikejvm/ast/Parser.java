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
        // If we're at EOF, return null to signal end of parsing
        if (isAtEnd()) {
            return null;
        }
        
        // Handle import statements
        if (match(TokenType.IMPORT)) {
            return parseImportStatement();
        }
        
        // Handle public declarations
        if (match(TokenType.PUBLIC)) {
            return parsePublicDeclaration();
        }
        
        // Handle type declarations (functions or variables)
        if (isTypeToken(peek().getType())) {
            if (checkFunctionDecl()) {
                return parseFunctionDeclaration(false);
            } else {
                return parseVariableDeclaration(false);
            }
        }
        
        // Handle control flow statements
        if (match(TokenType.IF)) return parseIfStatement();
        if (match(TokenType.WHILE)) return parseWhileStatement();
        if (match(TokenType.RETURN)) return parseReturnStatement();
        if (match(TokenType.LEFT_BRACE)) return parseBlockStatement();
        
        // Handle expression statements
        return parseExpressionStatement();
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
    
    private VariableDeclaration parseVariableDeclaration(boolean isPublic) throws CompilerError {
        Token typeToken = consumeType("Expected type.");
        Type type = getTypeFromToken(typeToken);
        
        Token nameToken = consume(TokenType.IDENTIFIER, "Expected variable name after type.");
        String name = nameToken.getLexeme();
        
        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration.");
        return new VariableDeclaration(type, name, initializer, isPublic, typeToken.getLine(), typeToken.getColumn());
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
        
        return parseCall();
    }
    
    private Expression parseCall() throws CompilerError {
        Expression expr = parsePrimary();
        
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }
        
        return expr;
    }
    
    private Expression finishCall(Expression callee) throws CompilerError {
        List<Expression> arguments = new ArrayList<>();
        
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        
        Token paren = consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments.");
        return new CallExpression(callee, arguments, paren.getLine(), paren.getColumn());
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
        
        // Identifiers and qualified identifiers
        if (match(TokenType.IDENTIFIER)) {
            Token name = previous();
            
            // Check for qualified identifier (module.identifier)
            if (match(TokenType.DOT)) {
                Token memberToken = consume(TokenType.IDENTIFIER, "Expected identifier after '.'.");
                return new QualifiedIdentifier(name.getLexeme(), memberToken.getLexeme(), 
                                             name.getLine(), name.getColumn());
            }
            
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
    
    // ========== STATEMENT PARSING ==========
    
    private Statement parsePublicDeclaration() throws CompilerError {
        if (isTypeToken(peek().getType())) {
            if (checkFunctionDecl()) {
                return parseFunctionDeclaration(true);
            } else {
                return parseVariableDeclaration(true);
            }
        }
        
        throw error(ErrorCode.BAD_SYNTAX, peek(), "Expected function or variable declaration after 'public'.");
    }
    
    private FunctionDeclaration parseFunctionDeclaration(boolean isPublic) throws CompilerError {
        Token typeToken = consumeType("Expected return type.");
        Type returnType = getTypeFromToken(typeToken);
        
        Token nameToken = consume(TokenType.IDENTIFIER, "Expected function name.");
        String name = nameToken.getLexeme();
        
        consume(TokenType.LEFT_PAREN, "Expected '(' after function name.");
        
        List<FunctionDeclaration.Parameter> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                Token paramTypeToken = consumeType("Expected parameter type.");
                Type paramType = getTypeFromToken(paramTypeToken);
                Token paramNameToken = consume(TokenType.IDENTIFIER, "Expected parameter name.");
                String paramName = paramNameToken.getLexeme();
                parameters.add(new FunctionDeclaration.Parameter(paramType, paramName));
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expected '{' before function body.");
        BlockStatement body = parseBlockStatement();
        
        return new FunctionDeclaration(returnType, name, parameters, body, isPublic, 
                                     typeToken.getLine(), typeToken.getColumn());
    }
    
    private IfStatement parseIfStatement() throws CompilerError {
        Token ifToken = previous();
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'.");
        Expression condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after if condition.");
        
        Statement thenStmt = parseStatement();
        Statement elseStmt = null;
        
        if (match(TokenType.ELSE)) {
            elseStmt = parseStatement();
        }
        
        return new IfStatement(condition, thenStmt, elseStmt, ifToken.getLine(), ifToken.getColumn());
    }
    
    private WhileStatement parseWhileStatement() throws CompilerError {
        Token whileToken = previous();
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'.");
        Expression condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after while condition.");
        
        Statement body = parseStatement();
        return new WhileStatement(condition, body, whileToken.getLine(), whileToken.getColumn());
    }
    
    private ReturnStatement parseReturnStatement() throws CompilerError {
        Token returnToken = previous();
        Expression value = null;
        
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after return value.");
        return new ReturnStatement(value, returnToken.getLine(), returnToken.getColumn());
    }
    
    private BlockStatement parseBlockStatement() throws CompilerError {
        Token leftBrace = previous();
        List<Statement> statements = new ArrayList<>();
        
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            try {
                Statement stmt = parseStatement();
                if (stmt != null) {
                    statements.add(stmt);
                }
            } catch (CompilerError e) {
                errorCollector.addError(e);
                synchronize();
            }
        }
        
        consume(TokenType.RIGHT_BRACE, "Expected '}' after block.");
        return new BlockStatement(statements, leftBrace.getLine(), leftBrace.getColumn());
    }
    
    private ExpressionStatement parseExpressionStatement() throws CompilerError {
        Expression expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression.");
        return new ExpressionStatement(expr, expr.getLine(), expr.getColumn());
    }
    
    // ========== HELPER METHODS ==========
    
    
    private boolean isTypeToken(TokenType type) {
        return type == TokenType.VOID || type == TokenType.INT || type == TokenType.FLOAT || 
               type == TokenType.DOUBLE || type == TokenType.CHAR || type == TokenType.STRING ||
               type == TokenType.BOOL || type == TokenType.BYTE || type == TokenType.SHORT ||
               type == TokenType.LONG || type == TokenType.UBYTE || type == TokenType.USHORT ||
               type == TokenType.UINT || type == TokenType.ULONG;
    }
    
    private Token consumeType(String message) throws CompilerError {
        if (isTypeToken(peek().getType())) {
            return advance();
        }
        throw error(ErrorCode.BAD_SYNTAX, peek(), message);
    }
    
    private Type getTypeFromToken(Token token) {
        switch (token.getType()) {
            case VOID: return PrimitiveType.VOID;
            case INT: return PrimitiveType.INT;
            case FLOAT: return PrimitiveType.FLOAT;
            case DOUBLE: return PrimitiveType.DOUBLE;
            case CHAR: return PrimitiveType.CHAR;
            case STRING: return PrimitiveType.STRING;
            case BOOL: return PrimitiveType.BOOL;
            case BYTE: return PrimitiveType.BYTE;
            case SHORT: return PrimitiveType.SHORT;
            case LONG: return PrimitiveType.LONG;
            case UBYTE: return PrimitiveType.UBYTE;
            case USHORT: return PrimitiveType.USHORT;
            case UINT: return PrimitiveType.UINT;
            case ULONG: return PrimitiveType.ULONG;
            default:
                throw new RuntimeException("Invalid type token: " + token.getType());
        }
    }
    
    private void synchronize() {
        advance();
        
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;
            
            switch (peek().getType()) {
                case IF:
                case WHILE:
                case RETURN:
                case PUBLIC:
                case IMPORT:
                    return;
                default:
                    if (isTypeToken(peek().getType())) {
                        return;
                    }
            }
            
            advance();
        }
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
    
    
}
