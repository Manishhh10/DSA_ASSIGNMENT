package Question_2;
public class Question_2_b {

    /*
     * This program finds the lexicographically smallest pair of points with the smallest Manhattan distance.
     *
     * The Manhattan distance between two points (x[i], y[i]) and (x[j], y[j]) is defined as:
     *      |x[i] - x[j]| + |y[i] - y[j]|
     *
     * The approach is as follows:
     * 1. Iterate over all pairs of points (i, j) where i < j to avoid duplicate pairs.
     * 2. Compute the Manhattan distance for each pair.
     * 3. Keep track of the minimum distance found and the corresponding pair of indices.
     * 4. If a pair has the same distance as the current minimum, update the pair if it is lexicographically smaller.
     *
     * This method guarantees that the returned pair is the lexicographically smallest among those with the smallest distance.
     */

    /**
     * Finds the lexicographically smallest pair of indices corresponding to the points with the smallest Manhattan distance.
     * <p>
     * The function iterates over all distinct pairs (i, j) (with i < j), calculates the Manhattan distance between
     * the points at these indices, and updates the result if:
     *  - The current pair's distance is smaller than the minimum found so far, or
     *  - The distance is the same as the minimum but the pair (i, j) is lexicographically smaller.
     *
     * @param x_coords an array of x-coordinates for the points.
     * @param y_coords an array of y-coordinates for the points.
     * @return an array of two integers representing the indices of the closest pair of points.
     */
    public static int[] findClosestPair(int[] x_coords, int[] y_coords) {
        int n = x_coords.length;
        int minDistance = Integer.MAX_VALUE;
        int[] result = new int[]{-1, -1};

        // Iterate through all pairs of points where the first index is less than the second index.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Calculate the Manhattan distance between point i and point j.
                int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]);

                // Check if this pair should update our result:
                // 1. If the distance is less than the current minimum, update.
                // 2. If the distance equals the current minimum, choose the lexicographically smaller pair.
                if (distance < minDistance || (distance == minDistance && (i < result[0] || (i == result[0] && j < result[1])))) {
                    minDistance = distance;
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        // Example Test Case:
        // Input:
        //   x_coords = [1, 2, 3, 2, 4]
        //   y_coords = [2, 3, 1, 2, 3]
        // Expected Output: [0, 3]
        //
        // Explanation:
        //   - Point 0: (1, 2)
        //   - Point 3: (2, 2)
        //   The Manhattan distance between these points is:
        //       |1 - 2| + |2 - 2| = 1 + 0 = 1, which is the smallest distance found.
        //   Although there is another pair (1, 3) with distance 1, the pair [0, 3] is lexicographically smaller.
        
        int[] x_coords = {1, 2, 3, 2, 4};
        int[] y_coords = {2, 3, 1, 2, 3};

        int[] closestPair = findClosestPair(x_coords, y_coords);
        System.out.println("Closest Pair: [" + closestPair[0] + ", " + closestPair[1] + "]");

        /*
         * Summary:
         * We implemented a solution to find the lexicographically smallest pair of points with the smallest Manhattan distance.
         * The algorithm computes the Manhattan distance for every distinct pair of points (i, j) where i < j.
         * It then tracks the pair with the smallest distance, updating it if a lexicographically smaller pair with the same
         * distance is found. For the input arrays:
         *   x_coords = [1, 2, 3, 2, 4]
         *   y_coords = [2, 3, 1, 2, 3]
         * The expected output is [0, 3] and our program outputs this result, confirming that the algorithm works as expected.
         */
    }
}
