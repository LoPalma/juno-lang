package com.juno.types;

/**
 * Base interface for all types in the type system.
 */
public interface Type {
    /**
     * Get the name of this type.
     * @return type name
     */
    String getName();
    
    /**
     * Check if this type is compatible with another type.
     * @param other the other type
     * @return true if compatible
     */
    boolean isCompatibleWith(Type other);
    
    /**
     * Get the size of this type in bytes.
     * @return size in bytes
     */
    int getSize();
    
    /**
     * Get the JVM type descriptor for this type.
     * @return JVM type descriptor
     */
    String getJVMDescriptor();
}