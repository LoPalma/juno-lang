package com.juno.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * Unit tests for AST classes and visitor pattern.
 */
public class ASTTest {

	@Test
	@DisplayName("Should create Program with statements")
	public void testProgramCreation() {
		Program program = new Program(Collections.emptyList(), 1, 1);

		assertThat(program).isNotNull();
		assertThat(program.getStatements()).isEmpty();
		assertThat(program.line()).isEqualTo(1);
		assertThat(program.column()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should create BinaryExpression correctly")
	public void testBinaryExpressionCreation() {
		// Create mock expressions for testing
		Expression left = new MockExpression(1, 1);
		Expression right = new MockExpression(1, 5);

		BinaryExpression binaryExpr = new BinaryExpression(left, "+", right, 1, 3);

		assertThat(binaryExpr).isNotNull();
		assertThat(binaryExpr.getLeft()).isEqualTo(left);
		assertThat(binaryExpr.getOperator()).isEqualTo("+");
		assertThat(binaryExpr.getRight()).isEqualTo(right);
		assertThat(binaryExpr.line()).isEqualTo(1);
		assertThat(binaryExpr.column()).isEqualTo(3);
	}

	@Test
	@DisplayName("Should support visitor pattern")
	public void testVisitorPattern() {
		// Create a test visitor that counts nodes
		TestNodeCountVisitor visitor = new TestNodeCountVisitor();

		Program program = new Program(Collections.emptyList(), 1, 1);
		program.accept(visitor);

		assertThat(visitor.getProgramCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should handle visitor pattern with binary expressions")
	public void testVisitorPatternWithBinaryExpression() {
		TestNodeCountVisitor visitor = new TestNodeCountVisitor();

		Expression left = new MockExpression(1, 1);
		Expression right = new MockExpression(1, 5);
		BinaryExpression binaryExpr = new BinaryExpression(left, "+", right, 1, 3);

		binaryExpr.accept(visitor);

		assertThat(visitor.getBinaryExpressionCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("Should maintain source location information")
	public void testSourceLocationTracking() {
		Program program = new Program(Collections.emptyList(), 10, 5);
		Expression mockExpr = new MockExpression(20, 15);

		assertThat(program.line()).isEqualTo(10);
		assertThat(program.column()).isEqualTo(5);
		assertThat(mockExpr.line()).isEqualTo(20);
		assertThat(mockExpr.column()).isEqualTo(15);
	}

	// Test implementations for testing purposes

	/**
	 * Mock expression for testing.
	 */
	private static class MockExpression implements Expression {
		private final int line;
		private final int column;
		private com.juno.types.Type type;

		public MockExpression(int line, int column) {
			this.line = line;
			this.column = column;
		}

		@Override
		public <T> T accept(ASTVisitor<T> visitor) {
			// For testing, we'll create a mock visitor method
			if (visitor instanceof TestNodeCountVisitor) {
				((TestNodeCountVisitor) visitor).visitMockExpression();
			}
			return null;
		}

		@Override
		public int line() {
			return line;
		}

		@Override
		public int column() {
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

	/**
	 * Test visitor that counts different types of AST nodes.
	 */
	private static class TestNodeCountVisitor implements ASTVisitor<Void> {
		private int programCount = 0;
		private int binaryExpressionCount = 0;
		private int mockExpressionCount = 0;

		@Override
		public Void visitProgram(Program program) {
			programCount++;
			return null;
		}

		@Override
		public Void visitBinaryExpression(BinaryExpression expr) {
			binaryExpressionCount++;
			return null;
		}

		public void visitMockExpression() {
			mockExpressionCount++;
		}

		// For now, return null for all other visitor methods since they're not implemented
		@Override
		public Void visitUnaryExpression(UnaryExpression expr) {
			return null;
		}

		@Override
		public Void visitLiteralExpression(LiteralExpression expr) {
			return null;
		}

		@Override
		public Void visitIdentifierExpression(IdentifierExpression expr) {
			return null;
		}

		@Override
		public Void visitAssignmentExpression(AssignmentExpression expr) {
			return null;
		}

		@Override
		public Void visitCallExpression(CallExpression expr) {
			return null;
		}

		@Override
		public Void visitExpressionStatement(ExpressionStatement stmt) {
			return null;
		}

		@Override
		public Void visitVariableDeclaration(VariableDeclaration stmt) {
			return null;
		}

		@Override
		public Void visitFunctionDeclaration(FunctionDeclaration stmt) {
			return null;
		}

		@Override
		public Void visitIfStatement(IfStatement stmt) {
			return null;
		}

		@Override
		public Void visitWhileStatement(WhileStatement stmt) {
			return null;
		}

		@Override
		public Void visitReturnStatement(ReturnStatement stmt) {
			return null;
		}

		@Override
		public Void visitBlockStatement(BlockStatement stmt) {
			return null;
		}

		@Override
		public Void visitImportStatement(ImportStatement stmt) {
			return null;
		}

		@Override
		public Void visitModuleDeclaration(ModuleDeclaration stmt) {
			return null;
		}

		@Override
		public Void visitQualifiedIdentifier(QualifiedIdentifier expr) {
			return null;
		}

		@Override
		public Void visitCastExpression(CastExpression expr) {
			return null;
		}

		@Override
		public Void visitArrayLiteralExpression(ArrayLiteralExpression expr) {
			return null;
		}

		@Override
		public Void visitArrayIndexExpression(ArrayIndexExpression expr) {
			return null;
		}

		@Override
		public Void visitBreakStatement(BreakStatement stmt) {
			return null;
		}

		@Override
		public Void visitContinueStatement(ContinueStatement stmt) {
			return null;
		}

		@Override
		public Void visitForInStatement(ForInStatement stmt) {
			return null;
		}

		@Override
		public Void visitTypeAlias(TypeAlias stmt) {
			return null;
		}

		@Override
		public Void visitAddressOfExpression(AddressOfExpression expr) {
			return null;
		}

		@Override
		public Void visitDereferenceExpression(DereferenceExpression expr) {
			return null;
		}

		@Override
		public Void visitStructDeclaration(StructDeclaration stmt) {
			return null;
		}

		// Getters for test assertions
		public int getProgramCount() {
			return programCount;
		}

		public int getBinaryExpressionCount() {
			return binaryExpressionCount;
		}

		public int getMockExpressionCount() {
			return mockExpressionCount;
		}
	}

	@Test
	@DisplayName("Should handle null statements in Program")
	public void testProgramWithNullStatements() {
		Program program = new Program(null, 1, 1);

		assertThat(program).isNotNull();
		// The Program implementation should handle null gracefully
	}

	@Test
	@DisplayName("Should create Program with multiple statements")
	public void testProgramWithMultipleStatements() {
		MockStatement stmt1 = new MockStatement(1, 1);
		MockStatement stmt2 = new MockStatement(2, 1);

		Program program = new Program(Arrays.asList(stmt1, stmt2), 1, 1);

		assertThat(program).isNotNull();
		assertThat(program.getStatements()).hasSize(2);
		assertThat(program.getStatements()).contains(stmt1, stmt2);
	}

	/**
	 * Mock statement for testing.
	 */
	private static class MockStatement implements Statement {
		private final int line;
		private final int column;

		public MockStatement(int line, int column) {
			this.line = line;
			this.column = column;
		}

		@Override
		public <T> T accept(ASTVisitor<T> visitor) {
			return null;
		}

		@Override
		public int line() {
			return line;
		}

		@Override
		public int column() {
			return column;
		}
	}
}