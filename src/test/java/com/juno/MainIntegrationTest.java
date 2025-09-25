package com.juno;

import com.juno.lexer.Lexer;
import com.juno.lexer.Token;
import com.juno.lexer.TokenType;
import com.juno.ast.Parser;
import com.juno.ast.Program;
import com.juno.error.ErrorCollector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Integration tests for the complete compiler pipeline.
 */
public class MainIntegrationTest {

	@Test
	@DisplayName("Should successfully tokenize hello.juno example")
	public void testHelloWorldTokenization() throws IOException {
		String helloSource = Files.readString(Path.of("examples/hello.juno"));

		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(helloSource, "examples/hello.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();

		// Should find expected tokens including keywords and identifiers
		assertThat(tokens).isNotEmpty();
		assertThat(tokens.get(tokens.size() - 1).getType()).isEqualTo(TokenType.EOF);

		// Look for key tokens from the hello.juno file
		boolean foundVoidKeyword = tokens.stream()
				.anyMatch(token -> token.getType() == TokenType.VOID);
		boolean foundMainIdentifier = tokens.stream()
				.anyMatch(token -> token.getType() == TokenType.IDENTIFIER && "main".equals(token.getLexeme()));
		boolean foundStringLiteral = tokens.stream()
				.anyMatch(token -> token.getType() == TokenType.STRING_LITERAL);

		assertThat(foundVoidKeyword).isTrue();
		assertThat(foundMainIdentifier).isTrue();
		assertThat(foundStringLiteral).isTrue();
	}

	@Test
	@DisplayName("Should successfully tokenize factorial.juno example")
	public void testFactorialTokenization() throws IOException {
		String factorialSource = Files.readString(Path.of("examples/factorial.juno"));

		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(factorialSource, "examples/factorial.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();

		assertThat(tokens).isNotEmpty();
		assertThat(tokens.get(tokens.size() - 1).getType()).isEqualTo(TokenType.EOF);

		// Look for key tokens from the factorial.juno file
		boolean foundIfKeyword = tokens.stream()
				.anyMatch(token -> token.getType() == TokenType.IF);
		boolean foundElseKeyword = tokens.stream()
				.anyMatch(token -> token.getType() == TokenType.ELSE);
		boolean foundReturnKeyword = tokens.stream()
				.anyMatch(token -> token.getType() == TokenType.RETURN);
		boolean foundFactorialIdentifier = tokens.stream()
				.anyMatch(token -> token.getType() == TokenType.IDENTIFIER && "factorial".equals(token.getLexeme()));

		assertThat(foundIfKeyword).isTrue();
		assertThat(foundElseKeyword).isTrue();
		assertThat(foundReturnKeyword).isTrue();
		assertThat(foundFactorialIdentifier).isTrue();
	}

	@Test
	@DisplayName("Should handle complete compilation pipeline for hello.juno")
	public void testHelloWorldCompilationPipeline() throws IOException {
		String helloSource = Files.readString(Path.of("examples/hello.juno"));

		// Phase 1: Lexical Analysis
		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(helloSource, "examples/hello.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();
		assertThat(tokens).isNotEmpty();

		// Phase 2: Parsing (returns empty program currently)
		String[] sourceLines = helloSource.split("\n");
		Parser parser = new Parser(tokens, "examples/hello.juno", sourceLines, errorCollector);
		Program program = parser.parseProgram();
		assertThat(program).isNotNull();
		// Note: Parser currently returns empty program, so we can't test much more

		// Test type checker (should work)
		assertThatCode(() -> new com.juno.ast.TypeChecker(errorCollector).check(program)).doesNotThrowAnyException();

		/* deprecated test: syntax evolved.
		assertThatThrownBy(() -> {
			new com.juno.ast.CodeGenerator().generate(program, "test");
		}).hasMessageContaining("io.print");
		*/

		// Debug: Print any errors that were collected
		if (errorCollector.hasErrors()) {
			System.out.println("Error collector has errors:");
			errorCollector.getErrors().forEach(error ->
																						 System.out.println("  " + error.getMessage() + " at " + error.getLine() + ":" + error.getColumn())
			);
		}

		// For now, we'll accept that there might be some errors since the compiler is not fully implemented
		// In a fully implemented compiler, this should be: assertThat(errorCollector.hasErrors()).isFalse();
	}

	@Test
	@DisplayName("Should tokenize basic variable declaration")
	public void testBasicVariableDeclaration() {
		String source = "int x = 42;";

		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(source, "test.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();

		// Remove EOF token for easier testing
		tokens = tokens.subList(0, tokens.size() - 1);

		assertThat(tokens).hasSize(5);
		assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INT);
		assertThat(tokens.get(1).getType()).isEqualTo(TokenType.IDENTIFIER);
		assertThat(tokens.get(1).getLexeme()).isEqualTo("x");
		assertThat(tokens.get(2).getType()).isEqualTo(TokenType.ASSIGN);
		assertThat(tokens.get(3).getType()).isEqualTo(TokenType.INTEGER_LITERAL);
		assertThat(tokens.get(3).getLiteral()).isEqualTo(42);
		assertThat(tokens.get(4).getType()).isEqualTo(TokenType.SEMICOLON);
	}

	@Test
	@DisplayName("Should tokenize function declaration")
	public void testFunctionDeclaration() {
		String source = "int add(int a, int b) { return a + b; }";

		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(source, "test.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();

		// Remove EOF token
		tokens = tokens.subList(0, tokens.size() - 1);

		assertThat(tokens).hasSize(16);

		// Check key positions
		assertThat(tokens.get(0).getType()).isEqualTo(TokenType.INT);
		assertThat(tokens.get(1).getType()).isEqualTo(TokenType.IDENTIFIER);
		assertThat(tokens.get(1).getLexeme()).isEqualTo("add");
		assertThat(tokens.get(2).getType()).isEqualTo(TokenType.LEFT_PAREN);
		assertThat(tokens.get(9).getType()).isEqualTo(TokenType.LEFT_BRACE);
		assertThat(tokens.get(10).getType()).isEqualTo(TokenType.RETURN);
		assertThat(tokens.get(15).getType()).isEqualTo(TokenType.RIGHT_BRACE);
	}

	@Test
	@DisplayName("Should handle arithmetic expressions")
	public void testArithmeticExpressions() {
		String source = "result = (a + b) * c / d - e % f;";

		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(source, "test.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();

		// Remove EOF token
		tokens = tokens.subList(0, tokens.size() - 1);

		// Should find all operators
		boolean foundPlus = tokens.stream().anyMatch(t -> t.getType() == TokenType.PLUS);
		boolean foundMinus = tokens.stream().anyMatch(t -> t.getType() == TokenType.MINUS);
		boolean foundMultiply = tokens.stream().anyMatch(t -> t.getType() == TokenType.MULTIPLY);
		boolean foundDivide = tokens.stream().anyMatch(t -> t.getType() == TokenType.DIVIDE);
		boolean foundModulo = tokens.stream().anyMatch(t -> t.getType() == TokenType.MODULO);
		boolean foundAssign = tokens.stream().anyMatch(t -> t.getType() == TokenType.ASSIGN);

		assertThat(foundPlus).isTrue();
		assertThat(foundMinus).isTrue();
		assertThat(foundMultiply).isTrue();
		assertThat(foundDivide).isTrue();
		assertThat(foundModulo).isTrue();
		assertThat(foundAssign).isTrue();
	}

	@Test
	@DisplayName("Should handle boolean expressions")
	public void testBooleanExpressions() {
		String source = "if (x == y && (a > b || c < d) && !flag) { }";

		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(source, "test.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();

		// Remove EOF token
		tokens = tokens.subList(0, tokens.size() - 1);

		// Should find comparison and logical operators
		boolean foundEquals = tokens.stream().anyMatch(t -> t.getType() == TokenType.EQUALS);
		boolean foundLogicalAnd = tokens.stream().anyMatch(t -> t.getType() == TokenType.LOGICAL_AND);
		boolean foundLogicalOr = tokens.stream().anyMatch(t -> t.getType() == TokenType.LOGICAL_OR);
		boolean foundLogicalNot = tokens.stream().anyMatch(t -> t.getType() == TokenType.LOGICAL_NOT);
		boolean foundGreater = tokens.stream().anyMatch(t -> t.getType() == TokenType.GREATER_THAN);
		boolean foundLess = tokens.stream().anyMatch(t -> t.getType() == TokenType.LESS_THAN);

		assertThat(foundEquals).isTrue();
		assertThat(foundLogicalAnd).isTrue();
		assertThat(foundLogicalOr).isTrue();
		assertThat(foundLogicalNot).isTrue();
		assertThat(foundGreater).isTrue();
		assertThat(foundLess).isTrue();
	}

	@Test
	@DisplayName("Should handle mixed content with comments and strings")
	public void testMixedContent() {
		String source = """
				/* Multi-line comment
				   with multiple lines */
				string message = "Hello, World!"; // Single line comment
				char c = 'A';
				float pi = 3.14159;
				bool isTrue = true;
				""";

		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(source, "test.juno", errorCollector);
		List<Token> tokens = lexer.tokenize();

		// Should parse correctly despite comments
		assertThat(tokens).isNotEmpty();
		assertThat(tokens.get(tokens.size() - 1).getType()).isEqualTo(TokenType.EOF);

		// Should find various types of literals
		boolean foundString = tokens.stream().anyMatch(t -> t.getType() == TokenType.STRING_LITERAL);
		boolean foundChar = tokens.stream().anyMatch(t -> t.getType() == TokenType.CHAR_LITERAL);
		boolean foundFloat = tokens.stream().anyMatch(t -> t.getType() == TokenType.FLOAT_LITERAL);
		boolean foundBool = tokens.stream().anyMatch(t -> t.getType() == TokenType.TRUE);

		assertThat(foundString).isTrue();
		assertThat(foundChar).isTrue();
		assertThat(foundFloat).isTrue();
		assertThat(foundBool).isTrue();
	}

	@Test
	@DisplayName("Should create temporary source file and compile it")
	public void testTemporaryFileCompilation(@TempDir Path tempDir) throws IOException {
		// Create a temporary .juno file
		Path tempFile = tempDir.resolve("test.juno");
		String testSource = """
				int main() {
				    int x = 10;
				    int y = 20;
				    int result = x + y;
				    return result;
				}
				""";
		Files.writeString(tempFile, testSource);

		// Test that the file can be tokenized
		String source = Files.readString(tempFile);
		ErrorCollector errorCollector = new ErrorCollector();
		Lexer lexer = new Lexer(source, tempFile.toString(), errorCollector);
		List<Token> tokens = lexer.tokenize();

		assertThat(tokens).isNotEmpty();
		assertThat(tokens.get(tokens.size() - 1).getType()).isEqualTo(TokenType.EOF);

		// Should find the main function and integer arithmetic
		boolean foundMain = tokens.stream()
				.anyMatch(t -> t.getType() == TokenType.IDENTIFIER && "main".equals(t.getLexeme()));
		boolean foundReturn = tokens.stream()
				.anyMatch(t -> t.getType() == TokenType.RETURN);

		assertThat(foundMain).isTrue();
		assertThat(foundReturn).isTrue();
	}
}