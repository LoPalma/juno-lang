.class public hello
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
; Local variable: string msg
    ldc "Hello Juno!"
    astore 0
    aload 0
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
    return
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic hello/main()V
    return
.end method

