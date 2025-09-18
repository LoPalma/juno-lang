import Io;

void main() {
    int i = 42;
    long l = 1234567890123;
    float f = 3.14;
    double d = 2.71828;
    bool b = true;
    char c = 'A';
    
    Io.println("int: " ^^ string<i>);
    Io.println("long: " ^^ string<l>); 
    Io.println("float: " ^^ string<f>);
    Io.println("double: " ^^ string<d>);
    Io.println("bool: " ^^ string<b>);
    Io.println("char: " ^^ string<c>);
}