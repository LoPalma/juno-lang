package com.juno.ast;

/**
 * Base interface for all AST nodes.
 */
public interface ASTNode {
    /**
     * Accept a visitor for processing this node.
     * @param visitor The visitor to accept
     * @param <T> The return type of the visitor
     * @return The result of visiting this node
     */
    <T> T accept(ASTVisitor<T> visitor);
    
    /**
     * Get the line number where this node appears in source code.
     * @return line number
     */
    int getLine();
    
    /**
     * Get the column number where this node appears in source code.
     * @return column number
     */
    int getColumn();
}