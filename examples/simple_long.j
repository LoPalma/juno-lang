.class public simple_long
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
; Local variable: long x
    ldc 42
    i2l ; convert to long
    lstore 0
; Return with value
    ldc 0
    i2l ; convert to long
    ireturn
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic simple_long/main()I
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(I)V
    return
.end method

