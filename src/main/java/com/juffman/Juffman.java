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

enum FrequencyFormat {

    BYTE(1) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeByte((byte)freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return (long)in.readByte();
        }
    },
    SHORT(2) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeShort((short)freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return (long)in.readShort();
        }
    },
    INT(4) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeInt((int)freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return (long)in.readInt();
        }
    },
    LONG(8) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeLong(freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return in.readLong();
        }
    };

    private final int length;

    FrequencyFormat(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    abstract void writeFrequency(long freq, DataOutputStream out) throws IOException;
    abstract long readFrequency(DataInputStream in) throws IOException;
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
            long[] frequencies = countFrequencies(content);
            HuffmanNode root = generateHuffmanTree(frequencies);
            // System.out.println(root.toStringIndent(0));

            HuffmanCode[] codeTable = generateHuffmanCodesForLetters(root);
            writeFrequencyTableToFile(frequencies, "encoded.txt");
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

    public static long[] countFrequencies(byte[] content) {
        long[] frequencies = new long[256];
        for (byte b : content) {
            frequencies[Byte.toUnsignedInt(b)]++;
        }
        return frequencies;
    }

    public static HuffmanNode generateHuffmanTree(long[] frequencies) {
        List<HuffmanNode> nodes = new ArrayList<>(frequencies.length);
        for (int i = 0; i < frequencies.length; ++i) {
            long freq = frequencies[i];
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

    public static void writeFrequencyTable(
        long[] frequencies, DataOutputStream out
    ) throws IOException {
        // magic
        out.writeByte((byte)'H');
        out.writeByte((byte)'U');
        out.writeByte((byte)'F');

        long max = Long.MIN_VALUE;
        int size = 0;

        for (long f : frequencies) {
            if (f == 0) continue;
            size++;
            if (f > max) max = f;
        }

        FrequencyFormat format;
        if (max <= Byte.MAX_VALUE) format = FrequencyFormat.BYTE;
        else if (max <= Short.MAX_VALUE) format = FrequencyFormat.SHORT;
        else if (max <= Integer.MAX_VALUE) format = FrequencyFormat.INT;
        else format = FrequencyFormat.LONG;

        out.writeByte(format.getLength()); // length of frequency fields
        out.writeByte(size);               // count of unique bytes

        for (int i = 0; i < frequencies.length; ++i) {
            long freq = frequencies[i];
            if (freq == 0) continue;
            out.writeByte((byte)i);
            format.writeFrequency(freq, out);
        }
    }

    public static void writeFrequencyTableToFile(long[] frequencies, String filename) {
        try (DataOutputStream out = new DataOutputStream(
            new FileOutputStream(filename))
        ) {
            writeFrequencyTable(frequencies, out);
        } catch(IOException e) {
            System.err.printf(
                "ERROR: could not write to file `%s`: %s%n", filename, e.getMessage());
        }
    }

    public static long[] readFrequencyTable(DataInputStream in) throws IOException {
        long[] frequencyTable = new long[256];
        if (in.readByte() != 'H' || in.readByte() != 'U' || in.readByte() != 'F')
            throw new IllegalStateException("Expected magic value");

        FrequencyFormat format = switch((int)in.readByte()) {
            case 1 -> FrequencyFormat.BYTE;
            case 2 -> FrequencyFormat.SHORT;
            case 4 -> FrequencyFormat.INT;
            case 8 -> FrequencyFormat.LONG;
            default -> throw new IllegalStateException("Unknown FrequencyFormat");
        };

        int size = in.readByte();

        for (int i = 0; i < size; ++i) {
            int index = Byte.toUnsignedInt(in.readByte());
            frequencyTable[index] = format.readFrequency(in);
        }

        return frequencyTable;
    }

    public static void dumpFrequencies(int[] frequencies, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < frequencies.length; ++i) {
                int freq = frequencies[i];
                if (freq == 0) continue;
                writer.write(String.valueOf((char)i).repeat(freq));
            }
            System.out.printf("[INFO] Successfully wrote file `%s`%n", filename);
        } catch(IOException e) {
            System.err.printf(
                "ERROR: could not write to file `%s`: %s%n", filename, e.getMessage());
        }
    }
}
