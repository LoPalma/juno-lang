package com.juno.ast;

import com.juno.types.Type;

/**
 * AST node representing a qualified identifier.
 * Examples: io.print, math.sin, string.length
 */
public class QualifiedIdentifier implements Expression {
	private final String moduleName;
	private final String identifier;
	private final int line;
	private final int column;
	private Type type;

	public QualifiedIdentifier(String moduleName, String identifier, int line, int column) {
		this.moduleName = moduleName;
		this.identifier = identifier;
		this.line = line;
		this.column = column;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitQualifiedIdentifier(this);
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

	public String getModuleName() {
		return moduleName;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getFullName() {
		return moduleName + "." + identifier;
	}
}