// Test program for explicit cast expressions in Juno
// Tests various casting scenarios

int main() {
    // Test integer to long cast
    int x = 42;
    long y = <long>(x);
    
    // Test long to int cast (potential data loss)
    long z = 1000;
    int w = <int>(z);
    
    // Test casting with return value
    return <int>(y);
}
