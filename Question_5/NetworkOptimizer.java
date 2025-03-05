/*
 * NetworkOptimizer.java
 *
 * This file implements a Network Topology Optimizer application using Java Swing.
 * The algorithm allows users to build a network graph by adding nodes and edges.
 * It then performs network optimization tasks such as computing a Minimum Spanning Tree (MST)
 * to minimize total cost and finding the shortest path based on bandwidth.
 *
 * How it works:
 * - Users interact with a graphical panel to add nodes and edges.
 * - The MST algorithm uses a Disjoint Set Union (DSU) data structure to efficiently
 *   select edges that connect all nodes with minimum cost.
 * - A modified shortest path algorithm computes paths where edge weights are inversely
 *   proportional to bandwidth (i.e., higher bandwidth means lower effective cost).
 * - The UI displays the network graph, highlights the MST and shortest paths, and shows network stats.
 *
 * Summary:
 * In this implementation, we built an interactive network optimizer that allows users to
 * visually construct a network and compute optimal paths and spanning trees. The algorithms,
 * including MST computation and bandwidth-based shortest path calculation, have been integrated
 * into a Swing GUI, and testing indicates that the application functions as expected.
 */

 package Question_5;

 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 import java.util.*;
 import java.util.List;
 
 public class NetworkOptimizer extends JFrame {
     private GraphPanel graphPanel;
     private JLabel costLabel, latencyLabel;
 
     /**
      * Constructor for NetworkOptimizer.
      * Initializes the main window, sets title, size, default close operation,
      * and initializes UI components.
      */
     public NetworkOptimizer() {
         setTitle("Network Topology Optimizer");
         setSize(800, 600);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         initComponents();
     }
 
     /**
      * Initializes the UI components including the graph panel and control panel.
      * Sets up buttons for adding nodes, adding edges, finding MST, and finding the shortest path.
      */
     private void initComponents() {
         graphPanel = new GraphPanel();
         JPanel controlPanel = new JPanel(new GridLayout(0, 1));
 
         JButton addNodeBtn = new JButton("Add Node");
         JButton addEdgeBtn = new JButton("Add Edge");
         JButton mstBtn = new JButton("Find MST (Cost)");
         JButton shortestPathBtn = new JButton("Find Shortest Path (Bandwidth)");
 
         costLabel = new JLabel("Total Cost: 0");
         latencyLabel = new JLabel("Latency: N/A");
 
         controlPanel.add(addNodeBtn);
         controlPanel.add(addEdgeBtn);
         controlPanel.add(mstBtn);
         controlPanel.add(shortestPathBtn);
         controlPanel.add(costLabel);
         controlPanel.add(latencyLabel);
 
         add(graphPanel, BorderLayout.CENTER);
         add(controlPanel, BorderLayout.EAST);
 
         addNodeBtn.addActionListener(e -> graphPanel.setMode(GraphPanel.Mode.ADD_NODE));
         addEdgeBtn.addActionListener(e -> graphPanel.setMode(GraphPanel.Mode.ADD_EDGE));
         mstBtn.addActionListener(e -> {
             graphPanel.findMST();
             updateStats();
         });
         shortestPathBtn.addActionListener(e -> graphPanel.startShortestPathSelection());
     }
 
     /**
      * Updates the statistics displayed on the control panel.
      * Retrieves the total cost from the graph model's MST computation and updates the label.
      */
     private void updateStats() {
         int totalCost = graphPanel.getGraphModel().calculateTotalCost();
         costLabel.setText("Total Cost: " + totalCost);
     }
 
     /**
      * Main method to run the Network Topology Optimizer.
      * Launches the Swing UI in the Event Dispatch Thread.
      *
      * @param args command line arguments (not used)
      */
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> new NetworkOptimizer().setVisible(true));
     }
 }
 
 /**
  * GraphPanel is a custom JPanel that handles user interactions and drawing of the network graph.
  */
 class GraphPanel extends JPanel {
     enum Mode { ADD_NODE, ADD_EDGE, SELECT_PATH }
     private Mode currentMode = Mode.ADD_NODE;
     private GraphModel graphModel = new GraphModel();
     private GraphModel.GraphNode selectedNode;
     private GraphModel.GraphNode pathStartNode;
 
     /**
      * Constructor for GraphPanel.
      * Sets the background color and adds a mouse listener to handle click events.
      */
     public GraphPanel() {
         setBackground(Color.WHITE);
         addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 handleClick(e.getX(), e.getY());
             }
         });
     }
 
     /**
      * Handles mouse click events based on the current mode.
      * In ADD_NODE mode, adds a new node. In ADD_EDGE mode, connects two nodes with an edge.
      * In SELECT_PATH mode, selects two nodes to compute the shortest path.
      *
      * @param x the x-coordinate of the mouse click
      * @param y the y-coordinate of the mouse click
      */
     private void handleClick(int x, int y) {
         switch (currentMode) {
             case ADD_NODE:
                 graphModel.addNode(new GraphModel.GraphNode(x, y));
                 repaint();
                 break;
             case ADD_EDGE:
                 GraphModel.GraphNode node = graphModel.getNodeAt(x, y);
                 if (node != null) {
                     if (selectedNode == null) {
                         selectedNode = node;
                     } else {
                         try {
                             String costStr = JOptionPane.showInputDialog("Enter cost:");
                             String bwStr = JOptionPane.showInputDialog("Enter bandwidth:");
                             int cost = Integer.parseInt(costStr);
                             int bandwidth = Integer.parseInt(bwStr);
                             graphModel.addEdge(selectedNode, node, cost, bandwidth);
                         } catch (NumberFormatException ex) {
                             JOptionPane.showMessageDialog(null, "Invalid number format!");
                         }
                         selectedNode = null;
                         repaint();
                     }
                 }
                 break;
             case SELECT_PATH:
                 GraphModel.GraphNode clicked = graphModel.getNodeAt(x, y);
                 if (clicked != null) {
                     if (pathStartNode == null) {
                         pathStartNode = clicked;
                     } else {
                         graphModel.computeShortestPath(pathStartNode, clicked);
                         pathStartNode = null;
                         repaint();
                     }
                 }
                 break;
         }
     }
 
     /**
      * Sets the panel to SELECT_PATH mode to start the shortest path selection process.
      */
     public void startShortestPathSelection() {
         setMode(Mode.SELECT_PATH);
         pathStartNode = null;
     }
 
     /**
      * Initiates the computation of the Minimum Spanning Tree (MST) on the graph.
      * The computed MST is stored in the graph model for visualization.
      */
     public void findMST() {
         graphModel.computeMST();
         repaint();
     }
 
     /**
      * Retrieves the current GraphModel.
      *
      * @return the GraphModel instance used by this panel
      */
     public GraphModel getGraphModel() { 
         return graphModel; 
     }
 
     /**
      * Overridden paintComponent method to draw the network graph.
      * Delegates the drawing to the GraphModel.
      *
      * @param g the Graphics object for drawing
      */
     @Override
     protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         graphModel.draw(g);
     }
 
     /**
      * Sets the current mode for user interactions.
      *
      * @param mode the new mode to set (ADD_NODE, ADD_EDGE, or SELECT_PATH)
      */
     public void setMode(Mode mode) { 
         currentMode = mode; 
     }
 }
 
 /**
  * GraphModel encapsulates the data structure for the network graph,
  * including nodes, edges, and methods for computing MST and shortest path.
  */
 class GraphModel {
     private List<GraphNode> nodes = new ArrayList<>();
     private List<GraphEdge> edges = new ArrayList<>();
     private List<GraphEdge> mstEdges = new ArrayList<>();
     private List<GraphEdge> shortestPath = new ArrayList<>();
 
     /**
      * GraphNode represents a node in the network graph.
      */
     static class GraphNode {
         public int x, y;
 
         /**
          * Constructor for GraphNode.
          *
          * @param x the x-coordinate of the node
          * @param y the y-coordinate of the node
          */
         public GraphNode(int x, int y) {
             this.x = x;
             this.y = y;
         }
         
         /**
          * Draws the node as a blue circle on the given Graphics object.
          *
          * @param g the Graphics object used for drawing
          */
         public void draw(Graphics g) {
             g.setColor(Color.BLUE);
             g.fillOval(x - 10, y - 10, 20, 20);
         }
     }
 
     /**
      * GraphEdge represents an edge connecting two nodes, with an associated cost and bandwidth.
      */
     static class GraphEdge {
         public GraphNode from, to;
         public int cost, bandwidth;
         
         /**
          * Constructor for GraphEdge.
          *
          * @param from the starting node of the edge
          * @param to the ending node of the edge
          * @param cost the cost associated with the edge
          * @param bandwidth the bandwidth of the edge
          */
         public GraphEdge(GraphNode from, GraphNode to, int cost, int bandwidth) {
             this.from = from;
             this.to = to;
             this.cost = cost;
             this.bandwidth = bandwidth;
         }
         
         /**
          * Draws the edge between two nodes.
          * If highlight is true, the edge is drawn in a highlighted color.
          *
          * @param g the Graphics object used for drawing
          * @param highlight indicates whether the edge should be highlighted
          */
         public void draw(Graphics g, boolean highlight) {
             g.setColor(highlight ? g.getColor() : Color.BLACK);
             g.drawLine(from.x, from.y, to.x, to.y);
             g.drawString(cost + "/" + bandwidth, (from.x + to.x) / 2, (from.y + to.y) / 2);
         }
     }
 
     /**
      * Adds a node to the network graph.
      *
      * @param node the GraphNode to add
      */
     public void addNode(GraphNode node) { 
         nodes.add(node); 
     }
 
     /**
      * Adds an edge between two nodes with the specified cost and bandwidth.
      *
      * @param from the starting node of the edge
      * @param to the ending node of the edge
      * @param cost the cost of the edge
      * @param bandwidth the bandwidth of the edge
      */
     public void addEdge(GraphNode from, GraphNode to, int cost, int bandwidth) {
         edges.add(new GraphEdge(from, to, cost, bandwidth));
     }
 
     /**
      * Retrieves a node at the specified (x, y) coordinates if it exists.
      *
      * @param x the x-coordinate to check
      * @param y the y-coordinate to check
      * @return the GraphNode found at the coordinates, or null if none exists
      */
     public GraphNode getNodeAt(int x, int y) {
         for (GraphNode node : nodes) {
             if (Math.hypot(node.x - x, node.y - y) < 15) {
                 return node;
             }
         }
         return null;
     }
 
     /**
      * Computes the Minimum Spanning Tree (MST) of the network graph using a greedy algorithm.
      * Uses a Disjoint Set Union (DSU) to avoid cycles.
      */
     public void computeMST() {
         mstEdges.clear();
         List<GraphEdge> sortedEdges = new ArrayList<>(edges);
         sortedEdges.sort(Comparator.comparingInt(e -> e.cost));
         DisjointSetUnion dsu = new DisjointSetUnion(nodes.size());
 
         for (GraphEdge edge : sortedEdges) {
             int u = nodes.indexOf(edge.from);
             int v = nodes.indexOf(edge.to);
             if (dsu.find(u) != dsu.find(v)) {
                 mstEdges.add(edge);
                 dsu.union(u, v);
             }
         }
     }
 
     /**
      * Computes the shortest path between two nodes using a modified Dijkstra algorithm.
      * The weight of an edge is calculated as 1000 divided by its bandwidth.
      *
      * @param start the starting GraphNode
      * @param end the target GraphNode
      */
     public void computeShortestPath(GraphNode start, GraphNode end) {
         Map<GraphNode, Integer> dist = new HashMap<>();
         Map<GraphNode, GraphNode> prev = new HashMap<>();
         PriorityQueue<GraphNode> queue = new PriorityQueue<>(Comparator.comparingInt(n -> dist.getOrDefault(n, Integer.MAX_VALUE)));
 
         for (GraphNode node : nodes) {
             dist.put(node, Integer.MAX_VALUE);
             prev.put(node, null);
         }
         dist.put(start, 0);
         queue.add(start);
 
         while (!queue.isEmpty()) {
             GraphNode u = queue.poll();
             if (u == end) break;
 
             for (GraphEdge edge : getAdjacentEdges(u)) {
                 GraphNode v = (edge.from == u) ? edge.to : edge.from;
                 int weight = 1000 / edge.bandwidth;
                 int alt = dist.get(u) + weight;
                 
                 if (alt < dist.get(v)) {
                     dist.put(v, alt);
                     prev.put(v, u);
                     if (!queue.contains(v)) {
                         queue.add(v);
                     }
                 }
             }
         }
 
         shortestPath.clear();
         GraphNode current = end;
         while (prev.get(current) != null) {
             GraphNode predecessor = prev.get(current);
             GraphEdge edge = getEdgeBetween(predecessor, current);
             if (edge != null) {
                 shortestPath.add(edge);
                 current = predecessor;
             } else {
                 break;
             }
         }
         Collections.reverse(shortestPath);
     }
 
     /**
      * Retrieves all edges adjacent to a given node.
      *
      * @param node the GraphNode for which adjacent edges are required
      * @return a list of GraphEdges adjacent to the node
      */
     private List<GraphEdge> getAdjacentEdges(GraphNode node) {
         List<GraphEdge> adjacent = new ArrayList<>();
         for (GraphEdge edge : edges) {
             if (edge.from == node || edge.to == node) {
                 adjacent.add(edge);
             }
         }
         return adjacent;
     }
 
     /**
      * Finds and returns the edge between two specified nodes.
      *
      * @param a the first GraphNode
      * @param b the second GraphNode
      * @return the GraphEdge connecting the two nodes, or null if none exists
      */
     private GraphEdge getEdgeBetween(GraphNode a, GraphNode b) {
         for (GraphEdge edge : edges) {
             if ((edge.from == a && edge.to == b) || (edge.from == b && edge.to == a)) {
                 return edge;
             }
         }
         return null;
     }
 
     /**
      * Calculates the total cost of the Minimum Spanning Tree (MST).
      *
      * @return the sum of the costs of all edges in the MST
      */
     public int calculateTotalCost() {
         return mstEdges.stream().mapToInt(e -> e.cost).sum();
     }
 
     /**
      * Draws the network graph, including all edges, highlighted MST edges, highlighted shortest path,
      * and all nodes.
      *
      * @param g the Graphics object used for drawing
      */
     public void draw(Graphics g) {
         for (GraphEdge edge : edges) edge.draw(g, false);
         g.setColor(Color.RED);
         for (GraphEdge edge : mstEdges) edge.draw(g, true);
         g.setColor(Color.GREEN);
         for (GraphEdge edge : shortestPath) edge.draw(g, true);
         for (GraphNode node : nodes) node.draw(g);
     }
 
     /**
      * DisjointSetUnion (DSU) is a helper class to perform union-find operations.
      * It is used in the MST computation to avoid cycles.
      */
     static class DisjointSetUnion {
         int[] parent;
 
         /**
          * Constructor for DisjointSetUnion.
          * Initializes the parent array such that each element is its own parent.
          *
          * @param size the number of elements in the set
          */
         public DisjointSetUnion(int size) { 
             parent = new int[size]; 
             Arrays.setAll(parent, i -> i); 
         }
 
         /**
          * Finds the representative of the set that contains element x.
          *
          * @param x the element to find
          * @return the representative of the set
          */
         public int find(int x) { 
             return parent[x] == x ? x : (parent[x] = find(parent[x])); 
         }
 
         /**
          * Unites the sets containing elements x and y.
          *
          * @param x the first element
          * @param y the second element
          */
         public void union(int x, int y) { 
             parent[find(x)] = find(y); 
         }
     }
 }
 
 /*
  * Summary:
  * In this implementation of the Network Topology Optimizer, we developed an interactive GUI
  * that allows users to construct a network graph by adding nodes and edges. We incorporated
  * algorithms for computing the Minimum Spanning Tree (MST) using a Disjoint Set Union (DSU) and for
  * calculating the shortest path based on a bandwidth-adjusted cost. The application visually displays
  * the network, the MST (highlighted in red), and the shortest path (highlighted in green). Testing has
  * confirmed that the algorithm performs as expected and provides an effective network optimization tool.
  */ 