package com.juno.ast;

/**
 * Represents the dereference operator (*pointer)
 */
public class DereferenceExpression implements Expression {
    private final Expression operand;
    private final int line;
    private final int column;
    private com.juno.types.Type type;
    
    public DereferenceExpression(Expression operand, int line, int column) {
        this.operand = operand;
        this.line = line;
        this.column = column;
    }
    
    public Expression getOperand() {
        return operand;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDereferenceExpression(this);
    }
    
    @Override
    public int getLine() {
        return line;
    }
    
    @Override
    public int getColumn() {
        return column;
    }
    
    @Override
    public com.juno.types.Type getType() {
        return type;
    }
    
    @Override
    public void setType(com.juno.types.Type type) {
        this.type = type;
    }
}