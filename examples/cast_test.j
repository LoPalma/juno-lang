.class public cast_test
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; Function: main()I
.method public static main()I
    .limit stack 20
    .limit locals 20
; Local variable: int x
    ldc 42
    i2l ; convert to long
; Convert long to int
    l2i ; long to int
    istore 0
; Local variable: long y
    iload 0
    i2l ; int to long
    lstore 1
; Local variable: long z
    ldc 1000
    i2l ; convert to long
    lstore 3
; Local variable: int w
    lload 3
    l2i ; long to int
    istore 5
; Return with value
    lload 1
    l2i ; long to int
    ireturn
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic cast_test/main()I
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(I)V
    return
.end method

