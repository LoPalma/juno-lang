package com.juno.types;

/**
 * Implementation of primitive types for the C-like language.
 * Handles both signed and unsigned integer types, floating point, char, string, bool, and void.
 */
public record PrimitiveType(String name, int size, String jvmDescriptor) implements Type {
	public static final PrimitiveType BYTE = new PrimitiveType("byte", 1, "B");
	public static final PrimitiveType UBYTE = new PrimitiveType("ubyte", 1, "B"); // JVM byte, range checking needed
	public static final PrimitiveType SHORT = new PrimitiveType("short", 2, "S");
	public static final PrimitiveType USHORT = new PrimitiveType("ushort", 2, "S"); // JVM short, range checking needed
	public static final PrimitiveType INT = new PrimitiveType("int", 4, "I");
	public static final PrimitiveType UINT = new PrimitiveType("uint", 4, "I"); // JVM int, range checking needed
	public static final PrimitiveType LONG = new PrimitiveType("long", 8, "J");
	public static final PrimitiveType ULONG = new PrimitiveType("ulong", 8, "J"); // JVM long, range checking needed
	public static final PrimitiveType FLOAT = new PrimitiveType("float", 4, "F");
	public static final PrimitiveType DOUBLE = new PrimitiveType("double", 8, "D");
	public static final PrimitiveType CHAR = new PrimitiveType("char", 2, "C");
	public static final PrimitiveType STRING = new PrimitiveType("string", -1, "Ljava/lang/String;");
	public static final PrimitiveType BOOL = new PrimitiveType("bool", 1, "Z");
	public static final PrimitiveType VOID = new PrimitiveType("void", 0, "V");

	@Override
	public boolean isCompatibleWith(Type other) {
		if (!(other instanceof PrimitiveType)) {
			return false;
		}

		PrimitiveType otherPrim = (PrimitiveType) other;

		// Exact match
		if (this == otherPrim) {
			return true;
		}

		// Numeric type compatibility (simplified for now)
		if (isNumeric() && otherPrim.isNumeric()) {
			// Allow widening conversions
			return this.size <= otherPrim.size;
		}

		return false;
	}

	@Override
	public String getJVMDescriptor() {
		return jvmDescriptor;
	}

	public boolean isNumeric() {
		return this == BYTE || this == UBYTE || this == SHORT || this == USHORT ||
				this == INT || this == UINT || this == LONG || this == ULONG ||
				this == FLOAT || this == DOUBLE;
	}

	public boolean isUnsigned() {
		return this == UBYTE || this == USHORT || this == UINT || this == ULONG;
	}

	public boolean isInteger() {
		return this == BYTE || this == UBYTE || this == SHORT || this == USHORT ||
				this == INT || this == UINT || this == LONG || this == ULONG;
	}

	public boolean isFloatingPoint() {
		return this == FLOAT || this == DOUBLE;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PrimitiveType)) return false;
		PrimitiveType other = (PrimitiveType) obj;
		return name.equals(other.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Get a primitive type by its name.
	 */
	public static PrimitiveType fromName(String name) {
		switch (name) {
			case "byte":
				return BYTE;
			case "ubyte":
				return UBYTE;
			case "short":
				return SHORT;
			case "ushort":
				return USHORT;
			case "int":
				return INT;
			case "uint":
				return UINT;
			case "long":
				return LONG;
			case "ulong":
				return ULONG;
			case "float":
				return FLOAT;
			case "double":
				return DOUBLE;
			case "char":
				return CHAR;
			case "string":
				return STRING;
			case "bool":
				return BOOL;
			case "void":
				return VOID;
			default:
				return null;
		}
	}
}