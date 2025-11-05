import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


import javax.swing.Timer;
import java.util.List;

public class LISVisualizer extends JFrame implements ActionListener {
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton startButton, randomButton;
    private Timer timer; 
    private int i, j;
    private int[] arr, dp, prev;
    private StringBuilder steps;

    public LISVisualizer() {
        setTitle(" LIS Visualizer - Dynamic Programming in Action");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("LIS Dynamic Programming Visualizer", JLabel.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 20));
        title.setForeground(new Color(30, 60, 150));

        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 16));
        inputField.setToolTipText("Enter numbers separated by spaces (e.g., 3 10 2 1 20)");

        startButton = new JButton("▶ Start Visualization");
        startButton.addActionListener(this);
        randomButton = new JButton(" Random Numbers");
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

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        getContentPane().setBackground(Color.white);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == randomButton) {
            Random r = new Random();
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < 8; k++)
                sb.append(r.nextInt(40)).append(" ");
            inputField.setText(sb.toString().trim());
        } else if (e.getSource() == startButton) {
            try {
                String[] parts = inputField.getText().trim().split("\\s+");
                arr = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
                startVisualization();
            } catch (Exception ex) {
                outputArea.setText(" Please enter valid numbers!");
            }
        }
    }

    private void startVisualization() {
        dp = new int[arr.length];
        prev = new int[arr.length];
        Arrays.fill(dp, 1);
        Arrays.fill(prev, -1);
        steps = new StringBuilder();

        outputArea.setText("Starting LIS Visualization...\n\n");
        i = 1;
        j = 0;

        if (timer != null)
            timer.stop();
        timer = new Timer(700, e -> visualizeStep());
        timer.start();
    }

    private void visualizeStep() {
        if (i < arr.length) {
            if (j < i) {
                steps.append(String.format("Comparing arr[%d]=%d with arr[%d]=%d\n", i, arr[i], j, arr[j]));
                if (arr[i] > arr[j] && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                    prev[i] = j;
                    steps.append("Updated dp[" + i + "] = " + dp[i] + "\n");
                } else {
                    steps.append(" No update\n");
                }
                j++;
            } else {
                j = 0;
                i++;
                steps.append("\n");
            }
        } else {
            timer.stop();
            showResult();
            return;
        }

        outputArea.setText("");
        outputArea.append("Array: " + Arrays.toString(arr) + "\n\n");
        outputArea.append("DP Table: " + Arrays.toString(dp) + "\n\n");
        outputArea.append("Steps:\n" + steps.toString());
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

        steps.append("\n Final LIS: " + lis + " (Length = " + lis.size() + ")\n");
        outputArea.setText("Array: " + Arrays.toString(arr) + "\n\n");
        outputArea.append("DP Table: " + Arrays.toString(dp) + "\n\n");
        outputArea.append("Steps:\n" + steps.toString());
        outputArea.append("\n Visualization Complete!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LISVisualizer::new);
    }
}
