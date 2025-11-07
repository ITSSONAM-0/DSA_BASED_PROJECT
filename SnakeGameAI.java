import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SnakeGameAI extends JPanel implements ActionListener {
    private final int WIDTH = 600, HEIGHT = 600;
    private final int UNIT_SIZE = 20;
    private final int DELAY = 100;

    private final LinkedList<Point> playerSnake = new LinkedList<>();
    private final LinkedList<Point> aiSnake = new LinkedList<>();
    private Point food;

    private char direction = 'R'; 
    private boolean running = false;
    private javax.swing.Timer timer; 
    private Random random;
    private int score = 0;

    public SnakeGameAI() {
        random = new Random();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        playerSnake.clear();
        aiSnake.clear();
        playerSnake.add(new Point(UNIT_SIZE * 5, UNIT_SIZE * 5));
        aiSnake.add(new Point(UNIT_SIZE * 25, UNIT_SIZE * 25));
        spawnFood();
        running = true;
        timer = new javax.swing.Timer(DELAY, this); 
        timer.start();
    }

    private void spawnFood() {
        int x = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        food = new Point(x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

            
            g.setColor(Color.GREEN);
            for (Point p : playerSnake) {
                g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            }

           
            g.setColor(Color.CYAN);
            for (Point p : aiSnake) {
                g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            }

            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + score, 10, 20);

        } else {
            gameOver(g);
        }
    }

    private void move() {
        
        Point head = new Point(playerSnake.getFirst());
        switch (direction) {
            case 'U':
                head.y -= UNIT_SIZE;
                break;
            case 'D':
                head.y += UNIT_SIZE;
                break;
            case 'L':
                head.x -= UNIT_SIZE;
                break;
            case 'R':
                head.x += UNIT_SIZE;
                break;
        }
        playerSnake.addFirst(head);

        if (head.equals(food)) {
            score++;
            spawnFood();
        } else {
            playerSnake.removeLast();
        }

        
        Point aiHead = aiSnake.getFirst();
        Point nextMove = getNextMoveAI(aiHead, food);
        if (nextMove != null) {
            aiSnake.addFirst(nextMove);
            aiSnake.removeLast();
        }
    }

   
    private Point getNextMoveAI(Point start, Point target) {
        int rows = HEIGHT / UNIT_SIZE;
        int cols = WIDTH / UNIT_SIZE;
        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];

        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        visited[start.y / UNIT_SIZE][start.x / UNIT_SIZE] = true;

        int[] dx = { 0, 0, -1, 1 };
        int[] dy = { -1, 1, 0, 0 };

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.equals(target))
                break;

            for (int i = 0; i < 4; i++) {
                int nx = current.x + dx[i] * UNIT_SIZE;
                int ny = current.y + dy[i] * UNIT_SIZE;
                Point next = new Point(nx, ny);

                if (nx < 0 || ny < 0 || nx >= WIDTH || ny >= HEIGHT)
                    continue;
                if (playerSnake.contains(next) || aiSnake.contains(next))
                    continue;
                if (!visited[ny / UNIT_SIZE][nx / UNIT_SIZE]) {
                    queue.add(next);
                    visited[ny / UNIT_SIZE][nx / UNIT_SIZE] = true;
                    parent[ny / UNIT_SIZE][nx / UNIT_SIZE] = current;
                }
            }
        }

       
        Point step = target;
        if (parent[step.y / UNIT_SIZE][step.x / UNIT_SIZE] == null)
            return null; // no path
        while (!parent[step.y / UNIT_SIZE][step.x / UNIT_SIZE].equals(start)) {
            step = parent[step.y / UNIT_SIZE][step.x / UNIT_SIZE];
        }
        return step;
    }

    private void checkCollisions() {
        Point head = playerSnake.getFirst();
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT)
            running = false;
        for (int i = 1; i < playerSnake.size(); i++)
            if (head.equals(playerSnake.get(i)))
                running = false;

        
        Point aiHead = aiSnake.getFirst();
        if (aiHead.x < 0 || aiHead.x >= WIDTH || aiHead.y < 0 || aiHead.y >= HEIGHT)
            running = false;
        for (int i = 1; i < aiSnake.size(); i++)
            if (aiHead.equals(aiSnake.get(i)))
                running = false;

        if (!running)
            timer.stop();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Game Over", WIDTH / 2 - 120, HEIGHT / 2);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, WIDTH / 2 - 40, HEIGHT / 2 + 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R')
                        direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L')
                        direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D')
                        direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U')
                        direction = 'D';
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game with AI");
        SnakeGameAI gamePanel = new SnakeGameAI();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
