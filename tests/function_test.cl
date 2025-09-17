public int multiply(int x, int y) {
    int result = x * y;
    return result;
}

public int add(int a, int b) {
    return a + b;
}

int main() {
    int num1 = 3;
    int num2 = 4;
    int product = multiply(num1, num2);
    int sum = add(product, 10);
    return sum;
}