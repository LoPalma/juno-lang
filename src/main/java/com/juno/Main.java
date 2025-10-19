package com.juno;

import com.juno.error.CompilerError;
import com.juno.error.ErrorCollector;
import com.juno.error.ErrorReporter;
import com.juno.lexer.Lexer;
import com.juno.ast.Parser;
import com.juno.ast.Program;
import com.juno.ast.ASTDebugPrinter;
import com.juno.ast.TypeChecker;
import com.juno.ast.CodeGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main entry point for the C-like JVM language compiler/interpreter.
 */
public class Main {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Invalid amount of arguments");
			System.exit(1);
		}
		String sourceFile = args[0];

		boolean debugAST = false;
		boolean verbose = false;
		for (String arg : args) {
			if ("--ast-dump".equals(arg)) {
				debugAST = true;
			}
			if ("--verbose".equals(arg) || "-V".equals(arg)) {
				verbose = true;
			}
		}

		// Check for Jasmin generation flag from environment
		boolean generateJasmin = "true".equals(System.getenv("JUNO_GENERATE_JASMIN"));

		try {
			long startTime = System.nanoTime();

			ErrorCollector errorCollector = new ErrorCollector();
			compile(sourceFile, errorCollector, debugAST, generateJasmin, verbose);

			long endTime = System.nanoTime();
			double durationMs = (endTime - startTime) / 1_000_000.0;

			// Print all errors and warnings
			errorCollector.printAll();

			// Only print summary if compilation completed
			if (!errorCollector.hasErrors() && verbose) {
				System.out.printf("Compilation completed in %.2f ms%n", durationMs);
			}

			// Exit with error code if there were errors
			if (errorCollector.hasErrors()) {
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("Internal compiler error: " + e.getMessage());
			if (System.getenv("JUNO_DEBUG") != null) {
				e.printStackTrace();
			}
			System.exit(1);
		}
	}

	private static void compile(String sourceFile, ErrorCollector errorCollector, boolean debugAST, boolean generateJasmin, boolean verbose) throws IOException {
		Path sourcePath = Paths.get(sourceFile);
		if (!Files.exists(sourcePath)) {
			throw new IllegalArgumentException("Source file not found: " + sourceFile);
		}

		String source = Files.readString(sourcePath);
		String[] sourceLines = source.split("\n");

		if (verbose) System.out.println("Compiling: " + sourceFile + "\n");

		// Lexical Analysis
		Lexer lexer = new Lexer(source, sourceFile, errorCollector);
		var tokens = lexer.tokenize();
		if (verbose) System.out.println("-- Found " + tokens.size() + " tokens\n");

		// Parsing - skip if lexical errors prevent meaningful parsing
		if (errorCollector.hasErrors()) {
			if (verbose) System.out.println("Skipping parsing due to lexical errors\n");
			return;
		}

		Parser parser = new Parser(tokens, sourceFile, sourceLines, errorCollector);
		Program program = parser.parseProgram();
		if (verbose) System.out.println("-- Parsed " + program.getStatements().size() + " statements");

		// Print AST if debug flag is enabled
		if (debugAST) {
			System.out.println("\n=== AST DEBUG OUTPUT ===");
			ASTDebugPrinter debugPrinter = new ASTDebugPrinter();
			System.out.println(program.accept(debugPrinter));
			System.out.println("========================\n");
		}

		// Type Checking
		if (errorCollector.hasErrors()) {
			if (verbose) System.out.println("Skipping type checking due to previous errors");
			return;
		}
		TypeChecker typeChecker = new TypeChecker(errorCollector);
		typeChecker.check(program);

		// Code Generation
		if (errorCollector.hasErrors()) {
			if (verbose) System.out.println("Skipping code generation due to previous errors");
			return;
		}

		try {
			CodeGenerator codeGen = new CodeGenerator();
			String outputClass = sourceFile.replace(".juno", "").replace(".jl", "");
			codeGen.generate(program, outputClass, generateJasmin);
			if (verbose) {
				if (generateJasmin) {
					System.out.println("Outputting to Jasmin file: " + outputClass.concat(".j"));
				}
				System.out.println("Outputting to class file: " + outputClass.concat(".class"));
			}
		} catch (Exception e) {
			System.err.println("Code generation failed: " + e.getMessage());
			if (System.getenv("JUNO_DEBUG") != null) {
				e.printStackTrace();
			}
			System.exit(1);
		}
	}
}