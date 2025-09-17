import Io;

int main() {
    string greeting = "Hello " ^^ "World!";
    Io.println(greeting);
    
    Io.print("Enter your name: ");
    string name = Io.scan();
    string message = "Hi, " ^^ name ^^ "!";
    Io.println(message);
    
    return 0;
}