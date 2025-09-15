package com.clikejvm.lexer;

/**
 * Enumeration of all token types in the C-like language.
 */
public enum TokenType {
    // Literals
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    CHAR_LITERAL,
    BOOLEAN_LITERAL,
    
    // Identifiers and keywords
    IDENTIFIER,
    
    // Keywords
    INT,
    FLOAT,
    CHAR,
    STRING,
    BOOL,
    VOID,
    TRUE,
    FALSE,
    IF,
    ELSE,
    WHILE,
    FOR,
    RETURN,
    BREAK,
    CONTINUE,
    STRUCT,
    IMPORT,
    MODULE,
    
    // Extended type keywords
    BYTE,
    UBYTE,
    SHORT,
    USHORT,
    UINT,
    ULONG,
    LONG,
    DOUBLE,
    
    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %
    ASSIGN,         // =
    EQUALS,         // ==
    NOT_EQUALS,     // !=
    LESS_THAN,      // <
    LESS_EQUAL,     // <=
    GREATER_THAN,   // >
    GREATER_EQUAL,  // >=
    LOGICAL_AND,    // &&
    LOGICAL_OR,     // ||
    LOGICAL_NOT,    // !
    BITWISE_AND,    // &
    BITWISE_OR,     // |
    BITWISE_XOR,    // ^
    BITWISE_NOT,    // ~
    LEFT_SHIFT,     // <<
    RIGHT_SHIFT,    // >>
    
    // Delimiters
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    LEFT_BRACKET,   // [
    RIGHT_BRACKET,  // ]
    SEMICOLON,      // ;
    COMMA,          // ,
    DOT,            // .
    ARROW,          // ->
    
    // Special
    EOF,
    NEWLINE,
    WHITESPACE
}