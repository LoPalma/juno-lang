package com.juno.types;

/**
 * Represents an optional type that can hold either a value of the specified type or null.
 * Example: optional int x;
 */
public record OptionalType(Type wrappedType) implements Type {

	@Override
	public String name() {
		return "optional " + wrappedType.name();
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
	public int size() {
		// Optional types need space for the value + null flag
		return wrappedType.size() + 1; // +1 for null flag
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

}