import Io;

int main() {
    // Test string array
    string[] names = ["Alice", "Bob", "Charlie"];
    string greeting = "Hello, " ^^ names[1] ^^ "!";
    
    // Print result
    Io.println(greeting);
    
    // Test array indexing with integer
    int[] numbers = [1, 2, 3, 4, 5];
    int first = numbers[0];
    
    return first;
}
