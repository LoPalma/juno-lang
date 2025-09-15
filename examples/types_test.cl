// types_test.cl - Test all supported type keywords

import io;

int main() {
    // Signed integer types
    byte b = 127;
    short s = 32000;
    int i = 2147483647;
    long l = 123456789012345;
    
    // Unsigned integer types  
    ubyte ub = 255;
    ushort us = 65535;
    uint ui = 3000000000;
    ulong ul = 1234567890123456;
    
    // Floating point types
    float f = 3.14159;
    double d = 2.718281828459045;
    
    // Other types
    char c = 'A';
    string str = "Hello, World!";
    bool flag = true;
    
    // Test that literals work with multiple types
    byte small1 = 42;
    ubyte small2 = 42;
    int medium1 = 42;
    uint medium2 = 42;
    
    io.print("Type system test complete");
    return 0;
}