package com.juno.ast;

import java.util.List;

/**
 * AST node representing an import statement.
 * Examples: 
 * - import io;
 * - import io.{print, println};
 * - import math.{sin, cos, PI};
 */
public class ImportStatement implements Statement {
    private final String moduleName;
    private final List<String> importedItems; // null means import entire module
    private final int line;
    private final int column;

    public ImportStatement(String moduleName, List<String> importedItems, int line, int column) {
        this.moduleName = moduleName;
        this.importedItems = importedItems;
        this.line = line;
        this.column = column;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitImportStatement(this);
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

    public List<String> getImportedItems() {
        return importedItems;
    }

    public boolean isWildcardImport() {
        return importedItems == null;
    }
}