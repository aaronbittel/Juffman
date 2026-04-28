package com.juffman;

public class HuffmanCodeBuilder {

    private HuffmanCodeBuilder() { }

    public static HuffmanCode[] build(HuffmanNode root) {
        HuffmanCode[] table = new HuffmanCode[256];
        buildRecursive(root, 0, 0, table);
        return table;
    }

    private static void buildRecursive(
        HuffmanNode node,
        int bits,
        int size,
        HuffmanCode[] table
    ) {
        if (node == null) return;

        if (node.isLetterNode()) {
            table[node.getIndex()] = new HuffmanCode(bits, size);
            return;
        }

        if (node.hasLeft()) {
            buildRecursive(node.left(), bits << 1, size + 1, table);
        }

        if (node.hasRight()) {
            buildRecursive(node.right(), (bits << 1) | 1, size + 1, table);
        }
    }

}
