import java.io.*;
import java.util.*;

class HuffmanNode implements Comparable<HuffmanNode> {
    char ch;
    int freq;
    HuffmanNode left, right;

    public HuffmanNode(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
    }

    public int compareTo(HuffmanNode node) {
        return this.freq - node.freq;
    }
}

public class FileCompression {

   
    public static HuffmanNode buildTree(Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode newNode = new HuffmanNode('\0', left.freq + right.freq);
            newNode.left = left;
            newNode.right = right;
            pq.add(newNode);
        }

        return pq.poll();
    }

   
    public static void generateCodes(HuffmanNode root, String code, Map<Character, String> huffmanCode) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            huffmanCode.put(root.ch, code);
        }
        generateCodes(root.left, code + "0", huffmanCode);
        generateCodes(root.right, code + "1", huffmanCode);
    }

    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String text = new String(Files.readAllBytes(new File(inputFile).toPath()));

        
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        
        HuffmanNode root = buildTree(freqMap);

        Map<Character, String> huffmanCode = new HashMap<>();
        generateCodes(root, "", huffmanCode);

        System.out.println("Huffman Codes: " + huffmanCode);

       
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(huffmanCode.get(c));
        }

        System.out.println("Encoded text: " + sb);

       
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("encoded.txt"))) {
            writer.write(sb.toString());
        }
    }
}
