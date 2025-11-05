import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import java.util.Random;

public class MazeSolverGUI extends JFrame {
    private int rows = 20, cols = 20;
    private int[][] maze;
    private boolean[][] visited;
    private final int cellSize = 25;
    private int startRow = 0, startCol = 0;
    private int endRow = rows - 1, endCol = cols - 1;
    private MazePanel mazePanel;
    private javax.swing.Timer timer; // explicitly Swing Timer
    private Stack<int[]> stack;

    public MazeSolverGUI() {
        setTitle("Graph Maze Solver");
        setSize(cols * cellSize + 50, rows * cellSize + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        JButton solveBtn = new JButton("Solve Maze");
        JButton shuffleBtn = new JButton("Generate Maze");

        JPanel controlPanel = new JPanel();
        controlPanel.add(solveBtn);
        controlPanel.add(shuffleBtn);
        add(controlPanel, BorderLayout.SOUTH);

        generateMaze();

        solveBtn.addActionListener(e -> solveMaze());
        shuffleBtn.addActionListener(e -> generateMaze());
    }

    private void generateMaze() {
        maze = new int[rows][cols];
        visited = new boolean[rows][cols];
        Random rand = new Random();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                maze[r][c] = (rand.nextDouble() < 0.3) ? 1 : 0; // 30% walls
        maze[startRow][startCol] = 0;
        maze[endRow][endCol] = 0;
        mazePanel.repaint();
    }

    private void solveMaze() {
        stack = new Stack<>();
        stack.push(new int[] { startRow, startCol });
        visited = new boolean[rows][cols];

        timer = new javax.swing.Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!stack.isEmpty()) {
                    int[] cell = stack.pop();
                    int r = cell[0], c = cell[1];
                    if (r < 0 || r >= rows || c < 0 || c >= cols || visited[r][c] || maze[r][c] == 1)
                        return;
                    visited[r][c] = true;
                    if (r == endRow && c == endCol) { // reached end
                        timer.stop();
                        JOptionPane.showMessageDialog(null, "Maze Solved!");
                        return;
                    }
                    // Push neighbors (DFS)
                    stack.push(new int[] { r + 1, c });
                    stack.push(new int[] { r - 1, c });
                    stack.push(new int[] { r, c + 1 });
                    stack.push(new int[] { r, c - 1 });
                } else {
                    timer.stop();
                    JOptionPane.showMessageDialog(null, "No solution found!");
                }
                mazePanel.repaint();
            }
        });
        timer.start();
    }

    private class MazePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (maze[r][c] == 1)
                        g.setColor(Color.BLACK);
                    else if (visited[r][c])
                        g.setColor(Color.RED);
                    else
                        g.setColor(Color.WHITE);
                    g.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                    g.setColor(Color.GRAY);
                    g.drawRect(c * cellSize, r * cellSize, cellSize, cellSize);
                }
            }
            // Start & End
            g.setColor(Color.GREEN);
            g.fillRect(startCol * cellSize, startRow * cellSize, cellSize, cellSize);
            g.setColor(Color.BLUE);
            g.fillRect(endCol * cellSize, endRow * cellSize, cellSize, cellSize);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeSolverGUI gui = new MazeSolverGUI();
            gui.setVisible(true);
        });
    }
}
