// Working comprehensive test of Juno compiler
bool isActive = true;
float pi = 3.14159;
int counter = 100;

public int addNumbers(int a, int b, int c) {
    int result = a + b + c + counter;
    return result;
}

public int multiply(int x, int y) {
    int product = x * y;
    return product;
}

public int main() {
    int first = 10;
    int second = 20;
    int third = 5;
    
    int sum = addNumbers(first, second, third);
    int doubled = multiply(sum, 2);
    
    return doubled;
}