.class public io_test
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
; Local variable: string greeting
; Literal value: Hello  (String)
; Literal AST type: string
; shouldBeLong: false
    ldc "Hello "
; Literal value: World! (String)
; Literal AST type: string
; shouldBeLong: false
    ldc "World!"
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
    astore 0
    aload 0
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
; Literal value: Enter your name:  (String)
; Literal AST type: string
; shouldBeLong: false
    ldc "Enter your name: "
    invokestatic com/juno/runtime/Io/print(Ljava/lang/String;)V
; Local variable: string name
    invokestatic com/juno/runtime/Io/scan()Ljava/lang/String;
    astore 1
; Local variable: string message
; Literal value: Hi,  (String)
; Literal AST type: string
; shouldBeLong: false
    ldc "Hi, "
    aload 1
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
; Literal value: ! (String)
; Literal AST type: string
; shouldBeLong: false
    ldc "!"
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    swap
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    invokevirtual java/lang/String/concat(Ljava/lang/String;)Ljava/lang/String;
    astore 2
    aload 2
    invokestatic com/juno/runtime/Io/println(Ljava/lang/String;)V
; Return with value
; Function return type: int
; isLongType(currentFunctionReturnType): false
; Literal value: 0 (Integer)
; Literal AST type: int
; shouldBeLong: false
    bipush 0
; Return expression type: int
; producesLongValue: false
    ireturn
.end method

; JVM-compatible main method wrapper
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 1
    invokestatic io_test/main()I
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(I)V
    return
.end method

