.class public function_test
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; Function: multiply(II)I
.method public static multiply(II)I
    .limit stack 20
    .limit locals 20
; Local variable: int result
    iload 0
    iload 1
    imul
    istore 2
; Return with value
    iload 2
    ireturn
    ireturn
.end method

; Function: add(II)I
.method public static add(II)I
    .limit stack 20
    .limit locals 20
; Return with value
    iload 0
    iload 1
    iadd
    ireturn
    ireturn
.end method

; Function: main()I
.method public static main()I
    .limit stack 20
    .limit locals 20
; Local variable: int num1
    bipush 3
    istore 0
; Local variable: int num2
    bipush 4
    istore 1
; Local variable: int product
    iload 0
    iload 1
    istore 2
; Local variable: int sum
    iload 2
    bipush 10
    istore 3
; Return with value
    iload 3
    ireturn
    ireturn
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic function_test/main()I
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(I)V
    return
.end method

