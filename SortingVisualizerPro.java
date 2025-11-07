import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SortingVisualizerPro extends JFrame {
    private Bar[] bars;
    private int currentI = -1, currentJ = -1;
    private int delay = 50;
    private String algorithm = "Bubble";
    private SortingPanel sortingPanel;
    private Timer timer;
    private int i = 0, j = 0;
    private int arraySize = 80;
    private int sortedUpto = -1; 

    public SortingVisualizerPro() {
        setTitle("Sorting Visualizer");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        bars = new Bar[arraySize];
        sortingPanel = new SortingPanel();
        add(sortingPanel, BorderLayout.CENTER);

        shuffleArray();

       
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JComboBox<String> algoDropdown = new JComboBox<>(new String[] {
                "Bubble", "Selection", "Insertion", "Quick", "Merge"
        });

        JTextField sizeField = new JTextField(String.valueOf(arraySize), 5);

        JButton startBtn = new JButton("Start");
        JButton shuffleBtn = new JButton("Shuffle");

        JSlider speedSlider = new JSlider(10, 200, delay);
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> delay = speedSlider.getValue());

        startBtn.addActionListener(e -> {
            algorithm = (String) algoDropdown.getSelectedItem();
            try {
                arraySize = Integer.parseInt(sizeField.getText());
                bars = new Bar[arraySize];
            } catch (NumberFormatException ex) {
                arraySize = 80;
                bars = new Bar[arraySize];
                sizeField.setText("80");
            }
            shuffleArray();
            startSorting(algorithm);
        });

        shuffleBtn.addActionListener(e -> shuffleArray());

        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algoDropdown);
        controlPanel.add(new JLabel("Array Size:"));
        controlPanel.add(sizeField);
        controlPanel.add(startBtn);
        controlPanel.add(shuffleBtn);
        controlPanel.add(new JLabel("Speed:"));
        controlPanel.add(speedSlider);

        add(controlPanel, BorderLayout.SOUTH);

       
        Timer animationTimer = new Timer(15, e -> {
            for (Bar bar : bars)
                bar.update();
            sortingPanel.repaint();
        });
        animationTimer.start();
    }

    private void shuffleArray() {
        Random rand = new Random();
        for (int k = 0; k < bars.length; k++) {
            bars[k] = new Bar(rand.nextInt(400) + 50);
        }
        currentI = currentJ = -1;
        i = j = 0;
        minIdx = -1;
        insertionInit = false;
        quickInit = false;
        mergeInit = false;
        sortedUpto = -1;
        if (timer != null)
            timer.stop();
        sortingPanel.repaint();
    }

    private void startSorting(String algo) {
        i = 0;
        j = 0;
        sortedUpto = -1;
        if (timer != null && timer.isRunning())
            timer.stop();
        switch (algo) {
            case "Bubble" -> bubbleSortTimer();
            case "Selection" -> selectionSortTimer();
            case "Insertion" -> insertionSortTimer();
            case "Quick" -> quickSortTimer();
            case "Merge" -> mergeSortTimer();
        }
    }

    
    private void bubbleSortTimer() {
        timer = new Timer(delay, e -> {
            if (i < bars.length - 1) {
                if (j < bars.length - i - 1) {
                    currentJ = j;
                    if (bars[j].targetHeight > bars[j + 1].targetHeight) {
                        swapBars(j, j + 1);
                    }
                    j++;
                } else {
                    j = 0;
                    i++;
                    sortedUpto = bars.length - i;
                }
            } else {
                currentJ = -1;
                sortedUpto = bars.length;
                timer.stop();
            }
        });
        timer.start();
    }

    private void swapBars(int a, int b) {
        int temp = bars[a].targetHeight;
        bars[a].setTarget(bars[b].targetHeight);
        bars[b].setTarget(temp);
    }

    
    private int minIdx = -1;

    private void selectionSortTimer() {
        i = 0;
        j = i + 1;
        minIdx = i;
        timer = new Timer(delay, e -> {
            if (i < bars.length - 1) {
                if (j < bars.length) {
                    currentJ = j;
                    if (bars[j].targetHeight < bars[minIdx].targetHeight)
                        minIdx = j;
                    j++;
                } else {
                    swapBars(i, minIdx);
                    i++;
                    j = i + 1;
                    minIdx = i;
                    sortedUpto = i;
                }
            } else {
                currentJ = -1;
                sortedUpto = bars.length;
                timer.stop();
            }
        });
        timer.start();
    }

   
    private int key, k;
    private boolean insertionInit = false;

    private void insertionSortTimer() {
        i = 1;
        timer = new Timer(delay, e -> {
            if (!insertionInit && i < bars.length) {
                key = bars[i].targetHeight;
                k = i - 1;
                insertionInit = true;
            }
            if (i < bars.length) {
                if (k >= 0 && bars[k].targetHeight > key) {
                    bars[k + 1].setTarget(bars[k].targetHeight);
                    k--;
                } else {
                    bars[k + 1].setTarget(key);
                    i++;
                    insertionInit = false;
                    sortedUpto = i;
                }
                currentJ = k + 1;
            } else {
                currentJ = -1;
                sortedUpto = bars.length;
                timer.stop();
            }
        });
        timer.start();
    }

   
    private int[] stackLow, stackHigh;
    private int top = -1;
    private boolean quickInit = false;

    private void quickSortTimer() {
        if (!quickInit) {
            stackLow = new int[bars.length];
            stackHigh = new int[bars.length];
            top = 0;
            stackLow[top] = 0;
            stackHigh[top] = bars.length - 1;
            quickInit = true;
        }
        timer = new Timer(delay, e -> {
            if (top < 0) {
                currentJ = -1;
                sortedUpto = bars.length;
                timer.stop();
                return;
            }
            int low = stackLow[top];
            int high = stackHigh[top--];
            int pi = partition(low, high);
            if (pi - 1 > low) {
                stackLow[++top] = low;
                stackHigh[top] = pi - 1;
            }
            if (pi + 1 < high) {
                stackLow[++top] = pi + 1;
                stackHigh[top] = high;
            }
        });
        timer.start();
    }

    private int partition(int low, int high) {
        int pivot = bars[high].targetHeight;
        int i = low - 1;
        for (int j = low; j < high; j++) {
            currentJ = j;
            if (bars[j].targetHeight < pivot) {
                i++;
                swapBars(i, j);
            }
        }
        swapBars(i + 1, high);
        return i + 1;
    }

    
    private boolean mergeInit = false;

    private void mergeSortTimer() {
        new Thread(() -> {
            try {
                mergeSort(0, bars.length - 1);
                sortedUpto = bars.length;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void mergeSort(int l, int r) throws InterruptedException {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(l, m);
            mergeSort(m + 1, r);
            merge(l, m, r);
        }
    }

    private void merge(int l, int m, int r) throws InterruptedException {
        int n1 = m - l + 1;
        int n2 = r - m;
        int[] L = new int[n1];
        int[] R = new int[n2];
        for (int i = 0; i < n1; i++)
            L[i] = bars[l + i].targetHeight;
        for (int j = 0; j < n2; j++)
            R[j] = bars[m + 1 + j].targetHeight;
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j])
                bars[k++].setTarget(L[i++]);
            else
                bars[k++].setTarget(R[j++]);
            currentJ = k;
            Thread.sleep(delay);
        }
        while (i < n1)
            bars[k++].setTarget(L[i++]);
        while (j < n2)
            bars[k++].setTarget(R[j++]);
    }

    private class SortingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth() / bars.length;
            for (int k = 0; k < bars.length; k++) {
                if (k == currentJ || k == currentJ + 1)
                    g.setColor(Color.RED);
                else if (k < sortedUpto)
                    g.setColor(Color.GREEN);
                else {
                    float ratio = (float) k / bars.length;
                    g.setColor(new Color(0f, 0.5f + 0.5f * ratio, 1f - 0.5f * ratio));
                }
                g.fillRect(k * width, getHeight() - (int) bars[k].currentHeight, width - 2,
                        (int) bars[k].currentHeight);
            }
        }
    }

    private class Bar {
        int targetHeight;
        float currentHeight;

        Bar(int height) {
            this.targetHeight = height;
            this.currentHeight = height;
        }

        void update() {
            currentHeight += (targetHeight - currentHeight) * 0.2;
        }

        void setTarget(int height) {
            targetHeight = height;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SortingVisualizerPro gui = new SortingVisualizerPro();
            gui.setVisible(true);
        });
    }
}
