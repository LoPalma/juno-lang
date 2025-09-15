package com.clikejvm.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for the Lexer class.
 */
public class LexerTest {

    @Test
    @DisplayName("Should tokenize basic keywords correctly")
    public void testKeywords() {
        String source = "int float char string bool void true false if else while for return";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token for easier testing
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(13);
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INT);
        assertThat(tokens.get(1).getType()).isEqualTo(TokenType.FLOAT);
        assertThat(tokens.get(2).getType()).isEqualTo(TokenType.CHAR);
        assertThat(tokens.get(3).getType()).isEqualTo(TokenType.STRING);
        assertThat(tokens.get(4).getType()).isEqualTo(TokenType.BOOL);
        assertThat(tokens.get(5).getType()).isEqualTo(TokenType.VOID);
        assertThat(tokens.get(6).getType()).isEqualTo(TokenType.TRUE);
        assertThat(tokens.get(7).getType()).isEqualTo(TokenType.FALSE);
        assertThat(tokens.get(8).getType()).isEqualTo(TokenType.IF);
        assertThat(tokens.get(9).getType()).isEqualTo(TokenType.ELSE);
        assertThat(tokens.get(10).getType()).isEqualTo(TokenType.WHILE);
        assertThat(tokens.get(11).getType()).isEqualTo(TokenType.FOR);
        assertThat(tokens.get(12).getType()).isEqualTo(TokenType.RETURN);
    }

    @Test
    @DisplayName("Should tokenize operators correctly")
    public void testOperators() {
        String source = "+ - * / % = == != < <= > >= && || ! & | ^ ~ << >>";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(21);
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.PLUS);
        assertThat(tokens.get(1).getType()).isEqualTo(TokenType.MINUS);
        assertThat(tokens.get(2).getType()).isEqualTo(TokenType.MULTIPLY);
        assertThat(tokens.get(3).getType()).isEqualTo(TokenType.DIVIDE);
        assertThat(tokens.get(4).getType()).isEqualTo(TokenType.MODULO);
        assertThat(tokens.get(5).getType()).isEqualTo(TokenType.ASSIGN);
        assertThat(tokens.get(6).getType()).isEqualTo(TokenType.EQUALS);
        assertThat(tokens.get(7).getType()).isEqualTo(TokenType.NOT_EQUALS);
        assertThat(tokens.get(8).getType()).isEqualTo(TokenType.LESS_THAN);
        assertThat(tokens.get(9).getType()).isEqualTo(TokenType.LESS_EQUAL);
        assertThat(tokens.get(10).getType()).isEqualTo(TokenType.GREATER_THAN);
        assertThat(tokens.get(11).getType()).isEqualTo(TokenType.GREATER_EQUAL);
        assertThat(tokens.get(12).getType()).isEqualTo(TokenType.LOGICAL_AND);
        assertThat(tokens.get(13).getType()).isEqualTo(TokenType.LOGICAL_OR);
        assertThat(tokens.get(14).getType()).isEqualTo(TokenType.LOGICAL_NOT);
        assertThat(tokens.get(15).getType()).isEqualTo(TokenType.BITWISE_AND);
        assertThat(tokens.get(16).getType()).isEqualTo(TokenType.BITWISE_OR);
        assertThat(tokens.get(17).getType()).isEqualTo(TokenType.BITWISE_XOR);
        assertThat(tokens.get(18).getType()).isEqualTo(TokenType.BITWISE_NOT);
        assertThat(tokens.get(19).getType()).isEqualTo(TokenType.LEFT_SHIFT);
        assertThat(tokens.get(20).getType()).isEqualTo(TokenType.RIGHT_SHIFT);
    }

    @Test
    @DisplayName("Should tokenize delimiters correctly")
    public void testDelimiters() {
        String source = "() {} [] ; , . ->";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(10);
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.LEFT_PAREN);
        assertThat(tokens.get(1).getType()).isEqualTo(TokenType.RIGHT_PAREN);
        assertThat(tokens.get(2).getType()).isEqualTo(TokenType.LEFT_BRACE);
        assertThat(tokens.get(3).getType()).isEqualTo(TokenType.RIGHT_BRACE);
        assertThat(tokens.get(4).getType()).isEqualTo(TokenType.LEFT_BRACKET);
        assertThat(tokens.get(5).getType()).isEqualTo(TokenType.RIGHT_BRACKET);
        assertThat(tokens.get(6).getType()).isEqualTo(TokenType.SEMICOLON);
        assertThat(tokens.get(7).getType()).isEqualTo(TokenType.COMMA);
        assertThat(tokens.get(8).getType()).isEqualTo(TokenType.DOT);
        assertThat(tokens.get(9).getType()).isEqualTo(TokenType.ARROW);
    }

    @Test
    @DisplayName("Should tokenize identifiers correctly")
    public void testIdentifiers() {
        String source = "variable myFunction _private MAX_SIZE camelCase";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(5);
        for (Token token : tokens) {
            assertThat(token.getType()).isEqualTo(TokenType.IDENTIFIER);
        }

        assertThat(tokens.get(0).getLexeme()).isEqualTo("variable");
        assertThat(tokens.get(1).getLexeme()).isEqualTo("myFunction");
        assertThat(tokens.get(2).getLexeme()).isEqualTo("_private");
        assertThat(tokens.get(3).getLexeme()).isEqualTo("MAX_SIZE");
        assertThat(tokens.get(4).getLexeme()).isEqualTo("camelCase");
    }

    @Test
    @DisplayName("Should tokenize string literals correctly")
    public void testStringLiterals() {
        String source = "\"Hello, World!\" \"\" \"Line 1\\nLine 2\"";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(3);
        for (Token token : tokens) {
            assertThat(token.getType()).isEqualTo(TokenType.STRING_LITERAL);
        }

        assertThat(tokens.get(0).getLiteral()).isEqualTo("Hello, World!");
        assertThat(tokens.get(1).getLiteral()).isEqualTo("");
        assertThat(tokens.get(2).getLiteral()).isEqualTo("Line 1\nLine 2");
    }

    @Test
    @DisplayName("Should tokenize character literals correctly")
    public void testCharacterLiterals() {
        String source = "'a' 'Z' '\\n' '\\t' '\\\\'";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(5);
        for (Token token : tokens) {
            assertThat(token.getType()).isEqualTo(TokenType.CHAR_LITERAL);
        }

        assertThat(tokens.get(0).getLiteral()).isEqualTo('a');
        assertThat(tokens.get(1).getLiteral()).isEqualTo('Z');
        assertThat(tokens.get(2).getLiteral()).isEqualTo('\n');
        assertThat(tokens.get(3).getLiteral()).isEqualTo('\t');
        assertThat(tokens.get(4).getLiteral()).isEqualTo('\\');
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "0", "999999"})
    @DisplayName("Should tokenize integer literals correctly")
    public void testIntegerLiterals(String intStr) {
        Lexer lexer = new Lexer(intStr);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INTEGER_LITERAL);
        assertThat(tokens.get(0).getLiteral()).isEqualTo(Integer.parseInt(intStr));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3.14", "0.0", "123.456"})
    @DisplayName("Should tokenize float literals correctly")
    public void testFloatLiterals(String floatStr) {
        Lexer lexer = new Lexer(floatStr);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.FLOAT_LITERAL);
        assertThat(tokens.get(0).getLiteral()).isEqualTo(Double.parseDouble(floatStr));
    }

    @Test
    @DisplayName("Should handle single-line comments correctly")
    public void testSingleLineComments() {
        String source = "int x; // This is a comment\nint y;";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(6); // int, x, ;, int, y, ;
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INT);
        assertThat(tokens.get(1).getType()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(tokens.get(2).getType()).isEqualTo(TokenType.SEMICOLON);
        assertThat(tokens.get(3).getType()).isEqualTo(TokenType.INT);
        assertThat(tokens.get(4).getType()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(tokens.get(5).getType()).isEqualTo(TokenType.SEMICOLON);
    }

    @Test
    @DisplayName("Should handle block comments correctly")
    public void testBlockComments() {
        String source = "int x; /* This is a\n   block comment */ int y;";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(6); // int, x, ;, int, y, ;
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INT);
        assertThat(tokens.get(1).getType()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(tokens.get(2).getType()).isEqualTo(TokenType.SEMICOLON);
        assertThat(tokens.get(3).getType()).isEqualTo(TokenType.INT);
        assertThat(tokens.get(4).getType()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(tokens.get(5).getType()).isEqualTo(TokenType.SEMICOLON);
    }

    @Test
    @DisplayName("Should maintain correct line and column information")
    public void testLineAndColumnTracking() {
        String source = "int x;\nfloat y;";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Check first line tokens
        assertThat(tokens.get(0).getLine()).isEqualTo(1); // int
        assertThat(tokens.get(1).getLine()).isEqualTo(1); // x
        assertThat(tokens.get(2).getLine()).isEqualTo(1); // ;

        // Check second line tokens
        assertThat(tokens.get(3).getLine()).isEqualTo(2); // float
        assertThat(tokens.get(4).getLine()).isEqualTo(2); // y
        assertThat(tokens.get(5).getLine()).isEqualTo(2); // ;
    }

    @Test
    @DisplayName("Should tokenize complete function correctly")
    public void testCompleteFunction() {
        String source = """
            int factorial(int n) {
                if (n <= 1) {
                    return 1;
                } else {
                    return n * factorial(n - 1);
                }
            }
            """;
        
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Should find all expected tokens including keywords, identifiers, operators, and delimiters
        assertThat(tokens).hasSizeGreaterThan(25);
        assertThat(tokens.get(tokens.size() - 1).getType()).isEqualTo(TokenType.EOF);

        // Check some key tokens
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INT);
        assertThat(tokens.get(1).getType()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(tokens.get(1).getLexeme()).isEqualTo("factorial");
    }

    @Test
    @DisplayName("Should handle whitespace correctly")
    public void testWhitespaceHandling() {
        String source = "   int    x   ;   ";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Remove EOF token
        tokens = tokens.subList(0, tokens.size() - 1);

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INT);
        assertThat(tokens.get(1).getType()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(tokens.get(2).getType()).isEqualTo(TokenType.SEMICOLON);
    }

    @Test
    @DisplayName("Should always end with EOF token")
    public void testEOFToken() {
        String source = "int x;";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        assertThat(tokens).isNotEmpty();
        Token lastToken = tokens.get(tokens.size() - 1);
        assertThat(lastToken.getType()).isEqualTo(TokenType.EOF);
    }

    @Test
    @DisplayName("Should handle empty source")
    public void testEmptySource() {
        String source = "";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0).getType()).isEqualTo(TokenType.EOF);
    }
}