import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GraphColoringGame extends JFrame {
    private final int NODE_RADIUS = 30;
    private final Color[] COLORS = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };
    private ArrayList<Integer>[] graph;
    private Point[] nodePositions;
    private int[] nodeColors; // -1 = uncolored
    private int numNodes;

    public GraphColoringGame() {
        setTitle("Graph Coloring Puzzle");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeGraph();

        GraphPanel panel = new GraphPanel();
        add(panel, BorderLayout.CENTER);

        JButton checkBtn = new JButton("Check Solution");
        add(checkBtn, BorderLayout.SOUTH);

        checkBtn.addActionListener(e -> {
            if (isValidColoring()) {
                JOptionPane.showMessageDialog(this, "Congratulations! Correct coloring!");
            } else {
                JOptionPane.showMessageDialog(this, "Some adjacent nodes have same color. Try again!");
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeGraph() {
        numNodes = 6;
        graph = new ArrayList[numNodes]; // generic array, suppress warning
        nodeColors = new int[numNodes];
        Arrays.fill(nodeColors, -1); // uncolored

        nodePositions = new Point[numNodes];
        nodePositions[0] = new Point(100, 100);
        nodePositions[1] = new Point(300, 100);
        nodePositions[2] = new Point(500, 100);
        nodePositions[3] = new Point(200, 300);
        nodePositions[4] = new Point(400, 300);
        nodePositions[5] = new Point(300, 500);

        for (int i = 0; i < numNodes; i++)
            graph[i] = new ArrayList<>();

        // Define edges
        addEdge(0, 1);
        addEdge(1, 2);
        addEdge(0, 3);
        addEdge(1, 3);
        addEdge(1, 4);
        addEdge(2, 4);
        addEdge(3, 4);
        addEdge(3, 5);
        addEdge(4, 5);
    }

    private void addEdge(int u, int v) {
        graph[u].add(v);
        graph[v].add(u);
    }

    private boolean isValidColoring() {
        for (int u = 0; u < numNodes; u++) {
            for (int v : graph[u]) {
                if (nodeColors[u] != -1 && nodeColors[u] == nodeColors[v])
                    return false;
            }
        }
        return true;
    }

    private class GraphPanel extends JPanel {
        public GraphPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (int i = 0; i < numNodes; i++) {
                        Point p = nodePositions[i];
                        double dist = p.distance(e.getX(), e.getY());
                        if (dist <= NODE_RADIUS) {
                            nodeColors[i] = (nodeColors[i] + 1) % COLORS.length;
                            repaint();
                            break;
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            // Draw edges
            for (int u = 0; u < numNodes; u++) {
                for (int v : graph[u]) {
                    if (u < v) {
                        Point p1 = nodePositions[u];
                        Point p2 = nodePositions[v];
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            // Draw nodes
            for (int i = 0; i < numNodes; i++) {
                Point p = nodePositions[i];
                if (nodeColors[i] == -1)
                    g.setColor(Color.LIGHT_GRAY);
                else
                    g.setColor(COLORS[nodeColors[i]]);
                g.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
                g.setColor(Color.BLACK);
                g.drawOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
                g.drawString("N" + i, p.x - 5, p.y + 5);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphColoringGame game = new GraphColoringGame();
            game.setVisible(true);
        });
    }
}
