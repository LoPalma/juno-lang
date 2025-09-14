// factorial.cl - Recursive factorial implementation

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
    
    print("Factorial of ");
    print(number);
    print(" is ");
    print(result);
    
    return 0;
}