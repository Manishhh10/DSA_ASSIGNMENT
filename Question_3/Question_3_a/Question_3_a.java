package Question_3.Question_3_a;
import java.util.*;

/**
 * This program calculates the minimum cost to connect all devices in a network, 
 * either by installing communication modules on devices or using direct connections.
 * 
 * Algorithm Overview:
 * 1. Treat the problem as a Minimum Spanning Tree (MST) problem with a virtual node.
 * 2. Add a virtual node (node 0) connected to each device (nodes 1 to n) with an edge cost equal to the module cost of the device.
 * 3. Include all given direct connections between devices as edges.
 * 4. Use Kruskal's algorithm to find the MST of the extended graph (n+1 nodes including the virtual node).
 * 5. The MST's total cost gives the minimum cost to connect all devices (modules + connections).
 */

class DSU {
    int[] parent;
    int[] rank;

    // Initialize Disjoint Set Union (DSU) for Kruskal's algorithm
    public DSU(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i; // Each node is its own parent initially
        }
    }

    // Find root of a node with path compression
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    // Union two sets by rank to keep the tree flat
    public boolean union(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot == yRoot) return false; // Already connected

        // Union by rank to balance tree height
        if (rank[xRoot] < rank[yRoot]) {
            parent[xRoot] = yRoot;
        } else {
            parent[yRoot] = xRoot;
            if (rank[xRoot] == rank[yRoot]) {
                rank[xRoot]++;
            }
        }
        return true;
    }
}

public class Question_3_a {

    /**
     * Calculates the minimum total cost to connect all devices using MST with a virtual node.
     * 
     * @param n Number of devices.
     * @param modules Array where modules[i] is the cost to install a module on device (i+1).
     * @param connections Array of connections between devices [device1, device2, cost].
     * @return Minimum total cost to connect all devices.
     */
    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        List<int[]> edges = new ArrayList<>();

        // Add edges from virtual node (0) to each device (cost = module cost)
        for (int i = 1; i <= n; i++) {
            edges.add(new int[]{0, i, modules[i - 1]});
        }

        // Add all direct connections between devices (device1 and device2 are 1-based)
        for (int[] conn : connections) {
            edges.add(new int[]{conn[0], conn[1], conn[2]});
        }

        // Sort edges by cost in ascending order for Kruskal's algorithm
        Collections.sort(edges, (a, b) -> a[2] - b[2]);

        DSU dsu = new DSU(n + 1); // Nodes 0 to n (virtual node is 0)
        int totalCost = 0;
        int edgesUsed = 0;

        // Process each edge in sorted order
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int cost = edge[2];

            if (dsu.find(u) != dsu.find(v)) {
                dsu.union(u, v);
                totalCost += cost;
                edgesUsed++;
                if (edgesUsed == n) break; // MST for (n+1 nodes) requires n edges
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {
        // Test Case 1 (Sample Input)
        int n1 = 3;
        int[] modules1 = {1, 2, 2};
        int[][] connections1 = {{1, 2, 1}, {2, 3, 1}};
        System.out.println("Test Case 1 - Expected: 3, Actual: " + minTotalCost(n1, modules1, connections1));

        // Test Case 2 (All devices use modules)
        int n2 = 1;
        int[] modules2 = {5};
        int[][] connections2 = {};
        System.out.println("Test Case 2 - Expected: 5, Actual: " + minTotalCost(n2, modules2, connections2));

        // Test Case 3 (Mix of modules and connections)
        int n3 = 2;
        int[] modules3 = {3, 4};
        int[][] connections3 = {{1, 2, 5}};
        System.out.println("Test Case 3 - Expected: 7, Actual: " + minTotalCost(n3, modules3, connections3));
    }
}

/*
Code Execution Summary:
1. We model the problem using a virtual node to represent module installation costs as edges.
2. All possible edges (module installations and direct connections) are added to a list.
3. Kruskal's algorithm finds the MST of the extended graph, ensuring all devices are connected at minimum cost.
4. Test cases validate correctness:
   - Test Case 1 matches the sample input/output (3).
   - Test Case 2 verifies a single device uses its module (5).
   - Test Case 3 confirms a mix of modules and connections (7).
The code produces the expected outputs, demonstrating correct functionality as per the problem requirements.
*/