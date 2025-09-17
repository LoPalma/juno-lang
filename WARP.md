mvn # WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a C-like programming language compiler that targets the JVM. The project implements a complete compilation pipeline from source code (.cl files) to JVM bytecode (.class files), featuring strong static typing, C-style syntax, and modern language constructs.

## Common Commands

### Building and Testing
```bash
# Compile the project
mvn compile

# Run all tests
mvn test

# Clean and rebuild everything
mvn clean compile

# Package as executable JAR
mvn package

# Clean all build artifacts
mvn clean
```

### Running the Compiler
```bash
# Clean execution without Maven noise (Recommended)
./run.sh examples/hello.cl
./run.sh examples/hello.cl --debug-ast

# Alternative Maven execution with reduced output
./mvn-run.sh examples/hello.cl

# Traditional Maven execution (verbose)
mvn exec:java -Dexec.args="examples/hello.cl"

# Direct JAR execution (after mvn package)
java -jar target/clike-jvm-lang-1.0.0-SNAPSHOT.jar examples/hello.cl
```

### Development Workflow
```bash
# Run a single test class (when tests exist)
mvn test -Dtest=LexerTest

# Compile and run in one command
mvn compile exec:java -Dexec.args="path/to/source.cl"

# Debug compilation phases
mvn exec:java -Dexec.args="examples/hello.cl" -Dexec.args="-verbose"
```

## Architecture Overview

### Compilation Pipeline
The compiler follows a traditional 4-phase pipeline:

1. **Lexical Analysis** (`com.clikejvm.lexer.Lexer`): Converts source code into tokens
2. **Parsing** (`com.clikejvm.parser.Parser`): Transforms tokens into Abstract Syntax Tree (AST)
3. **Type Checking** (`com.clikejvm.types.TypeChecker`): Validates types and semantics
4. **Code Generation** (`com.clikejvm.codegen.CodeGenerator`): Generates JVM bytecode using ASM

### Key Architectural Patterns

**Visitor Pattern**: Central to AST processing. All AST nodes implement `ASTNode.accept(ASTVisitor<T>)`, enabling clean separation of concerns between tree structure and operations (type checking, code generation).

**Token-Based Lexing**: The lexer (`Lexer.java`) uses a comprehensive token system with keyword recognition, operator precedence handling, and complete C-like language support including comments, string escaping, and numeric literals.

**Recursive Descent Parsing**: Parser uses recursive descent approach following the grammar specified in README.md, building AST nodes that maintain source location information for error reporting.

### Package Structure
- `com.clikejvm.lexer`: Tokenization (Lexer, Token, TokenType)
- `com.clikejvm.parser`: Syntax analysis (Parser)
- `com.clikejvm.ast`: AST node hierarchy with visitor pattern
- `com.clikejvm.types`: Type system and semantic analysis
- `com.clikejvm.codegen`: JVM bytecode generation using ASM library

### Current Implementation Status
- ✅ **Lexer**: Complete C-like token support with all type keywords and module system tokens
- ✅ **AST Structure**: Complete node hierarchy with visitor pattern
- ✅ **Parser**: Core parsing implemented with comprehensive statement and expression support
  - ✅ Import statements (full module and selective imports)
  - ✅ Function declarations with parameters and body blocks
  - ✅ Variable declarations with optional initialization
  - ✅ Control flow statements (if/else, while, return)
  - ✅ Expression parsing (literals, identifiers, calls, qualified identifiers)
  - ✅ Block statements with proper nesting and error recovery
  - ✅ All primitive types integration (including unsigned types)
  - 🚧 Advanced expressions (comparison operators, string concatenation)
- 🚧 **Type Checker**: Framework exists, comprehensive type system with unsigned types needed
- 🚧 **Code Generator**: Framework exists, ASM bytecode emission with unsigned arithmetic handling needed

## Language Specifications

### File Extension
Source files use `.cl` extension (C-Like language)

### Primitive Types

**Signed Integer Types:**
- `byte` (8-bit signed integer, -128 to 127)
- `short` (16-bit signed integer)
- `int` (32-bit signed integer)
- `long` (64-bit signed integer)

**Unsigned Integer Types:**
- `ubyte` (8-bit unsigned integer, 0 to 255)
- `ushort` (16-bit unsigned integer)
- `uint` (32-bit unsigned integer)
- `ulong` (64-bit unsigned integer)

**Floating Point Types:**
- `float` (32-bit IEEE 754 floating point)
- `double` (64-bit IEEE 754 floating point)

**Other Types:**
- `char` (UTF-16 character, compatible with JVM char)
- `string` (UTF-16 string with C++/D-like semantics, immutable by default)
- `bool` (boolean true/false)
- `void` (no return type)

- Strong static typing with explicit declarations required

