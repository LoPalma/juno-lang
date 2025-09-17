// Final comprehensive test of Juno compiler
bool isActive = true;
float pi = 3.14159;
string message = "Juno Language";

public int fibonacci(int n) {
    if (n == 0) {
        return 0;
    }
    if (n == 1) {
        return 1;
    }
    int a = fibonacci(n - 1);
    int b = fibonacci(n - 2);
    return a + b;
}

public bool checkCondition(int value) {
    bool result = value > 10 && isActive;
    return result;
}

public int main() {
    int x = 8;
    int fib = fibonacci(x);
    bool check = checkCondition(fib);
    
    if (check) {
        return fib * 2;
    } else {
        return fib + 1;
    }
}