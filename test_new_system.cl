int main() {
    int x = 5;
    char bad = 'unterminated;      // Missing closing quote
    float invalid = 123.456.789;   // Multiple decimal points
    string broken = "unterminated   // Missing closing quote
    char empty = '';               // Empty character literal
    int @ weird = 5;               // Unexpected character
    return 0;
}