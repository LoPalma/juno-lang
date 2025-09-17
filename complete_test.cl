// Complete test of Juno language features
int globalVar = 42;

public int calculateSum(int x, int y) {
    int result = x + y + globalVar;
    return result;
}

public int main() {
    int a = 10;
    int b = 20;
    int sum = calculateSum(a, b);
    return sum;
}