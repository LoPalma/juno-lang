package com.juno.ast;

import com.juno.types.Type;

/**
 * Base interface for all expression nodes.
 */
public interface Expression extends ASTNode {
	/**
	 * Get the type of this expression (set during type checking).
	 *
	 * @return the type, or null if not yet determined
	 */
	Type getType();

	/**
	 * Set the type of this expression (used during type checking).
	 *
	 * @param type the type to set
	 */
	void setType(Type type);
}