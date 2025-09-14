# C-like JVM Language

An interpreted C-like strongly typed programming language that targets the JVM.

## Project Overview

This project implements a complete compiler/interpreter for a C-like programming language that generates JVM bytecode. The language features strong static typing, C-style syntax, and modern language constructs while leveraging the JVM's runtime capabilities.

## Features

### Language Features
- **Strong Static Typing**: Variables must be declared with explicit types
- **C-like Syntax**: Familiar syntax with braces, semicolons, and C-style operators
- **Primitive Types**: `int`, `float`, `char`, `string`, `bool`
- **Control Flow**: `if`/`else`, `while`, `for` loops
- **Functions**: Function declarations and calls with parameters and return values
- **Expressions**: Arithmetic, logical, and comparison operators
- **Comments**: Single-line (`//`) and multi-line (`/* */`) comments

### Compiler Features
- **Lexical Analysis**: Complete tokenizer for C-like syntax
- **Parsing**: Recursive descent parser generating AST
- **Type Checking**: Strong static type checking with error reporting
- **Code Generation**: JVM bytecode generation using ASM library
- **Error Handling**: Comprehensive error reporting with line/column information

## Project Structure

```
src/
├── main/java/com/clikejvm/
│   ├── Main.java                    # Main entry point
│   ├── lexer/                       # Lexical analysis
│   │   ├── Lexer.java              # Tokenizer implementation
│   │   ├── Token.java              # Token representation
│   │   └── TokenType.java          # Token type enumeration
│   ├── parser/                      # Syntax analysis
│   │   └── Parser.java             # Recursive descent parser
│   ├── ast/                         # Abstract Syntax Tree
│   │   ├── ASTNode.java            # Base AST node interface
│   │   ├── ASTVisitor.java         # Visitor pattern interface
│   │   ├── Program.java            # Root AST node
│   │   ├── Expression.java         # Expression interface
│   │   ├── Statement.java          # Statement interface
│   │   ├── BinaryExpression.java   # Binary expressions
│   │   └── ASTNodes.java           # Additional AST node stubs
│   ├── types/                       # Type system
│   │   ├── Type.java               # Base type interface
│   │   └── TypeChecker.java        # Type checking logic
│   └── codegen/                     # Code generation
│       └── CodeGenerator.java      # JVM bytecode generation
├── test/java/com/clikejvm/         # Unit tests
examples/                            # Example programs
docs/                               # Additional documentation
```

## Building the Project

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build Commands
```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Package as JAR
mvn package

# Clean build artifacts
mvn clean
```

### Running the Compiler
```bash
# Using Maven exec plugin
mvn exec:java -Dexec.args="examples/hello.cl"

# Using the packaged JAR
java -jar target/clike-jvm-lang-1.0.0-SNAPSHOT.jar examples/hello.cl
```

## Language Specification

### Grammar (EBNF-style)

```
program        = statement* EOF
statement      = varDecl | funDecl | ifStmt | whileStmt | returnStmt | block | exprStmt
varDecl        = type IDENTIFIER ( "=" expression )? ";"
funDecl        = type IDENTIFIER "(" parameters? ")" block
ifStmt         = "if" "(" expression ")" statement ( "else" statement )?
whileStmt      = "while" "(" expression ")" statement
returnStmt     = "return" expression? ";"
block          = "{" statement* "}"
exprStmt       = expression ";"

expression     = assignment
assignment     = logicOr ( "=" assignment )?
logicOr        = logicAnd ( "||" logicAnd )*
logicAnd       = equality ( "&&" equality )*
equality       = comparison ( ( "==" | "!=" ) comparison )*
comparison     = term ( ( "<" | "<=" | ">" | ">=" ) term )*
term           = factor ( ( "+" | "-" ) factor )*
factor         = unary ( ( "*" | "/" | "%" ) unary )*
unary          = ( "!" | "-" ) unary | call
call           = primary ( "(" arguments? ")" )*
primary        = literal | IDENTIFIER | "(" expression ")"

type           = "int" | "float" | "char" | "string" | "bool" | "void"
literal        = INTEGER | FLOAT | STRING | CHAR | "true" | "false"
```

### Example Programs

#### Hello World
```c
// hello.cl
int main() {
    string message = "Hello, World!";
    print(message);
    return 0;
}
```

#### Factorial Function
```c
// factorial.cl
int factorial(int n) {
    if (n <= 1) {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
}

int main() {
    int result = factorial(5);
    print(result);
    return 0;
}
```

#### Fibonacci Sequence
```c
// fibonacci.cl
int fibonacci(int n) {
    if (n <= 1) {
        return n;
    }
    return fibonacci(n - 1) + fibonacci(n - 2);
}

int main() {
    for (int i = 0; i < 10; i = i + 1) {
        int fib = fibonacci(i);
        print(fib);
    }
    return 0;
}
```

## Development Status

This is a foundational implementation with the following status:

### ✅ Completed
- [x] Project structure and build system
- [x] Complete lexical analyzer with all C-like tokens
- [x] AST node hierarchy with visitor pattern
- [x] Basic framework for parser, type checker, and code generator

### 🔄 In Progress / TODO
- [ ] Complete parser implementation for all language constructs
- [ ] Implement comprehensive type system with all primitive types
- [ ] Build type checker with proper error reporting
- [ ] Implement JVM bytecode generation using ASM
- [ ] Add support for arrays and structures
- [ ] Implement standard library functions
- [ ] Add comprehensive error handling and reporting
- [ ] Create extensive test suite
- [ ] Add IDE integration and debugging support

## Technical Architecture

### Compilation Pipeline
1. **Lexical Analysis**: Source code → Tokens
2. **Parsing**: Tokens → Abstract Syntax Tree (AST)
3. **Type Checking**: AST → Type-annotated AST
4. **Code Generation**: AST → JVM Bytecode (.class files)

### Design Patterns
- **Visitor Pattern**: Used for AST traversal (type checking, code generation)
- **Builder Pattern**: Used in parser for constructing AST nodes
- **Strategy Pattern**: Planned for different optimization strategies

### Dependencies
- **ASM Library**: For JVM bytecode generation and manipulation
- **JUnit 5**: For unit testing
- **AssertJ**: For fluent assertions in tests

## Contributing

This project is in early development. Key areas for contribution:

1. **Parser Implementation**: Complete the recursive descent parser
2. **Type System**: Implement comprehensive type checking
3. **Code Generation**: Complete JVM bytecode generation
4. **Standard Library**: Add built-in functions and types
5. **Testing**: Expand test coverage
6. **Documentation**: Improve language specification and examples

## License

This project is open source. See LICENSE file for details.

## References

- [ASM Documentation](https://asm.ow2.io/)
- [JVM Specification](https://docs.oracle.com/javase/specs/jvms/se17/html/)
- [Crafting Interpreters](https://craftinginterpreters.com/) - Excellent resource for language implementation