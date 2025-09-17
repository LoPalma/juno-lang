package com.juno.ast;

import com.juno.types.Type;

/**
 * AST node representing a type alias declaration.
 * Example: type c_char = char|int;
 */
public class TypeAlias implements Statement {
    private final String aliasName;
    private final Type aliasedType;
    private final int line;
    private final int column;
    
    public TypeAlias(String aliasName, Type aliasedType, int line, int column) {
        this.aliasName = aliasName;
        this.aliasedType = aliasedType;
        this.line = line;
        this.column = column;
    }
    
    public String getAliasName() {
        return aliasName;
    }
    
    public Type getAliasedType() {
        return aliasedType;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTypeAlias(this);
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
    public String toString() {
        return "TypeAlias(" + aliasName + " = " + aliasedType + ")";
    }
}