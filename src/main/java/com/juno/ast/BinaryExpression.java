package com.juno.ast;

import com.juno.types.Type;

/**
 * Binary expression AST node (e.g., a + b, x == y).
 */
public class BinaryExpression implements Expression {
	private final Expression left;
	private final String operator;
	private final Expression right;
	private final int line;
	private final int column;
	private Type type;

	public BinaryExpression(Expression left, String operator, Expression right, int line, int column) {
		this.left = left;
		this.operator = operator;
		this.right = right;
		this.line = line;
		this.column = column;
	}

	public Expression getLeft() {
		return left;
	}

	public String getOperator() {
		return operator;
	}

	public Expression getRight() {
		return right;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitBinaryExpression(this);
	}

	@Override
	public int line() {
		return line;
	}

	@Override
	public int column() {
		return column;
	}
}