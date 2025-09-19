package com.juno.ast;

import java.util.List;

/**
 * Root AST node representing a complete program.
 */
public class Program implements ASTNode {
    private final List<Statement> statements;
    private final int line;
    private final int column;

    public Program(List<Statement> statements, int line, int column) {
        this.statements = statements;
        this.line = line;
        this.column = column;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
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
    public String toString() {
        return "Program{statements=" + statements + "}";
    }
}