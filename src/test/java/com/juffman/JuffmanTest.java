package com.juffman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JuffmanTest {

    @Test
    public void countFrequenciesEmpty() {
        String empty = "";
        FrequencyTable table = FrequencyTable.fromBytes(empty.getBytes());
        assertEquals(0, table.totalCount());
        for (int i = 0; i < table.getSize(); ++i) {
            assertEquals(0L, table.get(i));
        }
    }

    @Test
    public void countFrequenciesString() {
        String str = "AAAaaB";
        FrequencyTable table = FrequencyTable.fromBytes(str.getBytes());
        assertEquals(table.totalCount(), str.length());
        for (int i = 0; i < table.getSize(); ++i) {
            long f = table.get(i);
            if (i == 'A') assertEquals(3L, f);
            else if (i == 'a') assertEquals(2L, f);
            else if (i == 'B') assertEquals(1L, f);
            else assertEquals(0L, f);
        }
    }

    @Test
    public void countFrequenciesUnicode() {
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
        HuffmanNode root = HuffmanTreeBuilder.build(
            FrequencyTableTest.sampleFrequencyTable());

        HuffmanNode cNode = new HuffmanNode((byte) 'C', 32);
        HuffmanNode dNode = new HuffmanNode((byte) 'D', 42);
        HuffmanNode eNode = new HuffmanNode((byte)'E', 120);
        HuffmanNode kNode = new HuffmanNode((byte)'K', 7);
        HuffmanNode lNode = new HuffmanNode((byte)'L', 42);
        HuffmanNode mNode = new HuffmanNode((byte)'M', 24);
        HuffmanNode uNode = new HuffmanNode((byte)'U', 37);
        HuffmanNode zNode = new HuffmanNode((byte)'Z', 2);

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
        HuffmanNode root = HuffmanTreeBuilder.build(
            FrequencyTableTest.sampleFrequencyTable());

        HuffmanCode[] letterCodes = HuffmanCodeBuilder.build(root);
        assertEquals(letterCodes['C'], HuffmanCodeFromString("1110"));
        assertEquals(letterCodes['D'], HuffmanCodeFromString("101"));
        assertEquals(letterCodes['E'], HuffmanCodeFromString("0"));
        assertEquals(letterCodes['K'], HuffmanCodeFromString("111101"));
        assertEquals(letterCodes['L'], HuffmanCodeFromString("110"));
        assertEquals(letterCodes['M'], HuffmanCodeFromString("11111"));
        assertEquals(letterCodes['U'], HuffmanCodeFromString("100"));
        assertEquals(letterCodes['Z'], HuffmanCodeFromString("111100"));
    }

    @Test
    public void compressAndDecompressEmptyData() throws Exception {
        String content = "";
        ByteArrayInputStream inputBais = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream inputBaos = new ByteArrayOutputStream();

        HuffmanEncoder.compress(inputBais, inputBaos);

        ByteArrayInputStream outBais = new ByteArrayInputStream(
            inputBaos.toByteArray());
        ByteArrayOutputStream outBaos = new ByteArrayOutputStream();

        HuffmanDecoder.decompress(outBais, outBaos);

        assertArrayEquals(content.getBytes(), outBaos.toByteArray());
    }

    @Test
    public void compressAndDecompressNoLeftOver() throws Exception {
        String content = "HELLO WORLD";
        ByteArrayInputStream inputBais = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream inputBaos = new ByteArrayOutputStream();

        HuffmanEncoder.compress(inputBais, inputBaos);

        ByteArrayInputStream outBais = new ByteArrayInputStream(
            inputBaos.toByteArray());
        ByteArrayOutputStream outBaos = new ByteArrayOutputStream();

        HuffmanDecoder.decompress(outBais, outBaos);

        assertArrayEquals(content.getBytes(), outBaos.toByteArray());
    }

    @Test
    public void compressAndDecompressWithLeftOver() throws Exception {
        String content = "ABACBACBABCABCABCBACBACBA";
        ByteArrayInputStream inputBais = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream inputBaos = new ByteArrayOutputStream();

        HuffmanEncoder.compress(inputBais, inputBaos);

        ByteArrayInputStream outBais = new ByteArrayInputStream(
            inputBaos.toByteArray());
        ByteArrayOutputStream outBaos = new ByteArrayOutputStream();

        HuffmanDecoder.decompress(outBais, outBaos);

        assertArrayEquals(content.getBytes(), outBaos.toByteArray());
    }

    private static HuffmanCode HuffmanCodeFromString(String input) {
        int size = input.length();
        int bits = 0;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '1') {
                bits |= (1 << (size - i - 1));
            } else if (input.charAt(i) != '0') {
                throw new IllegalArgumentException("Only '0' or '1' allowed");
            }
        }
        return new HuffmanCode(bits, size);
    }
}
