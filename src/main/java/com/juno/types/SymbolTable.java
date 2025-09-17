package com.juno.types;

import java.util.*;

/**
 * Symbol table for managing variables, functions and their types in nested scopes.
 * Supports type inference and declaration tracking.
 */
public class SymbolTable {
    
    /**
     * Represents a symbol (variable or function) in the symbol table.
     */
    public static class Symbol {
        private final String name;
        private Type type;
        private final boolean isMutable;
        private final boolean isFunction;
        private final int declarationLine;
        private final int declarationColumn;
        private boolean isInitialized;
        
        public Symbol(String name, Type type, boolean isMutable, boolean isFunction, 
                     int declarationLine, int declarationColumn) {
            this.name = name;
            this.type = type;
            this.isMutable = isMutable;
            this.isFunction = isFunction;
            this.declarationLine = declarationLine;
            this.declarationColumn = declarationColumn;
            this.isInitialized = false;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public Type getType() { return type; }
        public void setType(Type type) { this.type = type; }
        public boolean isMutable() { return isMutable; }
        public boolean isFunction() { return isFunction; }
        public int getDeclarationLine() { return declarationLine; }
        public int getDeclarationColumn() { return declarationColumn; }
        public boolean isInitialized() { return isInitialized; }
        public void setInitialized(boolean initialized) { this.isInitialized = initialized; }
        
        @Override
        public String toString() {
            return String.format("%s: %s %s", name, type, isFunction ? "(function)" : "(variable)");
        }
    }
    
    /**
     * Represents a scope in the symbol table.
     */
    private static class Scope {
        private final Map<String, Symbol> symbols = new HashMap<>();
        private final Scope parent;
        private final String scopeName;
        
        public Scope(String scopeName, Scope parent) {
            this.scopeName = scopeName;
            this.parent = parent;
        }
        
        public void declare(String name, Symbol symbol) {
            symbols.put(name, symbol);
        }
        
        public Symbol lookup(String name) {
            Symbol symbol = symbols.get(name);
            if (symbol != null) {
                return symbol;
            }
            return parent != null ? parent.lookup(name) : null;
        }
        
        public Symbol lookupLocal(String name) {
            return symbols.get(name);
        }
        
        public boolean isDeclaredLocally(String name) {
            return symbols.containsKey(name);
        }
        
        public Collection<Symbol> getAllSymbols() {
            return symbols.values();
        }
        
        public String getScopeName() { return scopeName; }
        public Scope getParent() { return parent; }
        
        @Override
        public String toString() {
            return String.format("Scope[%s]: %d symbols", scopeName, symbols.size());
        }
    }
    
    // Current scope management
    private Scope currentScope;
    private int scopeDepth = 0;
    
    public SymbolTable() {
        // Create global scope
        currentScope = new Scope("global", null);
        initializeBuiltins();
    }
    
    /**
     * Initialize built-in functions and constants.
     */
    private void initializeBuiltins() {
        // Add built-in functions like print, println, etc.
        declareFunction("print", PrimitiveType.VOID, true, 0, 0);
        declareFunction("println", PrimitiveType.VOID, true, 0, 0);
        
        // Add built-in constants
        declareVariable("nullptr", new OptionalType(SpecialTypes.AnyType.INSTANCE), false, true, 0, 0);
    }
    
    /**
     * Enter a new scope.
     */
    public void enterScope(String scopeName) {
        currentScope = new Scope(scopeName, currentScope);
        scopeDepth++;
    }
    
    /**
     * Exit the current scope.
     */
    public void exitScope() {
        if (currentScope.getParent() == null) {
            throw new IllegalStateException("Cannot exit global scope");
        }
        currentScope = currentScope.getParent();
        scopeDepth--;
    }
    
    /**
     * Declare a variable in the current scope.
     */
    public void declareVariable(String name, Type type, boolean isMutable, boolean isInitialized, 
                               int line, int column) {
        if (currentScope.isDeclaredLocally(name)) {
            throw new IllegalArgumentException(
                String.format("Variable '%s' is already declared in current scope at line %d", 
                             name, currentScope.lookupLocal(name).getDeclarationLine())
            );
        }
        
        Symbol symbol = new Symbol(name, type, isMutable, false, line, column);
        symbol.setInitialized(isInitialized);
        currentScope.declare(name, symbol);
    }
    
    /**
     * Declare a function in the current scope.
     */
    public void declareFunction(String name, Type returnType, boolean isInitialized, int line, int column) {
        if (currentScope.isDeclaredLocally(name)) {
            throw new IllegalArgumentException(
                String.format("Function '%s' is already declared in current scope at line %d", 
                             name, currentScope.lookupLocal(name).getDeclarationLine())
            );
        }
        
        Symbol symbol = new Symbol(name, returnType, false, true, line, column);
        symbol.setInitialized(isInitialized);
        currentScope.declare(name, symbol);
    }
    
    /**
     * Look up a symbol (variable or function) by name.
     */
    public Symbol lookup(String name) {
        return currentScope.lookup(name);
    }
    
    /**
     * Look up a symbol only in the current scope.
     */
    public Symbol lookupLocal(String name) {
        return currentScope.lookupLocal(name);
    }
    
    /**
     * Check if a name is declared in the current scope.
     */
    public boolean isDeclaredLocally(String name) {
        return currentScope.isDeclaredLocally(name);
    }
    
    /**
     * Check if a name is declared in any accessible scope.
     */
    public boolean isDeclared(String name) {
        return lookup(name) != null;
    }
    
    /**
     * Update the type of a symbol (useful for auto type inference).
     */
    public void updateSymbolType(String name, Type newType) {
        Symbol symbol = lookup(name);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol '" + name + "' not found");
        }
        symbol.setType(newType);
    }
    
    /**
     * Mark a symbol as initialized.
     */
    public void markInitialized(String name) {
        Symbol symbol = lookup(name);
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol '" + name + "' not found");
        }
        symbol.setInitialized(true);
    }
    
    /**
     * Get current scope depth.
     */
    public int getScopeDepth() {
        return scopeDepth;
    }
    
    /**
     * Get current scope name.
     */
    public String getCurrentScopeName() {
        return currentScope.getScopeName();
    }
    
    /**
     * Get all symbols in current scope.
     */
    public Collection<Symbol> getCurrentScopeSymbols() {
        return currentScope.getAllSymbols();
    }
    
    /**
     * Debug method to print symbol table state.
     */
    public void debugPrint() {
        System.out.println("=== Symbol Table Debug ===");
        Scope scope = currentScope;
        while (scope != null) {
            System.out.println(scope);
            for (Symbol symbol : scope.getAllSymbols()) {
                System.out.println("  " + symbol);
            }
            scope = scope.getParent();
        }
        System.out.println("========================");
    }
}