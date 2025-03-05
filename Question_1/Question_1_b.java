package Question_1;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This program finds the kth lowest combined return from two sorted arrays of investment returns.
 * 
 * Algorithm Overview:
 * 1. Compute all possible products by multiplying each element from returns1 with each element from returns2.
 * 2. Store these products in a list.
 * 3. Sort the list of products in ascending order.
 * 4. Return the kth smallest product from the sorted list (k is 1-indexed, so we return the element at index k-1).
 *
 * This approach uses a brute-force method which is efficient for small input sizes.
 */

public class Question_1_b {
    
    /**
     * Computes the kth lowest combined return.
     * <p>
     * This function iterates through each element in the first sorted array and multiplies it with each element in the 
     * second sorted array. All the products are collected into a list. The list is then sorted and the kth smallest 
     * product (1-indexed) is returned.
     *
     * @param returns1 The first sorted array of investment returns.
     * @param returns2 The second sorted array of investment returns.
     * @param k The target index (1-indexed) for the kth lowest combined return.
     * @return The kth lowest combined return.
     */
    public static int kthLowestCombinedReturn(int[] returns1, int[] returns2, int k) {
        // List to store all combined returns (products)
        List<Integer> combinedReturns = new ArrayList<>();
        
        // Compute all possible products from the two arrays
        for (int r1 : returns1) {
            for (int r2 : returns2) {
                combinedReturns.add(r1 * r2);
            }
        }
        
        // Sort the list of products in ascending order
        Collections.sort(combinedReturns);
        
        // Return kth smallest product (k is 1-indexed, so index is k-1)
        return combinedReturns.get(k - 1);
    }
    
    public static void main(String[] args) {
        // Test Case 1:
        // Input: returns1 = [2, 5], returns2 = [3, 4], k = 2
        // Expected Output: 8
        int[] returns1_example1 = {2, 5};
        int[] returns2_example1 = {3, 4};
        int k1 = 2;
        int result1 = kthLowestCombinedReturn(returns1_example1, returns2_example1, k1);
        System.out.println("Test Case 1 - Expected Output: 8, Actual Output: " + result1);
        
        // Test Case 2:
        // Input: returns1 = [-4, -2, 0, 3], returns2 = [2, 4], k = 6
        // Expected Output: 0
        int[] returns1_example2 = {-4, -2, 0, 3};
        int[] returns2_example2 = {2, 4};
        int k2 = 6;
        int result2 = kthLowestCombinedReturn(returns1_example2, returns2_example2, k2);
        System.out.println("Test Case 2 - Expected Output: 0, Actual Output: " + result2);

        // Test Case 3:
        // Input: returns1 = [-4, -2, 0, 3], returns2 = [2, 4], k = 6
        // Expected Output: 0
        int[] returns1_example3 = {1, 3, 5, 7};
        int[] returns2_example3 = {2, 5};
        int k3 = 4;
        int result3 = kthLowestCombinedReturn(returns1_example3, returns2_example3, k3);
        System.out.println("Test Case 3 - Expected Output: 10, Actual Output: " + result3);
        
        /*
         * Summary:
         * We implemented a solution to find the kth lowest combined return from two sorted arrays of investment returns.
         * The approach involved computing every product from the two arrays, sorting the results, and then selecting the kth smallest value.
         * For Test Case 1, the input was returns1 = [2, 5] and returns2 = [3, 4] with k = 2, yielding an output of 8.
         * For Test Case 2, the input was returns1 = [-4, -2, 0, 3] and returns2 = [2, 4] with k = 6, yielding an output of 0.
         * The actual outputs matched the expected outputs, confirming that the code and algorithm work as required.
         */
    }
}
