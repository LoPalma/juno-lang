package com.clikejvm.ast;

import com.clikejvm.types.Type;
import java.util.List;

// Stub implementations for AST nodes referenced in ASTVisitor

class UnaryExpression implements Expression {
    private Type type;
    private final int line, column;
    
    public UnaryExpression(int line, int column) { this.line = line; this.column = column; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitUnaryExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class LiteralExpression implements Expression {
    private Type type;
    private final int line, column;
    
    public LiteralExpression(int line, int column) { this.line = line; this.column = column; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitLiteralExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class IdentifierExpression implements Expression {
    private Type type;
    private final int line, column;
    
    public IdentifierExpression(int line, int column) { this.line = line; this.column = column; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitIdentifierExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class AssignmentExpression implements Expression {
    private Type type;
    private final int line, column;
    
    public AssignmentExpression(int line, int column) { this.line = line; this.column = column; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitAssignmentExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class CallExpression implements Expression {
    private Type type;
    private final int line, column;
    
    public CallExpression(int line, int column) { this.line = line; this.column = column; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitCallExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class ExpressionStatement implements Statement {
    private final int line, column;
    
    public ExpressionStatement(int line, int column) { this.line = line; this.column = column; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitExpressionStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class VariableDeclaration implements Statement {
    private final int line, column;
    
    public VariableDeclaration(int line, int column) { this.line = line; this.column = column; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitVariableDeclaration(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class FunctionDeclaration implements Statement {
    private final int line, column;
    
    public FunctionDeclaration(int line, int column) { this.line = line; this.column = column; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitFunctionDeclaration(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class IfStatement implements Statement {
    private final int line, column;
    
    public IfStatement(int line, int column) { this.line = line; this.column = column; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitIfStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class WhileStatement implements Statement {
    private final int line, column;
    
    public WhileStatement(int line, int column) { this.line = line; this.column = column; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitWhileStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class ReturnStatement implements Statement {
    private final int line, column;
    
    public ReturnStatement(int line, int column) { this.line = line; this.column = column; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitReturnStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

class BlockStatement implements Statement {
    private final int line, column;
    
    public BlockStatement(int line, int column) { this.line = line; this.column = column; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitBlockStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}