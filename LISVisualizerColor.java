import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import java.util.List;

public class LISVisualizerColor extends JFrame implements ActionListener {
    private JTextField inputField;
    private JTextArea stepsArea;
    private JButton startButton, randomButton;
    private JPanel arrayPanel;
    private Timer timer;

    private int[] arr, dp, prev;
    private int i = 1, j = 0;
    private StringBuilder stepLog = new StringBuilder();

    private final int BOX_SIZE = 60;
    private final int DELAY = 800;

    public LISVisualizerColor() {
        setTitle(" LIS Visualizer (Dynamic Programming Animation)");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Longest Increasing Subsequence Visualizer", JLabel.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 20));
        title.setForeground(new Color(40, 60, 160));

        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 16));
        inputField.setToolTipText("Enter numbers (e.g., 3 10 2 1 20)");

        startButton = new JButton("▶ Start Visualization");
        startButton.addActionListener(this);

        randomButton = new JButton(" Random");
        randomButton.addActionListener(this);

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.add(title);
        topPanel.add(inputField);
        topPanel.add(new JPanel() {
            {
                add(startButton);
                add(randomButton);
            }
        });

        arrayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (arr == null)
                    return;
                for (int k = 0; k < arr.length; k++) {
                    Color color = Color.LIGHT_GRAY;
                    if (i < arr.length && k == i)
                        color = Color.BLUE; 
                    if (i < arr.length && j < i && k == j)
                        color = Color.ORANGE; // comparing j
                    g.setColor(color);
                    g.fillRect(50 + k * (BOX_SIZE + 10), 40, BOX_SIZE, BOX_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(50 + k * (BOX_SIZE + 10), 40, BOX_SIZE, BOX_SIZE);
                    g.drawString(String.valueOf(arr[k]), 70 + k * (BOX_SIZE + 10), 70);
                    g.drawString("dp=" + dp[k], 65 + k * (BOX_SIZE + 10), 95);
                }
            }
        };
        arrayPanel.setPreferredSize(new Dimension(800, 150));
        arrayPanel.setBackground(Color.WHITE);

        stepsArea = new JTextArea();
        stepsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        stepsArea.setEditable(false);
        stepsArea.setBorder(BorderFactory.createTitledBorder(" DP Steps"));

        add(topPanel, BorderLayout.NORTH);
        add(arrayPanel, BorderLayout.CENTER);
        add(new JScrollPane(stepsArea), BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == randomButton) {
            Random r = new Random();
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < 7; k++)
                sb.append(r.nextInt(40)).append(" ");
            inputField.setText(sb.toString().trim());
        } else if (e.getSource() == startButton) {
            try {
                String[] parts = inputField.getText().trim().split("\\s+");
                arr = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
                startVisualization();
            } catch (Exception ex) {
                stepsArea.setText(" Please enter valid numbers!");
            }
        }
    }

    private void startVisualization() {
        if (timer != null)
            timer.stop();
        dp = new int[arr.length];
        prev = new int[arr.length];
        Arrays.fill(dp, 1);
        Arrays.fill(prev, -1);
        i = 1;
        j = 0;
        stepLog.setLength(0);

        stepsArea.setText("Starting LIS Visualization...\n");
        repaint();

        timer = new Timer(DELAY, e -> visualizeStep());
        timer.start();
    }

    private void visualizeStep() {
        if (i < arr.length) {
            if (j < i) {
                stepLog.append(String.format("Comparing arr[%d]=%d with arr[%d]=%d\n", i, arr[i], j, arr[j]));
                if (arr[i] > arr[j] && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    prev[i] = j;
                    stepLog.append(" Updated dp[" + i + "] = " + dp[i] + "\n");
                } else {
                    stepLog.append(" No update\n");
                }
                j++;
            } else {
                j = 0;
                i++;
                stepLog.append("\n");
            }
        } else {
            timer.stop();
            showResult();
        }
        stepsArea.setText(stepLog.toString());
        arrayPanel.repaint();
    }

    private void showResult() {
        int maxIndex = 0;
        for (int k = 1; k < arr.length; k++)
            if (dp[k] > dp[maxIndex])
                maxIndex = k;

        List<Integer> lis = new ArrayList<>();
        for (int k = maxIndex; k >= 0; k = prev[k]) {
            lis.add(arr[k]);
            if (prev[k] == -1)
                break;
        }
        Collections.reverse(lis);

        stepLog.append("\n Final LIS: " + lis + " (Length = " + lis.size() + ")\n🎉 Visualization Complete!");
        stepsArea.setText(stepLog.toString());
        arrayPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LISVisualizerColor::new);
    }
}
