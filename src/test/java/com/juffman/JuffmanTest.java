package com.juffman;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JuffmanTest {

    @Test
    public void countFrequeciesEmpty() {
        String empty = "";
        int[] frequencies = Juffman.countFrequencies(empty.getBytes());
        assertEquals(getTotalCount(frequencies), empty.length());
        for (int f : frequencies) {
            assertEquals(f, 0);
        }
    }

    @Test
    public void countFrequeciesString() {
        String str = "AAAaaB";
        int[] frequencies = Juffman.countFrequencies(str.getBytes());
        assertEquals(getTotalCount(frequencies), str.length());
        for (int i = 0; i < frequencies.length; ++i) {
            int f = frequencies[i];
            if (i == 'A') assertEquals(f, 3);
            else if (i == 'a') assertEquals(f, 2);
            else if (i == 'B') assertEquals(f, 1);
            else assertEquals(f, 0);
        }
    }

    @Test
    public void countFrequeciesUnicode() {
        String unicode = "€é漢";
        byte[] bytes = unicode.getBytes();
        int[] frequencies = Juffman.countFrequencies(unicode.getBytes());
        assertEquals(getTotalCount(frequencies), bytes.length);
        for (byte b : bytes) {
            int unsigned = Byte.toUnsignedInt(b);
            assertEquals(1, frequencies[unsigned]);
        }
    }

    // source: https://opendsa-server.cs.vt.edu/ODSA/Books/CS3/html/Huffman.html
    @Test
    public void generateHuffmanTree() {
        int[] frequencies = new int[256];
        frequencies['C'] = 32;
        frequencies['D'] = 42;
        frequencies['E'] = 120;
        frequencies['K'] = 7;
        frequencies['L'] = 42;
        frequencies['M'] = 24;
        frequencies['U'] = 37;
        frequencies['Z'] = 2;

        HuffmanNode root = Juffman.generateHuffmanTree(frequencies);

        HuffmanNode cNode = new HuffmanNode(Byte.valueOf((byte)'C'), 32);
        HuffmanNode dNode = new HuffmanNode(Byte.valueOf((byte)'D'), 42);
        HuffmanNode eNode = new HuffmanNode(Byte.valueOf((byte)'E'), 120);
        HuffmanNode kNode = new HuffmanNode(Byte.valueOf((byte)'K'), 7);
        HuffmanNode lNode = new HuffmanNode(Byte.valueOf((byte)'L'), 42);
        HuffmanNode mNode = new HuffmanNode(Byte.valueOf((byte)'M'), 24);
        HuffmanNode uNode = new HuffmanNode(Byte.valueOf((byte)'U'), 37);
        HuffmanNode zNode = new HuffmanNode(Byte.valueOf((byte)'Z'), 2);

        HuffmanNode expectedRoot = new HuffmanNode(
            null, 306, eNode, new HuffmanNode(
                null,
                186,
                new HuffmanNode(null, 79, uNode, dNode),
                new HuffmanNode(
                    null,
                    107,
                    lNode,
                    new HuffmanNode(
                        null,
                        65,
                        cNode,
                        new HuffmanNode(
                            null,
                            33,
                            new HuffmanNode(null, 9, zNode, kNode),
                            mNode
                        )
                    )
                )
            )
        );

        assertEquals(root, expectedRoot);
    }

    private int getTotalCount(int[] frequencies) {
        int sum = 0;
        for (int f : frequencies) {
            sum += f;
        }
        return sum;
    }
}
