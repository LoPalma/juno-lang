.class public optional_void_assignment
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
; Local variable: optional int x
    bipush 0
; Convert int to optional int
    astore 0
    bipush 5
; Convert int to optional int for assignment
    astore 0
    return
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic optional_void_assignment/main()V
    return
.end method

