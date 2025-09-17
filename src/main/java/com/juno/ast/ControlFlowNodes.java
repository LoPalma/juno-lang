package com.juno.ast;

/**
 * AST nodes for break and continue control flow statements.
 */

/**
 * Break statement: break;
 */
class BreakStatement implements Statement {
    private final int line, column;
    
    public BreakStatement(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) { 
        return visitor.visitBreakStatement(this); 
    }
    
    @Override
    public int getLine() { return line; }
    
    @Override
    public int getColumn() { return column; }
}

/**
 * Continue statement: continue;
 */
class ContinueStatement implements Statement {
    private final int line, column;
    
    public ContinueStatement(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) { 
        return visitor.visitContinueStatement(this); 
    }
    
    @Override
    public int getLine() { return line; }
    
    @Override
    public int getColumn() { return column; }
}