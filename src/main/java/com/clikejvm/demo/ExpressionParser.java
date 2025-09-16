package com.clikejvm.demo;

import com.clikejvm.ast.ASTPrettyPrinter;
import com.clikejvm.error.CompilerError;
import com.clikejvm.error.ErrorCode;
import com.clikejvm.error.ErrorCollector;
import com.clikejvm.lexer.Lexer;
import com.clikejvm.lexer.Token;
import com.clikejvm.lexer.TokenType;

import java.util.List;

/**
 * Simple recursive descent parser for expressions that outputs pretty-printed AST.
 * Demonstrates the parenthesized AST format.
 */
public class ExpressionParser {
    private final List<Token> tokens;
    private final ASTPrettyPrinter printer;
    private int current = 0;
    
    public ExpressionParser(List<Token> tokens) {
        this.tokens = tokens;
        this.printer = new ASTPrettyPrinter();
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ExpressionParser \"<expression>\"");
            System.err.println("Example: java ExpressionParser \"5 + 3 * 2\"");
            System.exit(1);
        }
        
        String input = args[0];
        System.out.println("Input: " + input);
        System.out.println("AST:");
        
        try {
            ErrorCollector errorCollector = new ErrorCollector();
            Lexer lexer = new Lexer(input, "<input>", errorCollector);
            List<Token> tokens = lexer.tokenize();
            
            if (errorCollector.hasErrors()) {
                errorCollector.printAll();
                System.exit(1);
            }
            
            ExpressionParser parser = new ExpressionParser(tokens);
            String ast = parser.parseExpression();
            System.out.println(ast);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String parseExpression() {
        return additive();
    }
    
    private String additive() {
        String left = multiplicative();
        
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            String operator = previous().getLexeme();
            String right = multiplicative();
            left = printer.visitBinaryOperation(operator, left, right);
        }
        
        return left;
    }
    
    private String multiplicative() {
        String left = primary();
        
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            String operator = previous().getLexeme();
            String right = primary();
            left = printer.visitBinaryOperation(operator, left, right);
        }
        
        return left;
    }
    
    private String primary() {
        if (match(TokenType.INTEGER_LITERAL)) {
            Object value = previous().getLiteral();
            String type = inferType(value);
            return printer.visitLiteral(value, type);
        }
        
        if (match(TokenType.FLOAT_LITERAL)) {
            Object value = previous().getLiteral();
            String type = inferType(value);
            return printer.visitLiteral(value, type);
        }
        
        if (match(TokenType.STRING_LITERAL)) {
            Object value = previous().getLiteral();
            return printer.visitLiteral(value, "string");
        }
        
        if (match(TokenType.CHAR_LITERAL)) {
            Object value = previous().getLiteral();
            return printer.visitLiteral(value, "char");
        }
        
        if (match(TokenType.TRUE, TokenType.FALSE)) {
            Object value = previous().getLiteral();
            return printer.visitLiteral(value, "bool");
        }
        
        if (match(TokenType.IDENTIFIER)) {
            String name = previous().getLexeme();
            return printer.visitIdentifier(name);
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            String expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression");
            return expr;
        }
        
        throw new RuntimeException("Expected expression");
    }
    
    private String inferType(Object value) {
        if (value instanceof Integer) {
            int intValue = (Integer) value;
            if (intValue >= Byte.MIN_VALUE && intValue <= Byte.MAX_VALUE) {
                return "byte";
            } else if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                return "short";
            } else {
                return "int";
            }
        } else if (value instanceof Long) {
            return "long";
        } else if (value instanceof Double) {
            return "double";
        } else if (value instanceof Float) {
            return "float";
        }
        return "unknown";
    }
    
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
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
    
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException(message);
    }
}
