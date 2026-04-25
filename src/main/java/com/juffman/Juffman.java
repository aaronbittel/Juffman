package com.juffman;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            // System.out.println(root.toStringIndent(0));

            HuffmanCode[] codeTable = generateHuffmanCodesForLetters(root);
            System.out.println(codeTable['t']);
            System.out.println(codeTable['X']);
        } catch(IOException e) {
            System.err.printf("ERROR: reading '%s': %s%n", filepath, e.getMessage());
            System.exit(1);
        }
    }

    private static void generateCode(
        HuffmanCode[] codes,
        HuffmanNode node,
        HuffmanCode code
    ) {
        if (node.isLetterNode()) {
            codes[node.getIndex()] = new HuffmanCode(code);
        }
        if (node.hasLeft()) {
            HuffmanCode leftCode = new HuffmanCode(code);
            leftCode.append(0);
            generateCode(codes, node.getLeft(), leftCode);
        }
        if (node.hasRight()) {
            HuffmanCode rightCode = new HuffmanCode(code);
            rightCode.append(1);
            generateCode(codes, node.getRight(), rightCode);
        }
    }

    public static HuffmanCode[] generateHuffmanCodesForLetters(HuffmanNode root) {
        HuffmanCode[] codes = new HuffmanCode[256];
        generateCode(codes, root, new HuffmanCode());
        return codes;
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

    public static void dumpFrequencies(int[] frequencies, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < frequencies.length; ++i) {
                int count = frequencies[i];
                if (count == 0) continue;
                writer.write(String.valueOf((char)i).repeat(count));
            }
            System.out.printf("[INFO] Successfully wrote file `%s`%n", filename);
        } catch(IOException e) {
            System.err.printf("ERROR: could not write to file `%s`: %s%n", filename, e.getMessage());
        }
    }
}
