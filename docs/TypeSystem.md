# Type system
This piece of documentation regards  Juno's typesystem.

## Basic types

## Type table
This table sums up all Junpo's types:

| Juno type | Width (bits) |         C type |
|:----------|:------------:|---------------:|
| int       |      32      |     signed int |
| uint      |      32      |       unsigned |
| short     |      16      |   signed short |
| ushort    |      16      | unsigned short |
| long      |      64      |    signed long |
| ulong     |      64      |  unsigned long |
| byte      |      8       |      (int)char |
| ubyte     |      8       | (unsigned)char |
| bool      |      1       |          _Bool |
| char      |      8       |           char |
| string    |      ?       |                |
| any       |      ?       |                |

`any` can be any type.

`auto` is the type inferring keyword.

