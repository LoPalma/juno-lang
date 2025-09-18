import Io;

void main() {
    // Array declaration
    int[5] arr;
    
    // Array initialization with values
    int[] nums = [1, 2, 3, 4, 5];
    
    // Array access
    int first = nums[0];
    Io.println("First element: " ^^ string<first>);
    
    // Array assignment
    arr[0] = 42;
    int val = arr[0];
    Io.println("Assigned value: " ^^ string<val>);
}