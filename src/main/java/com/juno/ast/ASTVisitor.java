package com.juno.ast;

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
    T visitQualifiedIdentifier(QualifiedIdentifier expr);
    T visitCastExpression(CastExpression expr);
    T visitArrayLiteralExpression(ArrayLiteralExpression expr);
    T visitArrayIndexExpression(ArrayIndexExpression expr);
    T visitAddressOfExpression(AddressOfExpression expr);
    T visitDereferenceExpression(DereferenceExpression expr);
    
    // Control flow statements
    T visitBreakStatement(BreakStatement stmt);
    T visitContinueStatement(ContinueStatement stmt);
    
    T visitExpressionStatement(ExpressionStatement stmt);
    T visitVariableDeclaration(VariableDeclaration stmt);
    T visitFunctionDeclaration(FunctionDeclaration stmt);
    T visitIfStatement(IfStatement stmt);
    T visitWhileStatement(WhileStatement stmt);
    T visitForInStatement(ForInStatement stmt);
    T visitReturnStatement(ReturnStatement stmt);
    T visitBlockStatement(BlockStatement stmt);
    T visitImportStatement(ImportStatement stmt);
    T visitModuleDeclaration(ModuleDeclaration stmt);
    T visitTypeAlias(TypeAlias stmt);
    T visitStructDeclaration(StructDeclaration stmt);
}