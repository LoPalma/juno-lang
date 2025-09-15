// factorial.cl - Recursive factorial implementation

import io;

int factorial(int n) {
    if (n <= 1) {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
}

int main() {
    int number = 5;
    int result = factorial(number);
    
    io.print("Factorial of ");
    io.print(number);
    io.print(" is ");
    io.print(result);
    
    return 0;
}