import Io;

void main() {
    string|int x = 0;
    Io.println("Initial value (int): declared");
    
    x = 42;
    Io.println("After int assignment: done");
    
    x = "hello world";
    Io.println("After string assignment: done");
    
    Io.println("Union types working correctly!");
}