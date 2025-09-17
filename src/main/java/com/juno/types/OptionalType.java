package com.juno.types;

/**
 * Represents an optional type that can hold either a value of the specified type or null.
 * Example: optional int x;
 */
public class OptionalType implements Type {
    private final Type wrappedType;
    
    public OptionalType(Type wrappedType) {
        this.wrappedType = wrappedType;
    }
    
    public Type getWrappedType() {
        return wrappedType;
    }
    
    @Override
    public String getName() {
        return "optional " + wrappedType.getName();
    }
    
    @Override
    public boolean isCompatibleWith(Type other) {
        if (other instanceof OptionalType) {
            return wrappedType.isCompatibleWith(((OptionalType) other).wrappedType);
        }
        // Optional types can accept their wrapped type or null
        return wrappedType.isCompatibleWith(other);
    }
    
    @Override
    public int getSize() {
        // Optional types need space for the value + null flag
        return wrappedType.getSize() + 1; // +1 for null flag
    }
    
    @Override
    public String getJVMDescriptor() {
        // Optional types are implemented as boxed types in JVM
        return "Ljava/lang/Object;"; // Generic object for optional wrapper
    }
    
    @Override
    public String toString() {
        return "optional " + wrappedType.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OptionalType)) return false;
        OptionalType other = (OptionalType) obj;
        return wrappedType.equals(other.wrappedType);
    }
    
    @Override
    public int hashCode() {
        return wrappedType.hashCode() * 31 + 7; // 7 for optional marker
    }
}