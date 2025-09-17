package com.juno.types;

/**
 * Represents array types in the Juno language.
 * Supports both dynamic arrays (int[]) and fixed-size arrays (int[10]).
 */
public class ArrayType implements Type {
    private final Type elementType;
    private final Integer size; // null for dynamic arrays, size for fixed arrays
    
    public ArrayType(Type elementType) {
        this.elementType = elementType;
        this.size = null; // Dynamic array
    }
    
    public ArrayType(Type elementType, int size) {
        this.elementType = elementType;
        this.size = size; // Fixed-size array
    }
    
    public Type getElementType() {
        return elementType;
    }
    
    public Integer getArraySize() {
        return size;
    }
    
    public boolean isDynamicArray() {
        return size == null;
    }
    
    public boolean isFixedArray() {
        return size != null;
    }
    
    @Override
    public String getName() {
        if (isDynamicArray()) {
            return elementType.getName() + "[]";
        } else {
            return elementType.getName() + "[" + size + "]";
        }
    }
    
    @Override
    public boolean isCompatibleWith(Type other) {
        if (!(other instanceof ArrayType)) {
            return false;
        }
        
        ArrayType otherArray = (ArrayType) other;
        
        // Element types must be compatible
        if (!elementType.isCompatibleWith(otherArray.elementType)) {
            return false;
        }
        
        // For assignment compatibility:
        // - Dynamic arrays are compatible with any array of same element type
        // - Fixed arrays are only compatible with same size or dynamic arrays
        if (isDynamicArray() || otherArray.isDynamicArray()) {
            return true; // Dynamic arrays are flexible
        }
        
        // Both are fixed-size, must have same size
        return size.equals(otherArray.size);
    }
    
    @Override
    public int getSize() {
        // Arrays are references on the JVM (4 bytes on 32-bit, 8 bytes on 64-bit)
        // We'll use 8 bytes for reference size to be safe
        return 8;
    }
    
    @Override
    public String getJVMDescriptor() {
        // JVM array descriptor: [ElementDescriptor
        // e.g., int[] = [I, String[] = [Ljava/lang/String;
        return "[" + elementType.getJVMDescriptor();
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ArrayType)) return false;
        
        ArrayType other = (ArrayType) obj;
        return elementType.equals(other.elementType) && 
               (size == null ? other.size == null : size.equals(other.size));
    }
    
    @Override
    public int hashCode() {
        return elementType.hashCode() * 31 + (size != null ? size.hashCode() : 0);
    }
}