package com.juno.types;

/**
 * Represents a pointer type in the Juno language.
 * Since JVM doesn't have direct memory addresses, pointers are implemented
 * as references to JVM local variable slots or object field references.
 */
public record PointerType(Type pointedType) implements Type {

	@Override
	public String name() {
		return pointedType.name() + "*";
	}

	@Override
	public boolean isCompatibleWith(Type other) {
		if (!(other instanceof PointerType)) {
			return false;
		}
		PointerType otherPointer = (PointerType) other;
		// Pointers are compatible if they point to compatible types
		return pointedType.isCompatibleWith(otherPointer.pointedType) ||
				otherPointer.pointedType.isCompatibleWith(pointedType);
	}

	@Override
	public int size() {
		// Pointers are reference types, size is platform-dependent
		// On JVM, object references are typically 4-8 bytes
		return 8; // 64-bit reference
	}

	@Override
	public String getJVMDescriptor() {
		// Pointers are implemented as objects containing references
		return "Lcom/juno/runtime/Pointer;";
	}

	@Override
	public String toString() {
		return name();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PointerType)) return false;
		PointerType other = (PointerType) obj;
		return pointedType.equals(other.pointedType);
	}

}