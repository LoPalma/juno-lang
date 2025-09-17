// Test the new cast syntax: type<expr>

int main() {
    // Test the new syntax
    int x = 42;
    long y = long<x>;        // Instead of <long>(x)
    int w = int<y>;          // Instead of <int>(y)
    
    // Test with literals
    float f = float<100>;    // Instead of <float>(100)
    int truncated = int<3.14>;  // Instead of <int>(3.14)
    
    return truncated;
}