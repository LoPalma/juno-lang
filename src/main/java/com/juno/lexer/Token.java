package com.juno.lexer;

/**
 * Represents a token in the source code.
 */
public class Token {
	private final TokenType type;
	private final String lexeme;
	private final Object literal;
	private final int line;
	private final int column;

	public Token(TokenType type, String lexeme, Object literal, int line, int column) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
		this.column = column;
	}

	public TokenType getType() {
		return type;
	}

	public String getLexeme() {
		return lexeme;
	}

	public Object getLiteral() {
		return literal;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return String.format("Token{type=%s, lexeme='%s', literal=%s, line=%d, col=%d}",
												 type, lexeme, literal, line, column);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		Token token = (Token) obj;
		return line == token.line &&
				column == token.column &&
				type == token.type &&
				lexeme.equals(token.lexeme) &&
				(literal != null ? literal.equals(token.literal) : token.literal == null);
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + lexeme.hashCode();
		result = 31 * result + (literal != null ? literal.hashCode() : 0);
		result = 31 * result + line;
		result = 31 * result + column;
		return result;
	}
}