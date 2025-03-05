package Question_2;
public class Question_2_a {
    /*
     * This program determines the minimum number of rewards needed to distribute among employees
     * based on their performance ratings stored in an integer array.
     *
     * Rules:
     * 1. Every employee must receive at least one reward.
     * 2. Any employee with a higher rating than an adjacent employee must receive more rewards.
     */
    
    /**
     * Computes the minimum number of rewards needed based on employee ratings.
     * <p>
     * The method uses two passes through the ratings array:
     * <br>
     * - The left-to-right pass ensures that if an employee's rating is higher than the previous one,
     *   then they receive one more reward than the previous employee.
     * <br>
     * - The right-to-left pass ensures that if an employee's rating is higher than the next one,
     *   then they receive the maximum of their current reward or one more than the next employee.
     * <br>
     * Finally, the rewards are summed up and returned.
     *
     * @param ratings An array of integers representing employee performance ratings.
     * @return The minimum number of rewards required.
     */
    public static int minRewards(int[] ratings) {
        int n = ratings.length;
        if (n == 0) return 0;
        
        // Initialize rewards array with at least 1 reward for each employee
        int[] rewards = new int[n];
        for (int i = 0; i < n; i++) {
            rewards[i] = 1;
        }
        
        // Left-to-right pass: ensure right neighbor gets more rewards if rating is higher
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }
        
        // Right-to-left pass: ensure left neighbor gets more rewards if rating is higher
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }
        
        // Sum up the rewards for all employees
        int totalRewards = 0;
        for (int reward : rewards) {
            totalRewards += reward;
        }
        
        return totalRewards;
    }
    
    public static void main(String[] args) {
        // Test Case 1:
        // Input: ratings = [1, 0, 2]
        // Expected Output: 5 (Allocation: 2, 1, 2)
        int[] ratings1 = {1, 0, 2};
        int result1 = minRewards(ratings1);
        System.out.println("Test Case 1 - Expected Output: 5, Actual Output: " + result1);
        
        // Test Case 2:
        // Input: ratings = [1, 2, 2]
        // Expected Output: 4 (Allocation: 1, 2, 1)
        int[] ratings2 = {1, 2, 2};
        int result2 = minRewards(ratings2);
        System.out.println("Test Case 2 - Expected Output: 4, Actual Output: " + result2);
        
        /*
         * Summary:
         * We implemented a solution to determine the minimum rewards to be distributed among employees.
         * The approach involved a left-to-right pass to ensure that employees with higher ratings than their
         * left neighbor received one more reward, followed by a right-to-left pass to handle the right neighbor.
         * The test cases confirmed that for the input ratings [1, 0, 2] the total rewards are 5, and for 
         * [1, 2, 2] the total rewards are 4, matching the expected outputs. This verifies that the code and algorithm
         * work as intended.
         */
    }
}
