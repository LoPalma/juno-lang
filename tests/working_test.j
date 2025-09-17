.class public working_test
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.field public static isActive Z
.field public static pi F
.field public static counter I
; Function: addNumbers(III)I
.method public static addNumbers(III)I
    .limit stack 20
    .limit locals 20
.end method

; Function: multiply(II)I
.method public static multiply(II)I
    .limit stack 20
    .limit locals 20
.end method

; Function: main()I
.method public static main()I
    .limit stack 20
    .limit locals 20
.end method

; Static initializer for global variables
.method static <clinit>()V
    .limit stack 10
    .limit locals 1
; Initialize pi
    d2f
; Initialize counter
; Initialize isActive
    return
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic working_test/main()I
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(I)V
    return
.end method

