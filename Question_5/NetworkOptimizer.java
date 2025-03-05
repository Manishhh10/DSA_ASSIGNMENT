package Question_5;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class NetworkOptimizer extends JFrame {
    private GraphPanel graphPanel;
    private JLabel costLabel, latencyLabel;

    public NetworkOptimizer() {
        setTitle("Network Topology Optimizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

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

    private void updateStats() {
        int totalCost = graphPanel.getGraphModel().calculateTotalCost();
        costLabel.setText("Total Cost: " + totalCost);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkOptimizer().setVisible(true));
    }
}

class GraphPanel extends JPanel {
    enum Mode { ADD_NODE, ADD_EDGE, SELECT_PATH }
    private Mode currentMode = Mode.ADD_NODE;
    private GraphModel graphModel = new GraphModel();
    private GraphModel.GraphNode selectedNode;
    private GraphModel.GraphNode pathStartNode;

    public GraphPanel() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

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

    public void startShortestPathSelection() {
        setMode(Mode.SELECT_PATH);
        pathStartNode = null;
    }

    public void findMST() {
        graphModel.computeMST();
        repaint();
    }

    public GraphModel getGraphModel() { return graphModel; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        graphModel.draw(g);
    }

    public void setMode(Mode mode) { currentMode = mode; }
}

class GraphModel {
    private List<GraphNode> nodes = new ArrayList<>();
    private List<GraphEdge> edges = new ArrayList<>();
    private List<GraphEdge> mstEdges = new ArrayList<>();
    private List<GraphEdge> shortestPath = new ArrayList<>();

    static class GraphNode {
        public int x, y;
        public GraphNode(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void draw(Graphics g) {
            g.setColor(Color.BLUE);
            g.fillOval(x-10, y-10, 20, 20);
        }
    }

    static class GraphEdge {
        public GraphNode from, to;
        public int cost, bandwidth;
        
        public GraphEdge(GraphNode from, GraphNode to, int cost, int bandwidth) {
            this.from = from;
            this.to = to;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }
        
        public void draw(Graphics g, boolean highlight) {
            g.setColor(highlight ? g.getColor() : Color.BLACK);
            g.drawLine(from.x, from.y, to.x, to.y);
            g.drawString(cost + "/" + bandwidth, (from.x + to.x)/2, (from.y + to.y)/2);
        }
    }

    public void addNode(GraphNode node) { 
        nodes.add(node); 
    }

    public void addEdge(GraphNode from, GraphNode to, int cost, int bandwidth) {
        edges.add(new GraphEdge(from, to, cost, bandwidth));
    }

    public GraphNode getNodeAt(int x, int y) {
        for (GraphNode node : nodes) {
            if (Math.hypot(node.x - x, node.y - y) < 15) {
                return node;
            }
        }
        return null;
    }

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

    public void computeShortestPath(GraphNode start, GraphNode end) {
        Map<GraphNode, Integer> dist = new HashMap<>();
        Map<GraphNode, GraphNode> prev = new HashMap<>();
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(
            Comparator.comparingInt(n -> dist.getOrDefault(n, Integer.MAX_VALUE))
        );

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

    private List<GraphEdge> getAdjacentEdges(GraphNode node) {
        List<GraphEdge> adjacent = new ArrayList<>();
        for (GraphEdge edge : edges) {
            if (edge.from == node || edge.to == node) {
                adjacent.add(edge);
            }
        }
        return adjacent;
    }

    private GraphEdge getEdgeBetween(GraphNode a, GraphNode b) {
        for (GraphEdge edge : edges) {
            if ((edge.from == a && edge.to == b) || (edge.from == b && edge.to == a)) {
                return edge;
            }
        }
        return null;
    }

    public int calculateTotalCost() {
        return mstEdges.stream().mapToInt(e -> e.cost).sum();
    }

    public void draw(Graphics g) {
        for (GraphEdge edge : edges) edge.draw(g, false);
        g.setColor(Color.RED);
        for (GraphEdge edge : mstEdges) edge.draw(g, true);
        g.setColor(Color.GREEN);
        for (GraphEdge edge : shortestPath) edge.draw(g, true);
        for (GraphNode node : nodes) node.draw(g);
    }

    static class DisjointSetUnion {
        int[] parent;
        public DisjointSetUnion(int size) { 
            parent = new int[size]; 
            Arrays.setAll(parent, i -> i); 
        }
        public int find(int x) { 
            return parent[x] == x ? x : (parent[x] = find(parent[x])); 
        }
        public void union(int x, int y) { 
            parent[find(x)] = find(y); 
        }
    }
}