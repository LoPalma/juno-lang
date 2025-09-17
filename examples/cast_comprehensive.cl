// Comprehensive cast test for various type conversions

int main() {
    // Integer to floating point casts
    int x = 100;
    float f = <float>(x);
    double d = <double>(x);
    
    // Floating point to integer casts  
    float pi = 3.14;
    int truncated = <int>(pi);
    
    // Character casts
    char c = 'A';
    int ascii = <int>(c);
    char fromInt = <char>(65);
    
    // Boolean casts
    bool flag = true;
    int boolAsInt = <int>(flag);
    
    // Narrowing integer casts
    long big = 256;
    byte small = <byte>(big);  // This will truncate
    
    // Return the truncated pi value
    return truncated;
}