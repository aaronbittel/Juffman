package com.juffman;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class HuffmanNode {
    private Byte value;
    private int count;

    private HuffmanNode left;
    private HuffmanNode right;

    public HuffmanNode(Byte value, int count, HuffmanNode left, HuffmanNode right) {
        this.value = value;
        this.count = count;
        this.left = left;
        this.right = right;
    }

    public HuffmanNode(Byte value, int count) {
        this(value, count, null, null);
    }

    public Byte getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HuffmanNode)) return false;
        HuffmanNode other = (HuffmanNode)o;
        return count == other.count
            && Objects.equals(value, other.getValue())
            && Objects.equals(left, other.getLeft())
            && Objects.equals(right, other.getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, count, left, right);
    }

    @Override
    public String toString() {
        return toStringIndent(0);
    }

    public String toStringIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(indent))
          .append(nodeToString())
          .append("\n");

        if (left != null) sb.append(left.toStringIndent(indent + 2));
        if (right != null) sb.append(right.toStringIndent(indent + 2));

        return sb.toString();
    }

    private String nodeToString() {
        if (value == null) return String.valueOf(count);

        byte v = value;

        if (v == 32) return "' ' (" + count + ")";
        if (v > 32 && v <= 126) return ((char) v) + " (" + count + ")";

        return v + " (" + count + ")";
    }
}

public class Juffman {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("ERROR: No input file provided");
            System.out.printf("Usage: java %s <input>%n", Juffman.class.getName());
            System.exit(1);
        }

        Path filepath = Path.of(args[0]);
        if (!Files.exists(filepath)) {
            System.err.printf("ERROR: '%s' does not exist", filepath);
            System.exit(1);
        }
        try {
            byte[] content = Files.readAllBytes(filepath);
            int[] frequencies = countFrequencies(content);
            HuffmanNode root = generateHuffmanTree(frequencies);
            System.out.println(root.toStringIndent(0));
        } catch(IOException e) {
            System.err.printf("ERROR: reading '%s': %s%n", filepath, e.getMessage());
            System.exit(1);
        }
    }

    public static int[] countFrequencies(byte[] content) {
        int[] frequencies = new int[256];
        for (byte b : content) {
            frequencies[Byte.toUnsignedInt(b)]++;
        }
        return frequencies;
    }

    public static HuffmanNode generateHuffmanTree(int[] frequencies) {
        List<HuffmanNode> nodes = new ArrayList<>(frequencies.length);
        for (int i = 0; i < frequencies.length; ++i) {
            int freq = frequencies[i];
            if (freq == 0) continue;
            nodes.add(new HuffmanNode(Byte.valueOf((byte)i), frequencies[i]));
        }
        nodes.sort((a, b) -> a.getCount() - b.getCount());

        while(nodes.size() > 1) {
            HuffmanNode first = nodes.removeFirst();
            HuffmanNode second = nodes.removeFirst();
            nodes.add(new HuffmanNode(
                null, first.getCount() + second.getCount(), first, second));
            nodes.sort((a, b) -> a.getCount() - b.getCount());
        }

        return nodes.getFirst();
    }
}
