package com.juno.ast;

import com.juno.types.*;
import com.juno.error.*;
import java.util.*;

/**
 * Comprehensive type checker with smart auto type inference for the Juno programming language.
 * Supports primitive types, auto/any types, optional types, union types, and complex type compatibility.
 */
public class TypeChecker implements ASTVisitor<Type> {
    
    private final SymbolTable symbolTable;
    private final ErrorCollector errorCollector;
    private Type currentFunctionReturnType; // Track return type for return statement checking
    
    public TypeChecker(ErrorCollector errorCollector) {
        this.symbolTable = new SymbolTable();
        this.errorCollector = errorCollector;
        this.currentFunctionReturnType = null;
    }
    
    public void check(Program program) {
        System.out.println("Starting comprehensive type checking with smart inference...");
        
        try {
            visitProgram(program);
            System.out.println("Type checking completed successfully");
        } catch (Exception e) {
            errorCollector.addError(new CompilerError(
                "Type checking failed: " + e.getMessage(),
                ErrorCode.TYPE_ERROR,
                0, 0
            ));
            System.err.println("Type checking failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ===== PROGRAM AND TOP-LEVEL DECLARATIONS =====
    
    @Override
    public Type visitProgram(Program program) {
        symbolTable.enterScope("program");
        
        for (Statement stmt : program.getStatements()) {
            stmt.accept(this);
        }
        
        symbolTable.exitScope();
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitModuleDeclaration(ModuleDeclaration module) {
        symbolTable.enterScope("module_" + module.getModuleName());
        
        for (Statement stmt : module.getStatements()) {
            stmt.accept(this);
        }
        
        symbolTable.exitScope();
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitImportStatement(ImportStatement importStmt) {
        // Import statements don't need type checking but could be used for module resolution
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitTypeAlias(TypeAlias alias) {
        // Type aliases define new names for existing types
        // For now, we just validate that the aliased type exists
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitStructDeclaration(StructDeclaration structDecl) {
        String structName = structDecl.getName();
        
        // Check if struct is already declared
        if (symbolTable.isDeclaredLocally(structName)) {
            errorCollector.addError(new CompilerError(
								"Struct '" + structName + "' is already declared in current scope",
								ErrorCode.DUPLICATE_DECLARATION,
								structDecl.line(), structDecl.column()
            ));
        }
        
        // TODO: Add struct type to symbol table and validate field types
        // For now, just validate that all field types are valid
        for (StructDeclaration.Field field : structDecl.getFields()) {
            // Field type validation would go here
        }
        
        return PrimitiveType.VOID;
    }
    
    // ===== VARIABLE AND FUNCTION DECLARATIONS =====
    
    @Override
    public Type visitVariableDeclaration(VariableDeclaration varDecl) {
        String varName = varDecl.name();
        Type declaredType = varDecl.getDeclaredType();
        Expression initializer = varDecl.initializer();
        
        // Check if variable is already declared in current scope
        if (symbolTable.isDeclaredLocally(varName)) {
            errorCollector.addError(new CompilerError(
								"Variable '" + varName + "' is already declared in current scope",
								ErrorCode.DUPLICATE_DECLARATION,
								varDecl.line(), varDecl.column()
            ));
            return declaredType;
        }
        
        Type finalType = declaredType;
        boolean isInitialized = false;
        
        // Handle initializer if present
        if (initializer != null) {
            Type initType = initializer.accept(this);
            isInitialized = true;
            
            // Handle auto type inference
            if (declaredType instanceof SpecialTypes.AutoType) {
                SpecialTypes.AutoType autoType = (SpecialTypes.AutoType) declaredType;
                if (initType != null && !initType.getName().equals("void")) {
                    autoType.inferType(initType);
                    finalType = initType;
                } else {
                    errorCollector.addError(new CompilerError(
												"Cannot infer type for 'auto' variable '" + varName + "' from void expression",
												ErrorCode.TYPE_INFERENCE_ERROR,
												varDecl.line(), varDecl.column()
                    ));
                }
            }
            // Handle any type - can accept any value
            else if (declaredType instanceof SpecialTypes.AnyType) {
                // 'any' can accept any non-void type
                if (initType != null && initType.getName().equals("void")) {
                    errorCollector.addError(new CompilerError(
												"Cannot assign void expression to 'any' variable '" + varName + "'",
												ErrorCode.TYPE_MISMATCH,
												varDecl.line(), varDecl.column()
                    ));
                }
            }
            // Handle regular type compatibility
            else {
                if (!isAssignmentCompatible(initType, declaredType)) {
                    errorCollector.addError(new CompilerError(
												"Cannot assign " + initType + " to variable '" + varName + "' of type " + declaredType,
												ErrorCode.TYPE_MISMATCH,
												varDecl.line(), varDecl.column()
                    ));
                }
            }
        } else {
            // No initializer - check if auto type (requires initialization)
            if (declaredType instanceof SpecialTypes.AutoType) {
                errorCollector.addError(new CompilerError(
										"'auto' variable '" + varName + "' must be initialized",
										ErrorCode.UNINITIALIZED_AUTO,
										varDecl.line(), varDecl.column()
                ));
            }
        }
        
        // Declare variable in symbol table
        try {
            symbolTable.declareVariable(varName, finalType, true, isInitialized,
																				varDecl.line(), varDecl.column());
        } catch (IllegalArgumentException e) {
            errorCollector.addError(new CompilerError(
								e.getMessage(),
								ErrorCode.DUPLICATE_DECLARATION,
								varDecl.line(), varDecl.column()
            ));
        }
        
        return finalType;
    }
    
    @Override
    public Type visitFunctionDeclaration(FunctionDeclaration funcDecl) {
        String funcName = funcDecl.name();
        Type returnType = funcDecl.returnType();
        
        // Check if function is already declared in current scope
        if (symbolTable.isDeclaredLocally(funcName)) {
            errorCollector.addError(new CompilerError(
								"Function '" + funcName + "' is already declared in current scope",
								ErrorCode.DUPLICATE_DECLARATION,
								funcDecl.line(), funcDecl.column()
            ));
            return returnType;
        }
        
        // Declare function in current scope
        try {
            symbolTable.declareFunction(funcName, returnType, true,
																				funcDecl.line(), funcDecl.column());
        } catch (IllegalArgumentException e) {
            errorCollector.addError(new CompilerError(
								e.getMessage(),
								ErrorCode.DUPLICATE_DECLARATION,
								funcDecl.line(), funcDecl.column()
            ));
        }
        
        // Enter function scope for parameter and body checking
        symbolTable.enterScope("function_" + funcName);
        Type previousReturnType = currentFunctionReturnType;
        currentFunctionReturnType = returnType;
        
        // Declare parameters in function scope
        for (FunctionDeclaration.Parameter param : funcDecl.parameters()) {
            try {
                symbolTable.declareVariable(param.name(), param.type(), true, true,
																						funcDecl.line(), funcDecl.column());
            } catch (IllegalArgumentException e) {
                errorCollector.addError(new CompilerError(
										"Parameter '" + param.name() + "' is already declared",
										ErrorCode.DUPLICATE_DECLARATION,
										funcDecl.line(), funcDecl.column()
                ));
            }
        }
        
        // Type check function body
        if (funcDecl.body() != null) {
            funcDecl.body().accept(this);
        }
        
        // Restore previous state
        currentFunctionReturnType = previousReturnType;
        symbolTable.exitScope();
        
        return returnType;
    }
    
    // ===== STATEMENTS =====
    
    @Override
    public Type visitBlockStatement(BlockStatement block) {
        symbolTable.enterScope("block");
        
        for (Statement stmt : block.statements()) {
            stmt.accept(this);
        }
        
        symbolTable.exitScope();
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitExpressionStatement(ExpressionStatement exprStmt) {
        exprStmt.expression().accept(this);
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitIfStatement(IfStatement ifStmt) {
        Type conditionType = ifStmt.condition().accept(this);
        
        // Check that condition is boolean
        if (!isCompatible(conditionType, PrimitiveType.BOOL)) {
            errorCollector.addError(new CompilerError(
								"If condition must be boolean, got " + conditionType,
								ErrorCode.TYPE_MISMATCH,
								ifStmt.line(), ifStmt.column()
            ));
        }
        
        // Type check then and else branches
        ifStmt.getThenStatement().accept(this);
        if (ifStmt.getElseStatement() != null) {
            ifStmt.getElseStatement().accept(this);
        }
        
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitWhileStatement(WhileStatement whileStmt) {
        Type conditionType = whileStmt.condition().accept(this);
        
        // Check that condition is boolean
        if (!isCompatible(conditionType, PrimitiveType.BOOL)) {
            errorCollector.addError(new CompilerError(
								"While condition must be boolean, got " + conditionType,
								ErrorCode.TYPE_MISMATCH,
								whileStmt.line(), whileStmt.column()
            ));
        }
        
        // Type check loop body
        whileStmt.body().accept(this);
        
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitForInStatement(ForInStatement forStmt) {
        // Enter new scope for loop variable
        symbolTable.enterScope("for_loop");
        
        // Handle loop variable declaration
        String varName = forStmt.variableName();
        Type varType = forStmt.variableType();
        
        // Handle optional initializer
        if (forStmt.initializer() != null) {
            Type initType = forStmt.initializer().accept(this);
            if (!isCompatible(initType, varType)) {
                errorCollector.addError(new CompilerError(
										"For-in loop initializer type " + initType + " is not compatible with variable type " + varType,
										ErrorCode.TYPE_MISMATCH,
										forStmt.line(), forStmt.column()
                ));
            }
        }
        
        // Declare loop variable
        try {
            symbolTable.declareVariable(varName, varType, true, true,
																				forStmt.line(), forStmt.column());
        } catch (IllegalArgumentException e) {
            errorCollector.addError(new CompilerError(
								e.getMessage(),
								ErrorCode.DUPLICATE_DECLARATION,
								forStmt.line(), forStmt.column()
            ));
        }
        
        // Type check iterable expression
        Type iterableType = forStmt.iterable().accept(this);
        // TODO: Add proper iterable type checking when we have collections
        
        // Type check loop body
        forStmt.body().accept(this);
        
        symbolTable.exitScope();
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitReturnStatement(ReturnStatement returnStmt) {
        if (currentFunctionReturnType == null) {
            errorCollector.addError(new CompilerError(
								"Return statement outside of function",
								ErrorCode.RETURN_OUTSIDE_FUNCTION,
								returnStmt.line(), returnStmt.column()
            ));
            return PrimitiveType.VOID;
        }
        
        if (returnStmt.value() != null) {
            Type valueType = returnStmt.value().accept(this);
            if (!isCompatible(valueType, currentFunctionReturnType)) {
                errorCollector.addError(new CompilerError(
										"Return type " + valueType + " is not compatible with function return type " + currentFunctionReturnType,
										ErrorCode.TYPE_MISMATCH,
										returnStmt.line(), returnStmt.column()
                ));
            }
        } else {
            // Return with no value - function should return void
            if (!currentFunctionReturnType.getName().equals("void")) {
                errorCollector.addError(new CompilerError(
										"Function with return type " + currentFunctionReturnType + " must return a value",
										ErrorCode.MISSING_RETURN_VALUE,
										returnStmt.line(), returnStmt.column()
                ));
            }
        }
        
        return PrimitiveType.VOID;
    }
    
    // ===== EXPRESSIONS =====
    
    @Override
    public Type visitBinaryExpression(BinaryExpression expr) {
        Type leftType = expr.getLeft().accept(this);
        Type rightType = expr.getRight().accept(this);
        String operator = expr.getOperator();
        
        // Set type on the expression node
        Type resultType = getBinaryOperatorResultType(leftType, rightType, operator);
        expr.setType(resultType);
        
        if (resultType == null) {
            errorCollector.addError(new CompilerError(
								"Invalid binary operation: " + leftType + " " + operator + " " + rightType,
								ErrorCode.TYPE_MISMATCH,
								expr.line(), expr.column()
            ));
            return PrimitiveType.INT; // Return a reasonable default
        }
        
        return resultType;
    }
    
    @Override
    public Type visitUnaryExpression(UnaryExpression expr) {
        Type operandType = expr.getOperand().accept(this);
        String operator = expr.getOperator();
        
        Type resultType = getUnaryOperatorResultType(operandType, operator);
        expr.setType(resultType);
        
        if (resultType == null) {
            errorCollector.addError(new CompilerError(
								"Invalid unary operation: " + operator + operandType,
								ErrorCode.TYPE_MISMATCH,
								expr.line(), expr.column()
            ));
            return operandType; // Return operand type as fallback
        }
        
        return resultType;
    }
    
    @Override
    public Type visitLiteralExpression(LiteralExpression expr) {
        Object value = expr.getValue();
        Type type = inferLiteralType(value);
        expr.setType(type);
        return type;
    }
    
    @Override
    public Type visitIdentifierExpression(IdentifierExpression expr) {
        String name = expr.getName();
        SymbolTable.Symbol symbol = symbolTable.lookup(name);
        
        if (symbol == null) {
            errorCollector.addError(new CompilerError(
								"Undefined identifier '" + name + "'",
								ErrorCode.UNDEFINED_IDENTIFIER,
								expr.line(), expr.column()
            ));
            return PrimitiveType.INT; // Return a reasonable default
        }
        
        // Check if variable is used before initialization
        if (!symbol.isFunction() && !symbol.isInitialized()) {
            errorCollector.addError(new CompilerError(
								"Variable '" + name + "' used before initialization",
								ErrorCode.UNINITIALIZED_VARIABLE,
								expr.line(), expr.column()
            ));
        }
        
        Type type = symbol.getType();
        expr.setType(type);
        return type;
    }
    
    @Override
    public Type visitAssignmentExpression(AssignmentExpression expr) {
        Type targetType = expr.getTarget().accept(this);
        Type valueType = expr.getValue().accept(this);
        
        // Check assignment compatibility
        if (!isAssignmentCompatible(valueType, targetType)) {
            errorCollector.addError(new CompilerError(
								"Cannot assign " + valueType + " to " + targetType,
								ErrorCode.TYPE_MISMATCH,
								expr.line(), expr.column()
            ));
        }
        
        // Handle special 'any' type assignment
        if (targetType instanceof SpecialTypes.AnyType && expr.getTarget() instanceof IdentifierExpression) {
            // Update symbol table with new type for 'any' variables
            IdentifierExpression target = (IdentifierExpression) expr.getTarget();
            symbolTable.updateSymbolType(target.getName(), valueType);
            symbolTable.markInitialized(target.getName());
        }
        
        expr.setType(targetType);
        return targetType;
    }
    
    @Override
    public Type visitCallExpression(CallExpression expr) {
        // Get function name and look up its type
        Expression functionExpr = expr.getFunction();
        Type functionType;
        
        if (functionExpr instanceof IdentifierExpression) {
            String functionName = ((IdentifierExpression) functionExpr).getName();
            SymbolTable.Symbol symbol = symbolTable.lookup(functionName);
            
            if (symbol == null) {
                errorCollector.addError(new CompilerError(
										"Undefined function '" + functionName + "'",
										ErrorCode.UNDEFINED_IDENTIFIER,
										expr.line(), expr.column()
                ));
                return PrimitiveType.VOID;
            }
            
            if (!symbol.isFunction()) {
                errorCollector.addError(new CompilerError(
										"'" + functionName + "' is not a function",
										ErrorCode.NOT_A_FUNCTION,
										expr.line(), expr.column()
                ));
                return PrimitiveType.VOID;
            }
            
            functionType = symbol.getType(); // This is the return type
        } else if (functionExpr instanceof QualifiedIdentifier) {
            // Handle qualified function calls like module.function()
            functionType = functionExpr.accept(this);
        } else {
            errorCollector.addError(new CompilerError(
								"Invalid function call expression",
								ErrorCode.INVALID_EXPRESSION,
								expr.line(), expr.column()
            ));
            return PrimitiveType.VOID;
        }
        
        // Type check arguments
        for (Expression arg : expr.getArguments()) {
            arg.accept(this); // Check argument types
        }
        
        // TODO: Add parameter type checking when we have function signatures
        
        expr.setType(functionType);
        return functionType;
    }
    
    @Override
    public Type visitQualifiedIdentifier(QualifiedIdentifier expr) {
        String moduleName = expr.getModuleName();
        String identifier = expr.getIdentifier();
        
        // Handle runtime modules
        if ("Io".equals(moduleName)) {
            return resolveIoFunction(identifier);
        }
        
        // TODO: Add other modules as needed
        errorCollector.addError(new CompilerError(
						"Unknown module: " + moduleName,
						ErrorCode.UNDEFINED_IDENTIFIER,
						expr.line(), expr.column()
        ));
        return PrimitiveType.VOID;
    }
    
    private Type resolveIoFunction(String function) {
        switch (function) {
            case "print":
            case "println":
            case "report":
                return PrimitiveType.VOID;
            case "scan":
                return PrimitiveType.STRING;
            default:
                return PrimitiveType.VOID;
        }
    }
    
    @Override
    public Type visitCastExpression(CastExpression expr) {
        Type sourceType = expr.getExpression().accept(this);
        Type targetType = expr.getTargetType();
        
        // Check if cast is valid
        if (!isCastValid(sourceType, targetType)) {
            errorCollector.addError(new CompilerError(
								"Invalid cast from " + sourceType + " to " + targetType,
								ErrorCode.INVALID_CAST,
								expr.line(), expr.column()
            ));
        }
        
        expr.setType(targetType);
        return targetType;
    }
    
    @Override
    public Type visitArrayLiteralExpression(ArrayLiteralExpression expr) {
        List<Expression> elements = expr.getElements();
        
        if (elements.isEmpty()) {
            // Empty array - need to infer type from context or use default
            // For now, default to int[] for empty arrays
            Type arrayType = new ArrayType(PrimitiveType.INT);
            expr.setType(arrayType);
            return arrayType;
        }
        
        // Type check all elements and find common type
        Type commonType = null;
        for (Expression element : elements) {
            Type elementType = element.accept(this);
            if (commonType == null) {
                commonType = elementType;
            } else if (!commonType.equals(elementType)) {
                // Elements have different types - check if they're compatible
                if (!isCompatible(elementType, commonType) && !isCompatible(commonType, elementType)) {
                    errorCollector.addError(new CompilerError(
												"Array literal has incompatible element types: " + commonType + " and " + elementType,
												ErrorCode.TYPE_MISMATCH,
												expr.line(), expr.column()
                    ));
                    commonType = PrimitiveType.INT; // fallback
                    break;
                }
                // Use the more general type if compatible
                if (isCompatible(commonType, elementType)) {
                    commonType = elementType;
                }
            }
        }
        
        Type arrayType = new ArrayType(commonType, elements.size()); // Fixed-size array from literal
        expr.setType(arrayType);
        return arrayType;
    }
    
    @Override
    public Type visitArrayIndexExpression(ArrayIndexExpression expr) {
        Type arrayType = expr.getArray().accept(this);
        Type indexType = expr.getIndex().accept(this);
        
        // Check that the array expression is actually an array type
        if (!(arrayType instanceof ArrayType)) {
            errorCollector.addError(new CompilerError(
								"Cannot index non-array type: " + arrayType,
								ErrorCode.TYPE_MISMATCH,
								expr.line(), expr.column()
            ));
            return PrimitiveType.INT; // fallback
        }
        
        // Check that index is integer type
        if (!(indexType instanceof PrimitiveType) || !((PrimitiveType) indexType).isInteger()) {
            errorCollector.addError(new CompilerError(
								"Array index must be integer type, got " + indexType,
								ErrorCode.TYPE_MISMATCH,
								expr.line(), expr.column()
            ));
        }
        
        ArrayType array = (ArrayType) arrayType;
        Type elementType = array.getElementType();
        expr.setType(elementType);
        return elementType;
    }
    
    @Override
    public Type visitBreakStatement(BreakStatement stmt) {
        // TODO: Check that break is inside a loop
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitContinueStatement(ContinueStatement stmt) {
        // TODO: Check that continue is inside a loop
        return PrimitiveType.VOID;
    }
    
    @Override
    public Type visitAddressOfExpression(AddressOfExpression expr) {
        Type operandType = expr.getOperand().accept(this);
        
        // Check that operand is an lvalue (can be addressed)
        if (!isLValue(expr.getOperand())) {
            errorCollector.addError(new CompilerError(
								"Cannot take address of non-lvalue expression",
								ErrorCode.TYPE_ERROR,
								expr.line(), expr.column()
            ));
        }
        
        PointerType pointerType = new PointerType(operandType);
        expr.setType(pointerType);
        return pointerType;
    }
    
    @Override
    public Type visitDereferenceExpression(DereferenceExpression expr) {
        Type operandType = expr.getOperand().accept(this);
        
        // Check that operand is a pointer type
        if (!(operandType instanceof PointerType)) {
            errorCollector.addError(new CompilerError(
								"Cannot dereference non-pointer type: " + operandType,
								ErrorCode.TYPE_ERROR,
								expr.line(), expr.column()
            ));
            return PrimitiveType.INT; // fallback
        }
        
        PointerType pointerType = (PointerType) operandType;
        Type pointedType = pointerType.getPointedType();
        expr.setType(pointedType);
        return pointedType;
    }
    
    /**
     * Check if an expression is an lvalue (can be assigned to or addressed).
     */
    private boolean isLValue(Expression expr) {
        return expr instanceof IdentifierExpression ||
               expr instanceof ArrayIndexExpression ||
               expr instanceof DereferenceExpression;
    }
    
    // ===== TYPE COMPATIBILITY AND UTILITY METHODS =====
    
    /**
     * Check if two types are compatible for general operations.
     */
    private boolean isCompatible(Type fromType, Type toType) {
        if (fromType == null || toType == null) {
            return false;
        }
        
        // Exact match
        if (fromType.equals(toType)) {
            return true;
        }
        
        // Special handling for numeric type coercion
        if (fromType instanceof PrimitiveType && toType instanceof PrimitiveType) {
            PrimitiveType fromPrim = (PrimitiveType) fromType;
            PrimitiveType toPrim = (PrimitiveType) toType;
            
            // Allow numeric narrowing conversions for literals
            if (fromPrim.isNumeric() && toPrim.isNumeric()) {
                return true; // Allow all numeric conversions for now
            }
        }
        
        // Use built-in compatibility method
        return fromType.isCompatibleWith(toType);
    }
    
    /**
     * Check if assignment is compatible (more lenient than general compatibility).
     */
    private boolean isAssignmentCompatible(Type fromType, Type toType) {
        if (fromType == null || toType == null) {
            return false;
        }
        
        // Any type can accept anything except void
        if (toType instanceof SpecialTypes.AnyType) {
            return !fromType.getName().equals("void");
        }
        
        // Optional types can accept their wrapped type or null
        if (toType instanceof OptionalType) {
            OptionalType optType = (OptionalType) toType;
            return isCompatible(fromType, optType.getWrappedType()) || 
                   fromType.getName().equals("null");
        }
        
        // Union types can accept any of their constituent types
        if (toType instanceof UnionType) {
            UnionType unionType = (UnionType) toType;
            return unionType.canAccept(fromType);
        }
        
        // Regular compatibility check
        return isCompatible(fromType, toType);
    }
    
    /**
     * Get the result type of a binary operation.
     */
    private Type getBinaryOperatorResultType(Type leftType, Type rightType, String operator) {
        if (leftType == null || rightType == null) {
            return null;
        }
        
        switch (operator) {
            // Arithmetic operators
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
                return getArithmeticResultType(leftType, rightType);
            
            // Comparison operators
            case "<":
            case "<=":
            case ">":
            case ">=":
                if (isNumericCompatible(leftType, rightType)) {
                    return PrimitiveType.BOOL;
                }
                return null;
            
            // Equality operators
            case "==":
            case "!=":
                if (isCompatible(leftType, rightType) || isCompatible(rightType, leftType)) {
                    return PrimitiveType.BOOL;
                }
                return null;
            
            // Logical operators
            case "&&":
            case "||":
                if (isCompatible(leftType, PrimitiveType.BOOL) && isCompatible(rightType, PrimitiveType.BOOL)) {
                    return PrimitiveType.BOOL;
                }
                return null;
            
            // String concatenation
            case "^^":
                if (isCompatible(leftType, PrimitiveType.STRING) || isCompatible(rightType, PrimitiveType.STRING)) {
                    return PrimitiveType.STRING;
                }
                return null;
            
            default:
                return null;
        }
    }
    
    /**
     * Get the result type of a unary operation.
     */
    private Type getUnaryOperatorResultType(Type operandType, String operator) {
        if (operandType == null) {
            return null;
        }
        
        switch (operator) {
            case "-":
            case "+":
                if (((PrimitiveType) operandType).isNumeric()) {
                    return operandType;
                }
                return null;
            
            case "!":
                if (isCompatible(operandType, PrimitiveType.BOOL)) {
                    return PrimitiveType.BOOL;
                }
                return null;
            
            default:
                return null;
        }
    }
    
    /**
     * Get the result type of arithmetic operations with STRICT type matching.
     * Juno requires explicit casting - no implicit conversions allowed.
     */
    private Type getArithmeticResultType(Type leftType, Type rightType) {
        if (!(leftType instanceof PrimitiveType) || !(rightType instanceof PrimitiveType)) {
            return null;
        }
        
        PrimitiveType left = (PrimitiveType) leftType;
        PrimitiveType right = (PrimitiveType) rightType;
        
        if (!left.isNumeric() || !right.isNumeric()) {
            return null;
        }
        
        // STRICT TYPE MATCHING: Both operands must be exactly the same type
        // No implicit conversions allowed - explicit casting required
        if (!left.equals(right)) {
            return null;  // Type error - requires explicit cast
        }
        
        // Both types are the same, return that type
        return left;
    }
    
    /**
     * Check if two numeric types are compatible for comparison.
     */
    private boolean isNumericCompatible(Type left, Type right) {
        if (!(left instanceof PrimitiveType) || !(right instanceof PrimitiveType)) {
            return false;
        }
        
        PrimitiveType leftPrim = (PrimitiveType) left;
        PrimitiveType rightPrim = (PrimitiveType) right;
        
        return leftPrim.isNumeric() && rightPrim.isNumeric();
    }
    
    /**
     * Infer the type of a literal value.
     */
    private Type inferLiteralType(Object value) {
        if (value instanceof Integer) {
            return PrimitiveType.INT;
        } else if (value instanceof Long) {
            return PrimitiveType.LONG;
        } else if (value instanceof Float) {
            return PrimitiveType.FLOAT;
        } else if (value instanceof Double) {
            return PrimitiveType.DOUBLE;
        } else if (value instanceof Boolean) {
            return PrimitiveType.BOOL;
        } else if (value instanceof String) {
            return PrimitiveType.STRING;
        } else if (value instanceof Character) {
            return PrimitiveType.CHAR;
        } else {
            return PrimitiveType.INT; // Default fallback
        }
    }
    
    /**
     * Check if a cast is valid.
     * Implements comprehensive cast validation rules for Juno language.
     */
    private boolean isCastValid(Type fromType, Type toType) {
        if (fromType == null || toType == null) {
            return false;
        }
        
        String fromTypeName = fromType.getName();
        String toTypeName = toType.getName();
        
        // Can't cast from/to void
        if ("void".equals(fromTypeName) || "void".equals(toTypeName)) {
            return false;
        }
        
        // Same type casts are always valid (but unnecessary)
        if (fromTypeName.equals(toTypeName)) {
            return true;
        }
        
        // Handle primitive type casts
        if (fromType instanceof PrimitiveType && toType instanceof PrimitiveType) {
            return isPrimitiveCastValid(fromTypeName, toTypeName);
        }
        
        // Handle object type casts (for future use)
        // For now, allow all object casts with runtime checking
        if (!(fromType instanceof PrimitiveType) && !(toType instanceof PrimitiveType)) {
            return true; // Object to object casts (implement proper inheritance checking later)
        }
        
        // Primitive to object or object to primitive casts
        // Generally not allowed without boxing/unboxing (implement later if needed)
        return false;
    }
    
    /**
     * Check if a primitive type cast is valid.
     */
    private boolean isPrimitiveCastValid(String fromTypeName, String toTypeName) {
        // All numeric type conversions are allowed
        if (isNumericTypeName(fromTypeName) && isNumericTypeName(toTypeName)) {
            return true;
        }
        
        // Boolean to numeric conversions
        if ("bool".equals(fromTypeName) && isNumericTypeName(toTypeName)) {
            return true; // bool can be cast to any numeric type (0 or 1)
        }
        if (isNumericTypeName(fromTypeName) && "bool".equals(toTypeName)) {
            return true; // numeric can be cast to bool (0 = false, non-zero = true)
        }
        
        // Character to numeric conversions
        if ("char".equals(fromTypeName) && isNumericTypeName(toTypeName)) {
            return true; // char can be cast to numeric types (Unicode value)
        }
        if (isNumericTypeName(fromTypeName) && "char".equals(toTypeName)) {
            return true; // numeric can be cast to char (truncated to char range)
        }
        
        // Character to boolean conversions
        if ("char".equals(fromTypeName) && "bool".equals(toTypeName)) {
            return true; // char can be cast to bool (0 = false, non-zero = true)
        }
        if ("bool".equals(fromTypeName) && "char".equals(toTypeName)) {
            return true; // bool can be cast to char (0 or 1)
        }
        
        // String conversions
        if ("string".equals(toTypeName)) {
            // Allow all primitive types to be cast to string (toString conversion)
            return true;
        }
        
        if ("string".equals(fromTypeName)) {
            // String to other types requires explicit parsing (generally not allowed as cast)
            // This maintains type safety - strings need explicit parsing methods
            return false;
        }
        
        // All other combinations are invalid
        return false;
    }
    
    /**
     * Check if a type name represents a numeric type.
     */
    private boolean isNumericTypeName(String typeName) {
        return "int".equals(typeName) || "uint".equals(typeName) ||
               "long".equals(typeName) || "ulong".equals(typeName) ||
               "byte".equals(typeName) || "ubyte".equals(typeName) ||
               "short".equals(typeName) || "ushort".equals(typeName) ||
               "float".equals(typeName) || "double".equals(typeName);
    }
    
    /**
     * Get the symbol table for debugging.
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
