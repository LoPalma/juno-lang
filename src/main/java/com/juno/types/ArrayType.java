package com.juno.types;

import java.util.Objects;

/**
 * Represents array types in the Juno language.
 * Supports both dynamic arrays (int[]) and fixed-size arrays (int[10]).
 *
 */
public final class ArrayType implements Type {
	private final Type elementType;
	private final Integer size;

	/**
	 * @param size null for dynamic arrays, size for fixed arrays
	 */
	public ArrayType(Type elementType, Integer size) {
		this.elementType = elementType;
		this.size = size;
	}

	public ArrayType(Type elementType) {
		this(elementType, null); // Dynamic array
	}

	public ArrayType(Type elementType, int size) {
		this.elementType = elementType;
		this.size = size; // Fixed-size array
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
	public String name() {
		if (isDynamicArray()) {
			return elementType.name() + "[]";
		}
		else {
			return elementType.name() + "[" + size + "]";
		}
	}

	@Override
	public boolean isCompatibleWith(Type other) {
		if (!(other instanceof ArrayType otherArray)) {
			return false;
		}

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
	public int size() {
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
		return name();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ArrayType other)) return false;

		return elementType.equals(other.elementType) &&
				(Objects.equals(size, other.size));
	}

	public Type elementType() {
		return elementType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(elementType, size);
	}


}