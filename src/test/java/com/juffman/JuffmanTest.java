package com.juffman;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JuffmanTest {

    @Test
    public void countFrequeciesEmpty() {
        String empty = "";
        FrequencyTable table = FrequencyTable.fromBytes(empty.getBytes());
        assertEquals(table.totalCount(), empty.length());
        for (int i = 0; i < table.getSize(); ++i) {
            assertEquals(table.get(i), 0L);
        }
    }

    @Test
    public void countFrequeciesString() {
        String str = "AAAaaB";
        FrequencyTable table = FrequencyTable.fromBytes(str.getBytes());
        assertEquals(table.totalCount(), str.length());
        for (int i = 0; i < table.getSize(); ++i) {
            long f = table.get(i);
            if (i == 'A') assertEquals(f, 3L);
            else if (i == 'a') assertEquals(f, 2L);
            else if (i == 'B') assertEquals(f, 1L);
            else assertEquals(f, 0L);
        }
    }

    @Test
    public void countFrequeciesUnicode() {
        String unicode = "€é漢";
        byte[] bytes = unicode.getBytes();
        FrequencyTable table = FrequencyTable.fromBytes(unicode.getBytes());
        assertEquals(table.totalCount(), bytes.length);
        for (byte b : bytes) {
            int index = Byte.toUnsignedInt(b);
            assertEquals(1L, table.get(index));
        }
    }

    // source: https://opendsa-server.cs.vt.edu/ODSA/Books/CS3/html/Huffman.html
    @Test
    public void generateHuffmanTree() {
        HuffmanNode root = Juffman.generateHuffmanTree(
            FrequencyTableTest.sampleFrequencyTable());

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

    @Test
    public void generateHuffmanCodes() {
        HuffmanNode root = Juffman.generateHuffmanTree(
            FrequencyTableTest.sampleFrequencyTable());

        HuffmanCode[] letterCodes = Juffman.generateHuffmanCodesForLetters(root);
        assertEquals(letterCodes['C'], new HuffmanCode("1110"));
        assertEquals(letterCodes['D'], new HuffmanCode("101"));
        assertEquals(letterCodes['E'], new HuffmanCode("0"));
        assertEquals(letterCodes['K'], new HuffmanCode("111101"));
        assertEquals(letterCodes['L'], new HuffmanCode("110"));
        assertEquals(letterCodes['M'], new HuffmanCode("11111"));
        assertEquals(letterCodes['U'], new HuffmanCode("100"));
        assertEquals(letterCodes['Z'], new HuffmanCode("111100"));
    }

}
