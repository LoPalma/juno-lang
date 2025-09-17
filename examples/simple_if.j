.class public simple_if
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; Function: test(I)J
.method public static test(I)J
    .limit stack 20
    .limit locals 20
    iload 0
    bipush 5
    if_icmplt true_3
    iconst_0
    goto end_4
true_3:
    iconst_1
end_4:
    ifeq else_1
; Return with value
    bipush 1
; Converting int to long for ulong return
    i2l
    lreturn
    goto end_2
else_1:
; Return with value
    bipush 0
; Converting int to long for ulong return
    i2l
    lreturn
end_2:
.end method

; Function: main()J
.method public static main()J
    .limit stack 20
    .limit locals 20
; Return with value
    bipush 3
    invokestatic simple_if/test(I)J
; Return value already long - no conversion needed
    lreturn
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic simple_if/main()J
    getstatic java/lang/System/out Ljava/io/PrintStream;
    dup_x2
    pop
    invokevirtual java/io/PrintStream/println(J)V
    return
.end method

