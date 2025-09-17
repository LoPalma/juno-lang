// Test various type system scenarios including string handling

int main() {
    // String operations
    string message = "Hello, World!";
    string greeting = "Hi " ^^ "there";
    
    // Mixed type operations (should cause errors)
    int x = 5;
    string y = "test";
    // int z = x + y;  // This should be an error - no implicit conversion
    
    // Boolean operations
    bool flag1 = true;
    bool flag2 = false;
    bool combined = flag1 && flag2;
    
    // Character operations
    char c = 'A';
    int ascii = <int>(c);  // Using old cast syntax
    
    return ascii;
}