public class TestRunner {
    public static void main(String[] args) {
        try {
            System.out.println("=== Testing Juno Compiler Output ===");
            
            // Test tiny_test: add(1, 2) should return 3
            int result1 = tiny_test.main();
            System.out.println("tiny_test.main() = " + result1);
            System.out.println("Expected: 3, Got: " + result1 + " -> " + (result1 == 3 ? "✅ PASS" : "❌ FAIL"));
            
            // Test simple_test: test(5, 3) should return 15  
            int result2 = simple_test.main();
            System.out.println("simple_test.main() = " + result2);
            System.out.println("Expected: 15, Got: " + result2 + " -> " + (result2 == 15 ? "✅ PASS" : "❌ FAIL"));
            
            // Test working_test: complex calculation
            int result3 = working_test.main();
            System.out.println("working_test.main() = " + result3);
            // (10 + 20 + 5 + 100) * 2 = 135 * 2 = 270
            System.out.println("Expected: 270, Got: " + result3 + " -> " + (result3 == 270 ? "✅ PASS" : "❌ FAIL"));
            
            System.out.println("\n🎉 ALL TESTS COMPLETED! 🎉");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}