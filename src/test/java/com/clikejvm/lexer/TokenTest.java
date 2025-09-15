package com.clikejvm.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Token class.
 */
public class TokenTest {

    @Test
    @DisplayName("Should create token with all properties")
    public void testTokenCreation() {
        Token token = new Token(TokenType.INT, "int", null, 1, 5);
        
        assertThat(token.getType()).isEqualTo(TokenType.INT);
        assertThat(token.getLexeme()).isEqualTo("int");
        assertThat(token.getLiteral()).isNull();
        assertThat(token.getLine()).isEqualTo(1);
        assertThat(token.getColumn()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should create token with literal value")
    public void testTokenWithLiteral() {
        Token intToken = new Token(TokenType.INTEGER_LITERAL, "123", 123, 2, 10);
        Token stringToken = new Token(TokenType.STRING_LITERAL, "\"hello\"", "hello", 3, 15);
        Token charToken = new Token(TokenType.CHAR_LITERAL, "'a'", 'a', 4, 20);
        Token floatToken = new Token(TokenType.FLOAT_LITERAL, "3.14", 3.14, 5, 25);
        Token boolToken = new Token(TokenType.TRUE, "true", true, 6, 30);

        assertThat(intToken.getLiteral()).isEqualTo(123);
        assertThat(stringToken.getLiteral()).isEqualTo("hello");
        assertThat(charToken.getLiteral()).isEqualTo('a');
        assertThat(floatToken.getLiteral()).isEqualTo(3.14);
        assertThat(boolToken.getLiteral()).isEqualTo(true);
    }

    @Test
    @DisplayName("Should handle null literal")
    public void testNullLiteral() {
        Token token = new Token(TokenType.IDENTIFIER, "variable", null, 1, 1);
        
        assertThat(token.getLiteral()).isNull();
    }

    @Test
    @DisplayName("Should maintain immutable properties")
    public void testImmutability() {
        Token token = new Token(TokenType.STRING, "string", null, 10, 20);
        
        // Properties should not change
        assertThat(token.getType()).isEqualTo(TokenType.STRING);
        assertThat(token.getLexeme()).isEqualTo("string");
        assertThat(token.getLine()).isEqualTo(10);
        assertThat(token.getColumn()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should create EOF token correctly")
    public void testEOFToken() {
        Token eofToken = new Token(TokenType.EOF, "", null, 15, 1);
        
        assertThat(eofToken.getType()).isEqualTo(TokenType.EOF);
        assertThat(eofToken.getLexeme()).isEmpty();
        assertThat(eofToken.getLiteral()).isNull();
    }

    @Test
    @DisplayName("Should handle different numeric literal types")
    public void testNumericLiterals() {
        Token byteToken = new Token(TokenType.INTEGER_LITERAL, "127", (byte) 127, 1, 1);
        Token shortToken = new Token(TokenType.INTEGER_LITERAL, "32000", (short) 32000, 1, 1);
        Token longToken = new Token(TokenType.INTEGER_LITERAL, "999999999", 999999999L, 1, 1);
        
        assertThat(byteToken.getLiteral()).isEqualTo((byte) 127);
        assertThat(shortToken.getLiteral()).isEqualTo((short) 32000);
        assertThat(longToken.getLiteral()).isEqualTo(999999999L);
    }

    @Test
    @DisplayName("Should create tokens for all operator types")
    public void testOperatorTokens() {
        Token plusToken = new Token(TokenType.PLUS, "+", null, 1, 1);
        Token equalsToken = new Token(TokenType.EQUALS, "==", null, 1, 5);
        Token andToken = new Token(TokenType.LOGICAL_AND, "&&", null, 1, 10);
        
        assertThat(plusToken.getType()).isEqualTo(TokenType.PLUS);
        assertThat(equalsToken.getType()).isEqualTo(TokenType.EQUALS);
        assertThat(andToken.getType()).isEqualTo(TokenType.LOGICAL_AND);
        
        assertThat(plusToken.getLexeme()).isEqualTo("+");
        assertThat(equalsToken.getLexeme()).isEqualTo("==");
        assertThat(andToken.getLexeme()).isEqualTo("&&");
    }
}