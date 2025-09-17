package com.juno.types;

import java.util.List;
import java.util.Objects;

/**
 * Memory-safe variant type implementation for Juno.
 * 
 * Variants are implemented as JVM objects with:
 * - Type tag for runtime type checking
 * - Union of possible values (memory-safe via JVM)
 * - Automatic garbage collection
 * - No manual memory management required
 */
public class VariantType extends Type {
    private final List<Type> memberTypes;
    private final String name;
    
    public VariantType(String name, List<Type> memberTypes) {
        this.name = name;
        this.memberTypes = List.copyOf(memberTypes); // Immutable copy for safety
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public List<Type> getMemberTypes() {
        return memberTypes; // Already immutable
    }
    
    /**
     * Check if a type is compatible with this variant.
     * A type is compatible if it's one of the member types.
     */
    public boolean isCompatibleWith(Type other) {
        if (other == null) return false;
        
        // Exact match with variant
        if (other instanceof VariantType) {
            VariantType otherVariant = (VariantType) other;
            return this.equals(otherVariant);
        }
        
        // Check if type is one of our members
        return memberTypes.stream().anyMatch(memberType -> 
            memberType.equals(other) || memberType.getName().equals(other.getName())
        );
    }
    
    /**
     * Get the JVM descriptor for this variant type.
     * Variants are implemented as JVM objects.
     */
    public String getJVMDescriptor() {
        return "L" + getJVMClassName() + ";";
    }
    
    /**
     * Get the JVM class name for this variant.
     * Generated classes follow pattern: juno/variant/VariantName
     */
    public String getJVMClassName() {
        return "juno/variant/" + name.replace(" ", "");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VariantType)) return false;
        
        VariantType other = (VariantType) obj;
        return Objects.equals(name, other.name) && 
               Objects.equals(memberTypes, other.memberTypes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, memberTypes);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("variant ").append(name).append(" { ");
        for (int i = 0; i < memberTypes.size(); i++) {
            if (i > 0) sb.append(" | ");
            sb.append(memberTypes.get(i).getName());
        }
        sb.append(" }");
        return sb.toString();
    }
}