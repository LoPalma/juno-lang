.class public variant
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; Function: main()V
.method public static main()V
    .limit stack 20
    .limit locals 20
    ldc "This program demonstrates variant types"
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
; Local variable: string|int x
    ldc ""
; Convert string to string|int
; String already Object for union type
    astore 0 ; union type
    bipush 5
; Convert int to string|int for assignment
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer; ; box int to union
    astore 0 ; union type
    ldc "X's integer value is: "
    aload 0 ; union type
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
    ldc ":D"
; Convert string to string|int for assignment
; String already Object for union type
    astore 0 ; union type
    ldc "X's string value is '"
    aload 0 ; union type
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
    ldc "'"
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
    return
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic variant/main()V
    return
.end method

