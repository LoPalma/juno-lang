package com.juno.ast;

import com.juno.types.Type;

// NOTE: this grammar is currently not available


/**
 * AST node representing a type alias declaration.
 * Example: type c_char = char|int;
 */
public record TypeAlias(String aliasName, Type aliasedType, int line, int column) implements Statement {

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visitTypeAlias(this);
	}

	@Override
	public String toString() {
		return "TypeAlias(" + aliasName + " = " + aliasedType + ")";
	}
}