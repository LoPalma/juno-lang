package com.clikejvm.ast;

/**
 * Pretty prints AST nodes in parenthesized syntax format.
 * This is a standalone utility class for demonstration purposes.
 */
public class ASTPrettyPrinter {
    private int indentLevel = 0;
    private static final String INDENT = "  ";
    
    private String indent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append(INDENT);
        }
        return sb.toString();
    }
    
    private String withIndent(String content) {
        indentLevel++;
        String result = content;
        indentLevel--;
        return result;
    }
    
    public String visitProgram(Program program) {
        if (program.getStatements().isEmpty()) {
            return "(program)";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("(program\n");
        indentLevel++;
        
        for (Statement stmt : program.getStatements()) {
            sb.append(indent()).append("<statement>").append("\n");
        }
        
        indentLevel--;
        sb.append(")");
        return sb.toString();
    }
    
    public String visitBinaryExpression(BinaryExpression binaryExpr) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(binaryExpr.getOperator()).append("\n");
        
        indentLevel++;
        sb.append(indent()).append("<left>").append("\n");
        sb.append(indent()).append("<right>").append("\n");
        indentLevel--;
        
        sb.append(indent()).append(")");
        return sb.toString();
    }
    
    // For now, we'll create a simple literal expression since we need basic functionality
    public String visitLiteral(Object value, String inferredType) {
        return String.format("(literal\n%s  type: %s\n%s  value: %s)", 
                           indent(), inferredType, indent(), value);
    }
    
    public String visitBinaryOperation(String operator, String left, String right) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(operator).append("\n");
        
        indentLevel++;
        sb.append(indent()).append(left).append("\n");
        sb.append(indent()).append(right);
        indentLevel--;
        
        sb.append("\n").append(indent()).append(")");
        return sb.toString();
    }
    
    // Placeholder implementations for other AST nodes
    public String visitIdentifier(String name) {
        return String.format("(identifier\n%s  name: %s)", indent(), name);
    }
    
    public String visitFunctionCall(String functionName, java.util.List<String> arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append("(call\n");
        indentLevel++;
        sb.append(indent()).append("function: ").append(functionName).append("\n");
        
        if (!arguments.isEmpty()) {
            sb.append(indent()).append("arguments:\n");
            indentLevel++;
            for (String arg : arguments) {
                sb.append(indent()).append(arg).append("\n");
            }
            indentLevel--;
        }
        
        indentLevel--;
        sb.append(indent()).append(")");
        return sb.toString();
    }
    
    public String visitVariableDeclaration(String type, String name, String initializer) {
        StringBuilder sb = new StringBuilder();
        sb.append("(var-decl\n");
        indentLevel++;
        sb.append(indent()).append("type: ").append(type).append("\n");
        sb.append(indent()).append("name: ").append(name);
        
        if (initializer != null) {
            sb.append("\n").append(indent()).append("init: ").append(initializer);
        }
        
        indentLevel--;
        sb.append("\n").append(indent()).append(")");
        return sb.toString();
    }
    
    public String visitAssignment(String variable, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("(assign\n");
        indentLevel++;
        sb.append(indent()).append("var: ").append(variable).append("\n");
        sb.append(indent()).append("value: ").append(value);
        indentLevel--;
        sb.append("\n").append(indent()).append(")");
        return sb.toString();
    }
    
    public String visitIfStatement(String condition, String thenBranch, String elseBranch) {
        StringBuilder sb = new StringBuilder();
        sb.append("(if\n");
        indentLevel++;
        sb.append(indent()).append("condition: ").append(condition).append("\n");
        sb.append(indent()).append("then: ").append(thenBranch);
        
        if (elseBranch != null) {
            sb.append("\n").append(indent()).append("else: ").append(elseBranch);
        }
        
        indentLevel--;
        sb.append("\n").append(indent()).append(")");
        return sb.toString();
    }
    
    public String visitBlock(java.util.List<String> statements) {
        if (statements.isEmpty()) {
            return "(block)";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("(block\n");
        indentLevel++;
        
        for (String stmt : statements) {
            sb.append(indent()).append(stmt).append("\n");
        }
        
        indentLevel--;
        sb.append(indent()).append(")");
        return sb.toString();
    }
}