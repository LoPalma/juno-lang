package com.juno.types;

/**
 * Special types for type inference and dynamic typing.
 */
public class SpecialTypes {
    
    /**
     * Represents the 'auto' type that infers its actual type from initialization.
     * Once inferred, the type is fixed.
     */
    public static class AutoType implements Type {
        private Type inferredType;
        
        public AutoType() {
            this.inferredType = null;
        }
        
        public void inferType(Type type) {
            if (inferredType == null) {
                this.inferredType = type;
            }
        }
        
        public Type getInferredType() {
            return inferredType;
        }
        
        public boolean isInferred() {
            return inferredType != null;
        }
        
        @Override
        public String name() {
            return inferredType != null ? inferredType.name() : "auto";
        }
        
        @Override
        public boolean isCompatibleWith(Type other) {
            if (inferredType == null) {
                return false; // Cannot use auto without inference
            }
            return inferredType.isCompatibleWith(other);
        }
        
        @Override
        public int size() {
            return inferredType != null ? inferredType.size() : 0;
        }
        
        @Override
        public String getJVMDescriptor() {
            return inferredType != null ? inferredType.getJVMDescriptor() : "V";
        }
        
        @Override
        public String toString() {
            return inferredType != null ? inferredType.toString() : "auto";
        }
    }
    
    /**
     * Represents the 'any' type that can hold values of any type.
     * This is essentially a union of all types except void.
     */
    public static class AnyType implements Type {
        public static final AnyType INSTANCE = new AnyType();
        
        private AnyType() {}
        
        @Override
        public String name() {
            return "any";
        }
        
        @Override
        public boolean isCompatibleWith(Type other) {
            // 'any' is compatible with all types except void
            return !other.name().equals("void");
        }
        
        public boolean canAccept(Type type) {
            // 'any' can accept all types except void
            return !type.name().equals("void");
        }
        
        @Override
        public int size() {
            // 'any' needs maximum space + type tag
            return 16; // Generous space for any value + type information
        }
        
        @Override
        public String getJVMDescriptor() {
            return "Ljava/lang/Object;"; // Generic object wrapper
        }
        
        @Override
        public String toString() {
            return "any";
        }
        
        @Override
        public boolean equals(Object obj) {
            return obj instanceof AnyType;
        }
        
        @Override
        public int hashCode() {
            return "any".hashCode();
        }
    }
}