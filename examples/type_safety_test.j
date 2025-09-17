.class public type_safety_test
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; Function: test_implicit_cast()J
.method public static test_implicit_cast()J
    .limit stack 20
    .limit locals 20
; Local variable: int x
    bipush 42
    istore 0
; Local variable: ulong y
    bipush 100
    lstore 1
; Return with value
    iload 0
    lload 1
    iadd
; Converting int to long for ulong return
    i2l
    lreturn
.end method

; Function: main()J
.method public static main()J
    .limit stack 20
    .limit locals 20
; Return with value
    invokestatic type_safety_test/test_implicit_cast()J
; Return value already long - no conversion needed
    lreturn
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic type_safety_test/main()J
    getstatic java/lang/System/out Ljava/io/PrintStream;
    dup_x2
    pop
    invokevirtual java/io/PrintStream/println(J)V
    return
.end method

