package com.clikejvm.ast;

import com.clikejvm.types.Type;
import java.util.List;

/**
 * Complete AST node implementations for the C-like language.
 */

// Expression nodes

/**
 * Unary expression AST node (e.g., -x, !condition, ~bits).
 */
class UnaryExpression implements Expression {
    private final String operator;
    private final Expression operand;
    private final int line, column;
    private Type type;
    
    public UnaryExpression(String operator, Expression operand, int line, int column) {
        this.operator = operator;
        this.operand = operand;
        this.line = line; 
        this.column = column;
    }
    
    public String getOperator() { return operator; }
    public Expression getOperand() { return operand; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitUnaryExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Literal expression AST node (e.g., 42, 3.14, "hello", 'c', true).
 */
class LiteralExpression implements Expression {
    private final Object value;
    private final int line, column;
    private Type type;
    
    public LiteralExpression(Object value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public Object getValue() { return value; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitLiteralExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Identifier expression AST node (e.g., variable name, function name).
 */
class IdentifierExpression implements Expression {
    private final String name;
    private final int line, column;
    private Type type;
    
    public IdentifierExpression(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }
    
    public String getName() { return name; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitIdentifierExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Assignment expression AST node (e.g., x = 5, arr[i] = value).
 */
class AssignmentExpression implements Expression {
    private final Expression target;
    private final Expression value;
    private final int line, column;
    private Type type;
    
    public AssignmentExpression(Expression target, Expression value, int line, int column) {
        this.target = target;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public Expression getTarget() { return target; }
    public Expression getValue() { return value; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitAssignmentExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Function call expression AST node (e.g., func(), math.sin(x), obj.method(a, b)).
 */
class CallExpression implements Expression {
    private final Expression function; // can be IdentifierExpression or QualifiedIdentifier
    private final List<Expression> arguments;
    private final int line, column;
    private Type type;
    
    public CallExpression(Expression function, List<Expression> arguments, int line, int column) {
        this.function = function;
        this.arguments = arguments;
        this.line = line;
        this.column = column;
    }
    
    public Expression getFunction() { return function; }
    public List<Expression> getArguments() { return arguments; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitCallExpression(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

// Statement nodes

/**
 * Expression statement AST node (expression followed by semicolon).
 */
class ExpressionStatement implements Statement {
    private final Expression expression;
    private final int line, column;
    
    public ExpressionStatement(Expression expression, int line, int column) {
        this.expression = expression;
        this.line = line;
        this.column = column;
    }
    
    public Expression getExpression() { return expression; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitExpressionStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Variable declaration AST node (e.g., int x = 5;, string name;).
 */
class VariableDeclaration implements Statement {
    private final Type type;
    private final String name;
    private final Expression initializer; // can be null
    private final int line, column;
    private final boolean isPublic;
    
    public VariableDeclaration(Type type, String name, Expression initializer, boolean isPublic, int line, int column) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.isPublic = isPublic;
        this.line = line;
        this.column = column;
    }
    
    public Type getDeclaredType() { return type; }
    public String getName() { return name; }
    public Expression getInitializer() { return initializer; }
    public boolean isPublic() { return isPublic; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitVariableDeclaration(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Function declaration AST node with parameters and body.
 */
class FunctionDeclaration implements Statement {
    private final Type returnType;
    private final String name;
    private final List<Parameter> parameters;
    private final BlockStatement body;
    private final boolean isPublic;
    private final int line, column;
    
    public static class Parameter {
        public final Type type;
        public final String name;
        
        public Parameter(Type type, String name) {
            this.type = type;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return type + " " + name;
        }
    }
    
    public FunctionDeclaration(Type returnType, String name, List<Parameter> parameters, 
                             BlockStatement body, boolean isPublic, int line, int column) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.isPublic = isPublic;
        this.line = line;
        this.column = column;
    }
    
    public Type getReturnType() { return returnType; }
    public String getName() { return name; }
    public List<Parameter> getParameters() { return parameters; }
    public BlockStatement getBody() { return body; }
    public boolean isPublic() { return isPublic; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitFunctionDeclaration(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * If statement AST node (with optional else clause).
 */
class IfStatement implements Statement {
    private final Expression condition;
    private final Statement thenStmt;
    private final Statement elseStmt; // can be null
    private final int line, column;
    
    public IfStatement(Expression condition, Statement thenStmt, Statement elseStmt, int line, int column) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.line = line;
        this.column = column;
    }
    
    public Expression getCondition() { return condition; }
    public Statement getThenStatement() { return thenStmt; }
    public Statement getElseStatement() { return elseStmt; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitIfStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * While loop AST node.
 */
class WhileStatement implements Statement {
    private final Expression condition;
    private final Statement body;
    private final int line, column;
    
    public WhileStatement(Expression condition, Statement body, int line, int column) {
        this.condition = condition;
        this.body = body;
        this.line = line;
        this.column = column;
    }
    
    public Expression getCondition() { return condition; }
    public Statement getBody() { return body; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitWhileStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Return statement AST node (with optional value).
 */
class ReturnStatement implements Statement {
    private final Expression value; // can be null for void return
    private final int line, column;
    
    public ReturnStatement(Expression value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public Expression getValue() { return value; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitReturnStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

/**
 * Block statement AST node (sequence of statements in braces).
 */
class BlockStatement implements Statement {
    private final List<Statement> statements;
    private final int line, column;
    
    public BlockStatement(List<Statement> statements, int line, int column) {
        this.statements = statements;
        this.line = line;
        this.column = column;
    }
    
    public List<Statement> getStatements() { return statements; }
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visitBlockStatement(this); }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}
