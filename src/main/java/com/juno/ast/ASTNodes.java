package com.juno.ast;

import com.juno.types.Type;

import java.util.List;

// Expression nodes

/**
 * Unary expression AST node (e.g., -x, !condition, ~bits).
 */
class UnaryExpression implements Expression {
	private final String operator;
	private final Expression operand;
	private final int line, column;
	private Type type;

	public UnaryExpression(String operator, Expression operand, int line, int column) {
		this.operator = operator;
		this.operand = operand;
		this.line = line;
		this.column = column;
	}

	public String getOperator() {
		return operator;
	}

	public Expression getOperand() {
		return operand;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitUnaryExpression(this);
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}
}

/**
 * Literal expression AST node (e.g., 42, 3.14, "hello", 'c', true).
 */
class LiteralExpression implements Expression {
	private final Object value;
	private final int line, column;
	private Type type;

	public LiteralExpression(Object value, int line, int column) {
		this.value = value;
		this.line = line;
		this.column = column;
	}

	public Object getValue() {
		return value;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitLiteralExpression(this);
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}
}

/**
 * Identifier expression AST node (e.g., variable name, function name).
 */
class IdentifierExpression implements Expression {
	private final String name;
	private final int line, column;
	private Type type;

	public IdentifierExpression(String name, int line, int column) {
		this.name = name;
		this.line = line;
		this.column = column;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitIdentifierExpression(this);
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}
}

/**
 * Assignment expression AST node (e.g., x = 5, arr[i] = value).
 */
class AssignmentExpression implements Expression {
	private final Expression target;
	private final Expression value;
	private final int line, column;
	private Type type;

	public AssignmentExpression(Expression target, Expression value, int line, int column) {
		this.target = target;
		this.value = value;
		this.line = line;
		this.column = column;
	}

	public Expression getTarget() {
		return target;
	}

	public Expression getValue() {
		return value;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitAssignmentExpression(this);
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}
}

/**
 * Function call expression AST node (e.g., func(), math.sin(x), obj.method(a, b)).
 */
class CallExpression implements Expression {
	private final Expression function; // can be IdentifierExpression or QualifiedIdentifier
	private final List<Expression> arguments;
	private final int line, column;
	private Type type;

	public CallExpression(Expression function, List<Expression> arguments, int line, int column) {
		this.function = function;
		this.arguments = arguments;
		this.line = line;
		this.column = column;
	}

	public Expression getFunction() {
		return function;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitCallExpression(this);
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}
}

// Statement nodes

/**
 * Expression statement AST node (expression followed by semicolon).
 */
record ExpressionStatement(Expression expression, int line, int column) implements Statement {
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitExpressionStatement(this);
	}
}

/**
 * Variable declaration AST node (e.g., int x = 5;, string name;).
 *
 * @param initializer can be null
 */
record VariableDeclaration(Type type, String name, Expression initializer, boolean isPublic, int line,
													 int column) implements Statement {

	public Type getDeclaredType() {
		return type;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitVariableDeclaration(this);
	}
}

/**
 * Function declaration AST node with parameters and body.
 */

// TODO: fix array parameters not working
record FunctionDeclaration(Type returnType, String name, List<Parameter> parameters, BlockStatement body,
													 boolean isPublic, int line, int column) implements Statement {
	public record Parameter(Type type, String name) {

		@Override
		public String toString() {
			return type + " " + name;
		}
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitFunctionDeclaration(this);
	}
}

/**
 * If statement AST node (with optional else clause).
 *
 * @param elseStmt can be null
 */
record IfStatement(Expression condition, Statement thenStmt, Statement elseStmt, int line,
									 int column) implements Statement {
	public Statement getThenStatement() {
		return thenStmt;
	}

	public Statement getElseStatement() {
		return elseStmt;
	}

	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitIfStatement(this);
	}
}

/**
 * While loop AST node.
 */
record WhileStatement(Expression condition, Statement body, int line, int column) implements Statement {
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitWhileStatement(this);
	}
}

/**
 * Return statement AST node (with optional value).
 *
 * @param value can be null for void return
 */
record ReturnStatement(Expression value, int line, int column) implements Statement {
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitReturnStatement(this);
	}
}

/**
 * Block statement AST node (sequence of statements in braces).
 */
record BlockStatement(List<Statement> statements, int line, int column) implements Statement {
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitBlockStatement(this);
	}
}

/**
 * For-in loop AST node (e.g., for int x = 0 in Array.range(5) {}).
 *
 * @param initializer can be null
 */
record ForInStatement(Type variableType, String variableName, Expression initializer, Expression iterable,
											Statement body, int line, int column) implements Statement {
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitForInStatement(this);
	}
}
