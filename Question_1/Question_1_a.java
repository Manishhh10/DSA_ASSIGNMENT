package Question_1;
/**
 * Finds the minimum number of tests needed to determine the critical temperature using up to k samples and n temperatures.
 * 
 * Approach:
 * The problem is similar to finding the least number of attempts to determine the highest safe floor in a building using a limited number of test samples.
 * The idea is to calculate how many different possibilities (critical temperatures) we can check with a certain number of tests (m) and samples (k).
 * We start with m=0 and increase it until the total possibilities cover all possible critical temperatures (n+1 possibilities, since f can be from 0 to n).
 * 
 * How it works:
 * For each possible number of tests (m), we calculate the maximum number of critical temperatures we can determine by considering different scenarios:
 * - Using 0 samples (no test breaks the sample)
 * - Using 1 sample (one test breaks it)
 * - Up to using k samples (each failed test uses a sample)
 * The sum of these scenarios gives the total possibilities. Once this sum is >= n+1, we have our answer.
 */
public class Question_1_a {

    public static void main(String[] args) {
        // Example 1: 1 sample, 2 temperatures. Expected answer: 2
        System.out.println(minimalMeasurements(1, 2));
        
        // Example 2: 2 samples, 6 temperatures. Expected answer: 3
        System.out.println(minimalMeasurements(2, 6));
        
        // Example 3: 3 samples, 14 temperatures. Expected answer: 4
        System.out.println(minimalMeasurements(3, 14));
    }

    /**
     * Calculates the minimum number of tests needed to find the critical temperature.
     * 
     * @param k Number of samples available.
     * @param n Number of temperature levels (0 to n).
     * @return Minimum number of tests required.
     */
    public static int minimalMeasurements(int k, int n) {
        int required = n + 1; // Total possibilities to cover (0 to n inclusive)
        int m = 0; // Start checking from 0 tests

        while (true) {
            int currentSum = 0; // Total possibilities checked with m tests
            int currentTerm = 1; // Represents scenarios where 0 samples are broken (initial case)
            int maxSamplesUsed = Math.min(m, k); // Can't use more samples than tests or available samples

            // Calculate how many possibilities we can check with m tests
            for (int i = 0; i <= maxSamplesUsed; i++) {
                currentSum += currentTerm; // Add possibilities where i samples are broken

                // Prepare next term for (i+1) samples broken, if possible
                if (i < maxSamplesUsed) {
                    currentTerm = currentTerm * (m - i) / (i + 1);
                }
            }

            // Check if we've covered all required possibilities
            if (currentSum >= required) {
                return m; // Found the minimum number of tests
            }
            m++; // Try with one more test
        }
    }
}

/*
Explanation of Code Execution:
- The code starts with m=0 and increments it until the total possibilities checked (currentSum) meet or exceed required (n+1).
- For each m, it calculates how many critical temperatures can be determined by considering all scenarios from 0 to k samples broken.
- The 'currentTerm' efficiently calculates each scenario using the previous term to avoid complex calculations.
- The examples provided in the main method test the function and print the correct expected results, confirming the code works as intended.
*/