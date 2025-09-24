package com.juno.ast;

/**
 * Debug printer that shows the parsed AST structure using the visitor pattern.
 * This helps visualize what the parser actually produced.
 */
public class ASTDebugPrinter implements ASTVisitor<String> {
	private int indentLevel = 0;
	private static final String INDENT = "  ";

	private String indent() {
		StringBuilder sb = new StringBuilder();
		sb.append(INDENT.repeat(Math.max(0, indentLevel)));
		return sb.toString();
	}

	private String withIndent(String result) {
		indentLevel++;
		indentLevel--;
		return result;
	}

	@Override
	public String visitProgram(Program program) {
		StringBuilder sb = new StringBuilder();
		sb.append("Program {\n");
		indentLevel++;

		for (Statement stmt : program.getStatements()) {
			sb.append(indent()).append(stmt.accept(this)).append("\n");
		}

		indentLevel--;
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String visitBinaryExpression(BinaryExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("BinaryExpr(").append(expr.getOperator()).append(") {\n");

		indentLevel++;
		sb.append(indent()).append("left: ").append(expr.getLeft().accept(this)).append("\n");
		sb.append(indent()).append("right: ").append(expr.getRight().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitUnaryExpression(UnaryExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("UnaryExpr(").append(expr.getOperator()).append(") {\n");

		indentLevel++;
		sb.append(indent()).append("operand: ").append(expr.getOperand().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitLiteralExpression(LiteralExpression expr) {
		return "Literal(" + getTypeName(expr.getValue()) + ", " + formatValue(expr.getValue()) + ")";
	}

	@Override
	public String visitIdentifierExpression(IdentifierExpression expr) {
		return "Identifier(\"" + expr.getName() + "\")";
	}

	@Override
	public String visitAssignmentExpression(AssignmentExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("Assignment {\n");

		indentLevel++;
		sb.append(indent()).append("target: ").append(expr.getTarget().accept(this)).append("\n");
		sb.append(indent()).append("value: ").append(expr.getValue().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitCallExpression(CallExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("Call {\n");

		indentLevel++;
		sb.append(indent()).append("function: ").append(expr.getFunction().accept(this)).append("\n");
		sb.append(indent()).append("args: [");

		if (!expr.getArguments().isEmpty()) {
			sb.append("\n");
			indentLevel++;
			for (int i = 0; i < expr.getArguments().size(); i++) {
				sb.append(indent()).append(expr.getArguments().get(i).accept(this));
				if (i < expr.getArguments().size() - 1) sb.append(",");
				sb.append("\n");
			}
			indentLevel--;
			sb.append(indent());
		}
		sb.append("]");

		indentLevel--;
		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitQualifiedIdentifier(QualifiedIdentifier expr) {
		return "QualifiedId(\"" + expr.getModuleName() + "." + expr.getIdentifier() + "\")";
	}

	@Override
	public String visitCastExpression(CastExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("Cast(").append(expr.getTargetType()).append(") {\n");

		indentLevel++;
		sb.append(indent()).append("expr: ").append(expr.getExpression().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitArrayLiteralExpression(ArrayLiteralExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("ArrayLiteral [");

		if (!expr.getElements().isEmpty()) {
			sb.append("\n");
			indentLevel++;
			for (int i = 0; i < expr.getElements().size(); i++) {
				sb.append(indent()).append(expr.getElements().get(i).accept(this));
				if (i < expr.getElements().size() - 1) sb.append(",");
				sb.append("\n");
			}
			indentLevel--;
			sb.append(indent());
		}
		sb.append("]");

		return sb.toString();
	}

	@Override
	public String visitArrayIndexExpression(ArrayIndexExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("ArrayIndex {\n");

		indentLevel++;
		sb.append(indent()).append("array: ").append(expr.getArray().accept(this)).append("\n");
		sb.append(indent()).append("index: ").append(expr.getIndex().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitExpressionStatement(ExpressionStatement stmt) {
		return "ExprStmt { " + stmt.expression().accept(this) + " }";
	}

	@Override
	public String visitVariableDeclaration(VariableDeclaration stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("VarDecl");
		if (stmt.isPublic()) sb.append("(public)");
		sb.append(" {\n");

		indentLevel++;
		sb.append(indent()).append("type: ").append(stmt.getDeclaredType()).append("\n");
		sb.append(indent()).append("name: \"").append(stmt.name()).append("\"");

		if (stmt.initializer() != null) {
			sb.append("\n").append(indent()).append("init: ").append(stmt.initializer().accept(this));
		}

		indentLevel--;
		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitFunctionDeclaration(FunctionDeclaration stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("FuncDecl");
		if (stmt.isPublic()) sb.append("(public)");
		sb.append(" {\n");

		indentLevel++;
		sb.append(indent()).append("return: ").append(stmt.returnType()).append("\n");
		sb.append(indent()).append("name: \"").append(stmt.name()).append("\"\n");
		sb.append(indent()).append("params: [");

		if (!stmt.parameters().isEmpty()) {
			sb.append("\n");
			indentLevel++;
			for (FunctionDeclaration.Parameter param : stmt.parameters()) {
				sb.append(indent()).append(param.type()).append(" ").append(param.name()).append("\n");
			}
			indentLevel--;
			sb.append(indent());
		}
		sb.append("]\n");

		sb.append(indent()).append("body: ").append(stmt.body().accept(this));

		indentLevel--;
		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitIfStatement(IfStatement stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("If {\n");

		indentLevel++;
		sb.append(indent()).append("condition: ").append(stmt.condition().accept(this)).append("\n");
		sb.append(indent()).append("then: ").append(stmt.getThenStatement().accept(this));

		if (stmt.getElseStatement() != null) {
			sb.append("\n").append(indent()).append("else: ").append(stmt.getElseStatement().accept(this));
		}

		indentLevel--;
		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitWhileStatement(WhileStatement stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("While {\n");

		indentLevel++;
		sb.append(indent()).append("condition: ").append(stmt.condition().accept(this)).append("\n");
		sb.append(indent()).append("body: ").append(stmt.body().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitForInStatement(ForInStatement stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("ForIn {\n");

		indentLevel++;
		sb.append(indent()).append("variable: ").append(stmt.variableType()).append(" ").append(stmt.variableName());

		if (stmt.initializer() != null) {
			sb.append(" = ").append(stmt.initializer().accept(this));
		}

		sb.append("\n").append(indent()).append("iterable: ").append(stmt.iterable().accept(this));
		sb.append("\n").append(indent()).append("body: ").append(stmt.body().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitReturnStatement(ReturnStatement stmt) {
		if (stmt.value() == null) {
			return "Return { }";
		}
		return "Return { " + stmt.value().accept(this) + " }";
	}

	@Override
	public String visitBlockStatement(BlockStatement stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("Block {\n");

		indentLevel++;
		for (Statement s : stmt.statements()) {
			sb.append(indent()).append(s.accept(this)).append("\n");
		}
		indentLevel--;

		sb.append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitImportStatement(ImportStatement stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("Import {\n");

		indentLevel++;
		sb.append(indent()).append("module: \"").append(stmt.getModuleName()).append("\"");

		if (!stmt.isWildcardImport()) {
			sb.append("\n").append(indent()).append("items: [");
			for (int i = 0; i < stmt.getImportedItems().size(); i++) {
				sb.append("\"").append(stmt.getImportedItems().get(i)).append("\"");
				if (i < stmt.getImportedItems().size() - 1) sb.append(", ");
			}
			sb.append("]");
		}

		indentLevel--;
		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitModuleDeclaration(ModuleDeclaration stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("Module(\"").append(stmt.getModuleName()).append("\") {\n");

		indentLevel++;
		for (Statement s : stmt.getStatements()) {
			sb.append(indent()).append(s.accept(this)).append("\n");
		}
		indentLevel--;

		sb.append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitTypeAlias(TypeAlias stmt) {
		return "TypeAlias { name: \"" + stmt.aliasName() + "\", type: " + stmt.aliasedType() + " }";
	}

	@Override
	public String visitStructDeclaration(StructDeclaration stmt) {
		StringBuilder sb = new StringBuilder();
		sb.append("StructDecl");
		if (stmt.isPublic()) sb.append("(public)");
		sb.append(" {\n");

		indentLevel++;
		sb.append(indent()).append("name: \"").append(stmt.getName()).append("\"\n");
		sb.append(indent()).append("fields: [\n");

		indentLevel++;
		for (StructDeclaration.Field field : stmt.getFields()) {
			sb.append(indent()).append(field.type).append(" ").append(field.name).append("\n");
		}
		indentLevel--;

		sb.append(indent()).append("]\n");
		indentLevel--;

		sb.append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitBreakStatement(BreakStatement stmt) {
		return "Break { }";
	}

	@Override
	public String visitContinueStatement(ContinueStatement stmt) {
		return "Continue { }";
	}

	@Override
	public String visitAddressOfExpression(AddressOfExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("AddressOf(&) {\n");

		indentLevel++;
		sb.append(indent()).append("operand: ").append(expr.getOperand().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	@Override
	public String visitDereferenceExpression(DereferenceExpression expr) {
		StringBuilder sb = new StringBuilder();
		sb.append("Dereference(*) {\n");

		indentLevel++;
		sb.append(indent()).append("operand: ").append(expr.getOperand().accept(this));
		indentLevel--;

		sb.append("\n").append(indent()).append("}");
		return sb.toString();
	}

	private String getTypeName(Object value) {
		if (value == null) return "null";
		if (value instanceof Integer) return "int";
		if (value instanceof Long) return "long";
		if (value instanceof Float) return "float";
		if (value instanceof Double) return "double";
		if (value instanceof Boolean) return "bool";
		if (value instanceof Character) return "char";
		if (value instanceof String) return "string";
		return value.getClass().getSimpleName().toLowerCase();
	}

	private String formatValue(Object value) {
		if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else if (value instanceof Character) {
			return "'" + value + "'";
		}
		return String.valueOf(value);
	}
}