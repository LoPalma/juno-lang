.class public test_union_simple
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
; Local variable: string|int x
    bipush 0
; Convert int to string|int
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer; ; box int to union
    astore 0 ; union type
    return
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic test_union_simple/main()V
    return
.end method

