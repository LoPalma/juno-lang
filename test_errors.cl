// Test file with intentional errors to showcase error reporting

int main() {
    int x = 5;
    int y = 10
    
    if (x > 5 {
        io.print("Hello");
    }
    
    x();  // Should trigger E-BadFun
    
    return 0;
}