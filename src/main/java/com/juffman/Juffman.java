package com.juffman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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
            byte[] data = Files.readAllBytes(filepath);
            FrequencyTable frequencyTable = FrequencyTable.fromBytes(data);
            HuffmanNode root = generateHuffmanTree(frequencyTable);

            HuffmanCode[] codeTable = generateHuffmanCodesForLetters(root);
            String filename = "encoded.txt";
            try (DataOutputStream out = new DataOutputStream(
                new FileOutputStream(filename)))
            {
                frequencyTable.writeToStream(out);
                Juffman.encode(data, codeTable, out);
                System.out.printf("[INFO] Generated `%s`%n", filename);
            }
        } catch(IOException e) {
            System.err.printf("ERROR: reading '%s': %s%n", filepath, e.getMessage());
            System.exit(1);
        }
    }

    public static void encode(
        byte[] data,
        HuffmanCode[] codeTable,
        DataOutputStream out
    ) throws IOException {
        BitWriter bitWriter = new BitWriter(out);
        for (byte b : data) {
            HuffmanCode code = codeTable[Byte.toUnsignedInt(b)];
            bitWriter.write(code);
        }
        bitWriter.flush();
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

    public static HuffmanNode generateHuffmanTree(FrequencyTable table) {
        List<HuffmanNode> nodes = new ArrayList<>(table.getSize());
        for (int i = 0; i < table.getSize(); ++i) {
            long freq = table.get(i);
            if (freq == 0L) continue;
            nodes.add(new HuffmanNode(Byte.valueOf((byte)i), freq));
        }
        nodes.sort((a, b) -> (int)(a.getCount() - b.getCount()));

        while(nodes.size() > 1) {
            HuffmanNode first = nodes.removeFirst();
            HuffmanNode second = nodes.removeFirst();
            nodes.add(new HuffmanNode(
                null, first.getCount() + second.getCount(), first, second));
            nodes.sort((a, b) -> (int)(a.getCount() - b.getCount()));
        }

        return nodes.getFirst();
    }
}

class BitWriter {
    private final DataOutputStream out;
    private byte b = 0;
    private int index = 7;

    public BitWriter(DataOutputStream out) {
        this.out = out;
    }

    public void write(HuffmanCode code) throws IOException {
        for (int i = 0; i < code.getSize(); ++i) {
            if (code.get(i)) {
                int mask = 1 << index;
                b |= mask;
            }
            index--;
            if (index < 0) {
                flushByte();
            }
        }
    }

    private void flushByte() throws IOException {
        out.writeByte((int)b);
        index = 7;
        b = 0;
    }

    public void flush() throws IOException {
        if (index < 7) flushByte();
    }
}
