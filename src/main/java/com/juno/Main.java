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
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: junoc [-j] <source-file>");
            System.exit(1);
        }

        String sourceFile = args[0];
        boolean debugAST = args.length > 1 && "--debug-ast".equals(args[1]);
        
        // Check for Jasmin generation flag from environment
        boolean generateJasmin = "true".equals(System.getenv("JUNO_GENERATE_JASMIN"));
        
        try {
            ErrorCollector errorCollector = new ErrorCollector();
            compile(sourceFile, errorCollector, debugAST, generateJasmin);
            
            // Print all errors and warnings
            errorCollector.printAll();
            
            // Only print summary if compilation completed
            if (!errorCollector.hasErrors()) {
                System.out.println("Compilation completed successfully.");
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

    private static void compile(String sourceFile, ErrorCollector errorCollector, boolean debugAST, boolean generateJasmin) throws IOException {
        Path sourcePath = Paths.get(sourceFile);
        if (!Files.exists(sourcePath)) {
            throw new IllegalArgumentException("Source file not found: " + sourceFile);
        }

        String source = Files.readString(sourcePath);
        String[] sourceLines = source.split("\n");
        
        System.out.println("Compiling: " + sourceFile);
        
        // Lexical Analysis
        System.out.println("Phase 1: Lexical Analysis");
        Lexer lexer = new Lexer(source, sourceFile, errorCollector);
        var tokens = lexer.tokenize();
        System.out.println("Found " + tokens.size() + " tokens");

        // Parsing - skip if lexical errors prevent meaningful parsing
        System.out.println("Phase 2: Parsing");
        if (errorCollector.hasErrors()) {
            System.out.println("Skipping parsing due to lexical errors");
            return;
        }
        
        Parser parser = new Parser(tokens, sourceFile, sourceLines, errorCollector);
        Program program = parser.parseProgram();
        System.out.println("Parsed program with " + program.getStatements().size() + " statements");
        
        // Print AST if debug flag is enabled
        if (debugAST) {
            System.out.println("\n=== AST DEBUG OUTPUT ===");
            ASTDebugPrinter debugPrinter = new ASTDebugPrinter();
            System.out.println(program.accept(debugPrinter));
            System.out.println("========================\n");
        }

        // Type Checking
        System.out.println("Phase 3: Type Checking");
        if (errorCollector.hasErrors()) {
            System.out.println("Skipping type checking due to previous errors");
            return;
        }
        TypeChecker typeChecker = new TypeChecker(errorCollector);
        typeChecker.check(program);

        // Code Generation
        System.out.println("Phase 4: Code Generation");
        if (errorCollector.hasErrors()) {
            System.out.println("Skipping code generation due to previous errors");
            return;
        }
        
        try {
            CodeGenerator codeGen = new CodeGenerator();
            String outputClass = sourceFile.replace(".juno", "").replace(".jl", "");
            codeGen.generate(program, outputClass, generateJasmin);
        } catch (Exception e) {
            System.err.println("Code generation failed: " + e.getMessage());
            if (System.getenv("JUNO_DEBUG") != null) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