### Key Language Features
- C-style syntax with braces and semicolons
- Function declarations with parameters and return types
- Control flow: `if`/`else`, `while`, `for` loops
- Expressions with operator precedence matching C (including `^^` concatenation)
- Single-line (`//`) and block (`/* */`) comments
- C++-like structs (public-only datatypes)
- Module system with explicit public declarations

### Module System
The language uses a module-based architecture for organizing code and libraries:

**Visibility Rules:**
- All module members are **private by default**
- Use `public` keyword to export functions, variables, and types
- This prevents accidental exports and promotes intentional API design

**Import Syntax:**
```c
import io;              // Import entire module
import io.{print};      // Import specific functions
import math.{sin, cos, PI}; // Import multiple items
```

**Standard Library Modules:**
- `io` - Input/output operations (print, println, readLine)
- `math` - Mathematical functions (sin, cos, sqrt, PI)
- `string` - String manipulation utilities
- `mem` - Memory management utilities
- `sys` - System calls and OS interaction
- `collections` - Arrays, maps, sets
- `time` - Date/time functionality

**Usage Example:**
```c
import io;
import math;

// Private helper function (not exported)
int calculate(int x) {
    return x * 2;
}

// Public function (can be imported by other modules)
public int main() {
    uint number = 42u;
    string message = "Result: " ^^ string(calculate(number));
    io.print(message);
    return 0;
}
```

### Struct System
**C++-like Structs (Datatypes, not Classes):**
```c
// Define a struct (all fields are public by default)
struct Point {
    float x;
    float y;
}

// Public struct (can be imported)
public struct Color {
    ubyte red;
    ubyte green; 
    ubyte blue;
}

// Usage
Point p = {3.14f, 2.71f};
Color red = {255u, 0u, 0u};
float distance = math.sqrt(p.x * p.x + p.y * p.y);
```

**Struct Characteristics:**
- All fields are **public** (no access control within structs)
- Simple data containers with constructors
- No methods, inheritance, or complex OOP features
- Designed for pure data representation

### Type System Implementation Notes

**JVM Mapping Strategy:**
- Unsigned types (`uint`, `ulong`, `ushort`, `ubyte`) require careful handling since JVM doesn't natively support unsigned arithmetic
- Use wider signed types for unsigned operations (e.g., `uint` → JVM `long` for arithmetic)
- Implement unsigned comparison and arithmetic operations as method calls or inline bytecode
- `byte` maps directly to JVM `byte`, `ubyte` uses JVM `short` with range validation

**String Implementation:**
- D-style string semantics: immutable by default, UTF-16 encoding
- Support for string slicing, concatenation, and comparison operators
- Integration with JVM `String` class while maintaining C-like syntax

**Module System Implementation:**
- Module names map to JVM package/class structure (e.g., `io` → `io.IoModule`)
- Qualified calls (`io.print`) compile to static method invocations
- Import statements create symbol table entries for name resolution
- Standard library modules are provided as compiled .class files in classpath

**Lexer Integration:**
- ✅ All type keywords added to `TokenType.java` and `Lexer.java` keyword map
- ✅ Integer literals (e.g., `123`) are type-agnostic and compatible with all numeric types
- ✅ Type compatibility resolved during type checking phase, not lexical analysis
- ✅ Added `IMPORT` and `MODULE` tokens for module syntax

## Development Focus Areas

When working on this codebase, the main areas needing development are:

1. **Advanced Expression Parsing**: Add support for comparison operators (<=, >=, ==, !=), string concatenation (^^), and complete operator precedence
2. **Type Checker Implementation**: Implement comprehensive type checking with unsigned arithmetic validation and type compatibility checks
3. **Module System**: Implement module resolution, symbol tables, and standard library integration
4. **Code Generation**: Complete JVM bytecode generation with module call support and unsigned arithmetic handling
5. **Standard Library Modules**: Implement core modules (`io`, `math`, `string`) as JVM classes
6. **For Loop Support**: Add parsing and semantics for C-style for loops
7. **Struct System**: Implement struct declarations and usage

## Testing Strategy

The project uses JUnit 5 with AssertJ for testing. When adding tests:
- Place test files in `src/test/java/com/clikejvm/`
- Test each compilation phase independently
- Use example `.cl` files for integration testing
- Focus on error handling and edge cases

## Dependencies

- **ASM Library (9.5)**: JVM bytecode manipulation and generation
- **JUnit 5**: Unit testing framework
- **AssertJ**: Fluent assertions for tests
- **Java 17**: Minimum runtime requirement

## Notes

- The main entry point (`Main.java`) demonstrates the complete compilation pipeline
- AST nodes maintain source location (line/column) for error reporting
- The lexer handles complex tokenization including escape sequences and nested comments
- Example programs in `examples/` directory demonstrate language capabilities
- Project uses Maven Shade plugin to create executable JAR with dependencies