package com.clikejvm.parser;

import com.clikejvm.ast.Program;
import com.clikejvm.lexer.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser for the C-like language.
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parseProgram() {
        // TODO: Implement parser
        // For now, return empty program
        return new Program(new ArrayList<>(), 1, 1);
    }

    // TODO: Add parsing methods for statements and expressions
}