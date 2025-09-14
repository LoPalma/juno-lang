package com.clikejvm.ast;

/**
 * Visitor interface for traversing and processing AST nodes.
 * @param <T> The return type of visit methods
 */
public interface ASTVisitor<T> {
    T visitProgram(Program program);
    T visitBinaryExpression(BinaryExpression expr);
    T visitUnaryExpression(UnaryExpression expr);
    T visitLiteralExpression(LiteralExpression expr);
    T visitIdentifierExpression(IdentifierExpression expr);
    T visitAssignmentExpression(AssignmentExpression expr);
    T visitCallExpression(CallExpression expr);
    
    T visitExpressionStatement(ExpressionStatement stmt);
    T visitVariableDeclaration(VariableDeclaration stmt);
    T visitFunctionDeclaration(FunctionDeclaration stmt);
    T visitIfStatement(IfStatement stmt);
    T visitWhileStatement(WhileStatement stmt);
    T visitReturnStatement(ReturnStatement stmt);
    T visitBlockStatement(BlockStatement stmt);
}