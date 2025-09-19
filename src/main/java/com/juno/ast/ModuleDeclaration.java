package com.juno.ast;

import java.util.List;

/**
 * AST node representing a module declaration.
 * Example: module io { ... }
 */
public class ModuleDeclaration implements Statement {
    private final String moduleName;
    private final List<Statement> statements;
    private final int line;
    private final int column;

    public ModuleDeclaration(String moduleName, List<Statement> statements, int line, int column) {
        this.moduleName = moduleName;
        this.statements = statements;
        this.line = line;
        this.column = column;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitModuleDeclaration(this);
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    public String getModuleName() {
        return moduleName;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}