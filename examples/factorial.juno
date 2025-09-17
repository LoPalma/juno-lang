// factorial.cl - Recursive factorial implementation

import io;

int factorial(int n) {
    if (n <= 1) {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
}

void main() {
    int number = 5;
    int result = factorial(number);
    
    io.print("Factorial of " ^^ string(number) ^^ "is " ^^ string(result));
}