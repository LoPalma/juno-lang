package com.juno.ast;

import com.juno.types.Type;
import java.util.List;

/**
 * Represents a struct declaration statement.
 */
public class StructDeclaration implements Statement {
    private final String name;
    private final List<Field> fields;
    private final boolean isPublic;
    private final int line;
    private final int column;

    public StructDeclaration(String name, List<Field> fields, boolean isPublic, int line, int column) {
        this.name = name;
        this.fields = fields;
        this.isPublic = isPublic;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitStructDeclaration(this);
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    /**
     * Represents a field in a struct.
     */
    public static class Field {
        public final Type type;
        public final String name;

        public Field(Type type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}