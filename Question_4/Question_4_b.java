package Question_4;
import java.util.*;

/**
 * This program determines the minimum number of roads to traverse to collect all packages and return to the starting location.
 * 
 * Algorithm Overview:
 * 1. Model the problem using BFS with bitmasking to track collected packages.
 * 2. Precompute coverage masks for each node (packages within 2 steps).
 * 3. For each possible starting node, perform BFS to find the shortest cycle covering all packages.
 * 4. Track the minimum roads traversed across all starting nodes.
 */
public class Question_4_b {

    /**
     * Represents a state during BFS: current node, collected packages (bitmask), and steps taken.
     */
    private static class State {
        int node;
        int mask;
        int steps;

        public State(int node, int mask, int steps) {
            this.node = node;
            this.mask = mask;
            this.steps = steps;
        }
    }

    /**
     * Builds the adjacency list from the given roads.
     */
    private static List<List<Integer>> buildAdjacencyList(int n, int[][] roads) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] road : roads) {
            int u = road[0], v = road[1];
            adj.get(u).add(v);
            adj.get(v).add(u);
        }
        return adj;
    }

    /**
     * Finds all nodes within 2 steps from a given node using BFS.
     */
    private static Set<Integer> getNodesWithinTwoSteps(int u, List<List<Integer>> adj) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(u);
        visited.add(u);
        int steps = 0;

        while (!queue.isEmpty() && steps < 2) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int current = queue.poll();
                for (int neighbor : adj.get(current)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            steps++;
        }
        return visited;
    }

    /**
     * Computes the minimum number of roads to traverse to collect all packages.
     */
    public static int minRoads(int[] packages, int[][] roads) {
        int n = packages.length;
        List<Integer> packageIndices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) packageIndices.add(i);
        }
        if (packageIndices.isEmpty()) return 0;

        int totalPackages = packageIndices.size();
        int fullMask = (1 << totalPackages) - 1;
        List<List<Integer>> adj = buildAdjacencyList(n, roads);

        // Precompute coverage masks for all nodes
        int[] coverageMasks = new int[n];
        for (int u = 0; u < n; u++) {
            Set<Integer> withinTwoSteps = getNodesWithinTwoSteps(u, adj);
            int mask = 0;
            for (int i = 0; i < totalPackages; i++) {
                if (withinTwoSteps.contains(packageIndices.get(i))) {
                    mask |= (1 << i);
                }
            }
            coverageMasks[u] = mask;
        }

        int minSteps = Integer.MAX_VALUE;
        for (int start = 0; start < n; start++) {
            int initialMask = coverageMasks[start];
            if (initialMask == fullMask) {
                return 0; // Start and end here
            }

            int[][] dist = new int[n][1 << totalPackages];
            for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
            Queue<State> queue = new LinkedList<>();
            queue.add(new State(start, initialMask, 0));
            dist[start][initialMask] = 0;

            while (!queue.isEmpty()) {
                State current = queue.poll();
                if (current.node == start && current.mask == fullMask) {
                    minSteps = Math.min(minSteps, current.steps);
                    break;
                }
                for (int neighbor : adj.get(current.node)) {
                    int newMask = current.mask | coverageMasks[neighbor];
                    int newSteps = current.steps + 1;
                    if (newSteps < dist[neighbor][newMask]) {
                        dist[neighbor][newMask] = newSteps;
                        queue.add(new State(neighbor, newMask, newSteps));
                    }
                }
            }
        }
        return minSteps;
    }

    // Test Cases
    public static void main(String[] args) {
        // Test Case 1
        int[] packages1 = {1, 0, 0, 0, 0, 1};
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println("Test Case 1 - Expected: 2, Actual: " + minRoads(packages1, roads1));

        // Test Case 2
        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
        System.out.println("Test Case 2 - Expected: 2, Actual: " + minRoads(packages2, roads2));
    }
}

/*
Summary:
- The code uses BFS with bitmasking to track collected packages efficiently.
- Input: Packages array and roads. Output: Minimum roads to collect all packages and return.
- Precomputation of coverage masks allows quick lookup during BFS.
- Test cases validate the algorithm against expected results.
- The algorithm handles all packages and finds the minimal cycle, ensuring correctness.
*/