.class public fibonacci
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; Function: fib(I)I
.method public static fib(I)I
    .limit stack 20
    .limit locals 20
    iload 0
    bipush 2
    if_icmplt true_3
    iconst_0
    goto end_4
true_3:
    iconst_1
end_4:
    ifeq else_1
; Return with value
    iload 0
    ireturn
    goto end_2
else_1:
end_2:
; Return with value
    iload 0
    bipush 1
    isub
    invokestatic fibonacci/fib(I)I
    iload 0
    bipush 2
    isub
    invokestatic fibonacci/fib(I)I
    iadd
    ireturn
.end method

; Function: main()V
.method public static main()V
    .limit stack 20
    .limit locals 20
; Local variable: string x
    bipush 9
    invokestatic fibonacci/fib(I)I
    invokestatic java/lang/String/valueOf(I)Ljava/lang/String; ; int to string
; Convert string to string
    astore 0
    aload 0
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
    ldc "Done!"
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
    return
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic fibonacci/main()V
    return
.end method

