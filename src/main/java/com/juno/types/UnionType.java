package com.juno.types;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Represents a union type that can hold values of multiple types.
 * Example: string|int x;
 */
public class UnionType implements Type {
    private final Set<Type> types;
    
    public UnionType(List<Type> types) {
        this.types = new HashSet<>(types);
    }
    
    public Set<Type> getTypes() {
        return new HashSet<>(types);
    }
    
    @Override
    public String name() {
        return types.stream()
                   .map(Type::name)
                   .collect(Collectors.joining("|"));
    }
    
    @Override
    public boolean isCompatibleWith(Type other) {
        if (other instanceof UnionType) {
            UnionType otherUnion = (UnionType) other;
            // This union is compatible with another if all our types are compatible with any of theirs
            return types.stream().allMatch(myType -> 
                otherUnion.types.stream().anyMatch(theirType -> myType.isCompatibleWith(theirType))
            );
        }
        
        // A union type is compatible with another type if any of its constituent types is compatible
        return types.stream().anyMatch(type -> type.isCompatibleWith(other));
    }
    
    public boolean canAccept(Type type) {
        // A union can accept a type if any of its constituent types is compatible with it
        return types.stream().anyMatch(unionType -> unionType.isCompatibleWith(type));
    }
    
    @Override
    public int size() {
        // Union types need space for the largest possible value + type tag
        int maxSize = types.stream().mapToInt(Type::size).max().orElse(4);
        return maxSize + 4; // +4 bytes for type tag
    }
    
    @Override
    public String getJVMDescriptor() {
        // Union types are implemented as tagged unions in JVM
        return "Ljava/lang/Object;"; // Generic object wrapper for unions
    }
    
    @Override
    public String toString() {
        return name();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UnionType)) return false;
        UnionType other = (UnionType) obj;
        return types.equals(other.types);
    }
    
    @Override
    public int hashCode() {
        return types.hashCode();
    }
}