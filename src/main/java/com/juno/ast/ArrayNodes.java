package com.juno.ast;

import com.juno.types.Type;
import java.util.List;

/**
 * AST nodes for array-related expressions in Juno language.
 */

/**
 * Array literal expression: [1, 2, 3] or ["a", "b", "c"]
 */
class ArrayLiteralExpression implements Expression {
    private final List<Expression> elements;
    private final int line, column;
    private Type type;
    
    public ArrayLiteralExpression(List<Expression> elements, int line, int column) {
        this.elements = elements;
        this.line = line;
        this.column = column;
    }
    
    public List<Expression> getElements() { return elements; }
    
    @Override
    public Type getType() { return type; }
    
    @Override
    public void setType(Type type) { this.type = type; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) { 
        return visitor.visitArrayLiteralExpression(this); 
    }
    
    @Override
    public int line() { return line; }
    
    @Override
    public int column() { return column; }
}

/**
 * Array index expression: array[index]
 */
class ArrayIndexExpression implements Expression {
    private final Expression array;
    private final Expression index;
    private final int line, column;
    private Type type;
    
    public ArrayIndexExpression(Expression array, Expression index, int line, int column) {
        this.array = array;
        this.index = index;
        this.line = line;
        this.column = column;
    }
    
    public Expression getArray() { return array; }
    public Expression getIndex() { return index; }
    
    @Override
    public Type getType() { return type; }
    
    @Override
    public void setType(Type type) { this.type = type; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) { 
        return visitor.visitArrayIndexExpression(this); 
    }
    
    @Override
    public int line() { return line; }
    
    @Override
    public int column() { return column; }
}