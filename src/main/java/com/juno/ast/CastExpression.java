package com.juno.ast;

import com.juno.types.Type;

/**
 * AST node representing an explicit cast expression.
 * Syntax: <targetType>(expression)
 * Examples: int(floatValue), ubyte(intValue), double(result)
 */
public class CastExpression implements Expression {
	private final Type targetType;
	private final Expression expression;
	private final int line;
	private final int column;
	private Type type; // Will be set to targetType during type checking

	public CastExpression(Type targetType, Expression expression, int line, int column) {
		this.targetType = targetType;
		this.expression = expression;
		this.line = line;
		this.column = column;
		this.type = targetType; // Cast expression evaluates to the target type
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitCastExpression(this);
	}

	@Override
	public int line() {
		return line;
	}

	@Override
	public int column() {
		return column;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Get the target type to cast to.
	 *
	 * @return the target type
	 */
	public Type getTargetType() {
		return targetType;
	}

	/**
	 * Get the expression being cast.
	 *
	 * @return the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		return targetType + "(" + expression + ")";
	}
}