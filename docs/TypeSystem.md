# Juno Type System

This document describes the type system of the **Juno** programming language.

Juno is a **statically typed** and **very strongly typed** language. All types are resolved at compile time and implicit conversions are not permitted. Operations with mismatched operand types produce compile-time errors. For example:

```juno
3.0 / 2;  // error: operand type mismatch
```

Even types that appear dynamic (for example `any`) are statically checked — you cannot perform an operation with incompatible operand types even when one operand is `any`:

```juno
any a = 42;
a = 3.14;
a / 2;   // error: cannot apply '/' to (any, int)
```

All casts must be explicit. Casting uses the syntax `<type>(<expr>)`:

```juno
int x = int(3.0);    // explicit cast
float y = float(2);  // explicit cast
```

---

## Casting (explicit only)

* **Syntax:** `<type>(<expr>)`
* **Rule:** No implicit numeric or pointer conversions are performed. Any conversion that changes or narrows the representation must be written explicitly with a cast.
* **Errors:** Casting that is invalid or cannot represent the value will be rejected by the compiler.

Examples:

```juno
int a = int(3.7);       // allowed (value truncated if implementation-defined)
int b = int(3.0 / 2.0); // allowed with explicit cast
float f = float(1);     // allowed with explicit cast

// Implicit conversion is not allowed:
int c = 3.0;            // error: implicit conversion from float to int
```

---

## Primitive Types

The following table summarizes Juno’s built-in primitive types:

| Juno type |  Width (bits) | C equivalent           |
| :-------- | :-----------: | ---------------------- |
| `int`     |       32      | `signed int`           |
| `uint`    |       32      | `unsigned int`         |
| `short`   |       16      | `signed short`         |
| `ushort`  |       16      | `unsigned short`       |
| `long`    |       64      | `signed long`          |
| `ulong`   |       64      | `unsigned long`        |
| `byte`    |       8       | `char` (signed)        |
| `ubyte`   |       8       | `unsigned char`        |
| `bool`    |       1       | `_Bool`                |
| `char`    |       8       | `char`                 |
| `string`  | runtime-sized | implementation-defined |
| `any`     | runtime-sized | implementation-defined |

### Special Types

* **`string`**

    * Dynamically sized, UTF‑8 encoded sequence of characters.
    * Strings are immutable.
    * Storage and lifetime are managed by the runtime (implementation-defined).

* **`any`**

    * A statically-typed generic container capable of holding a value of any concrete type.
    * Representation is implementation-defined (commonly a boxed value with a tag); semantics are statically enforced.
    * Operations on `any` are only allowed when the operation is valid for all possible types represented by that `any` value in context; otherwise the compiler reports an error.

* **`auto`**

    * Type inference keyword. The compiler infers the static type of the variable from the initializer at compile time.

```juno
auto x = 10;       // x : int
auto y = "hello"; // y : string
```

---

## Variant Types

Juno supports **variant types** (discriminated unions) using the `|` operator. Variant types are resolved statically and are typically implemented as tagged unions.

```juno
int|string v;
v = 5;      // valid
v = "hi";  // valid
v = 0.3;     // error: type mismatch
```

Variants are useful for expressing values that may take one of several concrete types while still preserving compile-time type safety.

### Optional Types

Including `void` in a variant allows representing a value that may be absent. Juno provides the `optional` keyword as a convenience for this common pattern.

```juno
optional int maybe;
maybe = 42;      // has value
maybe = nullptr; // no value
```

**Nullability:** Any pointer-like type that can be null must be declared `optional`. Non-optional pointers are guaranteed to be non-null and the compiler will prevent assigning `nullptr` to them.

---

## Type Aliases

Use the `type` keyword to create aliases for complex type expressions (including variants):

```juno
type C_char = char|byte;
C_char c = 'a';
```

---

## Type Utilities (Module `Type`)

The `Type` module exposes utilities for inspecting and working with types at runtime (while preserving static safety).

### `Type.typeof`

```juno
optional any typeof(optional any expr)
```

Returns the static type of the provided expression as an `any` value. This is primarily useful for reflection-like features or compiler tools.

Example:

```juno
int x = 5;
auto t = Type.typeof(x);
// `t` represents the type `int`
```

### `Type.isVoid`

```juno
bool isVoid(optional any expr)
```

Returns `true` if the given optional value is empty (`nullptr`), otherwise returns `false`.

Example:

```juno
optional int x;
x = nullptr;
if (Type.isVoid(x)) {
    // handle absent value
}
```

---

## Examples

```juno
// explicit casting
float f = 3.0;
int i = int(f); // explicit cast required

// variants and optionals
int|string v;
v = 100;
if (Type.typeof(v) == /* type value for int */) {
    // handle int case
}

optional string s = nullptr;
if (Type.isVoid(s)) {
    // s has no value
}
```

---

## Summary

* Juno is **statically typed** and **very strongly typed**.
* **No implicit conversions** — all casts are explicit using `<type>(<expr>)`.
* Primitive types have fixed widths as documented above.
* Variant and optional types provide expressive, safe alternatives to unchecked unions and nullable pointers.
* The `Type` module offers helpful utilities (`typeof`, `isVoid`) for type inspection and handling optional values.
