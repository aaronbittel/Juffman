package com.juffman;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
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
        CliConfig config = parseArgs(args);
        try {
            run(config);
        } catch(IOException e) {
            System.err.printf("Something went wrong: %s%n", e.getMessage());
        }
    }

    private static CliConfig helpConfig() {
        return new CliConfig(null, null, null, true, false);
    }

    private static CliConfig versionConfig() {
        return new CliConfig(null, null, null, false, true);
    }

    public static CliConfig parseArgs(String[] args) {
        if (args.length == 0) {
            System.err.println("ERROR: No subcommand provided");
            System.exit(1);
        }

        boolean version = false;
        boolean help = false;

        for (String arg : args) {
            switch (arg) {
                case "--help":
                case "-h":
                    help = true;
                    break;
                case "--version":
                case "-v":
                    version = true;
                    break;
            }
        }

        if (help) return helpConfig();
        if (version) return versionConfig();

        String inputFile = null;
        String outputFile = null;
        CliMode mode = null;

        String subcommand = args[0];

        if (subcommand.equals("decode")) mode = CliMode.DECODE;
        else if (subcommand.equals("encode")) mode = CliMode.ENCODE;
        else {
            printUsage();
            System.err.printf("ERROR: Unknown subcommand `%s`%n", subcommand);
            System.exit(1);
        }

        for (int i = 1; i < args.length; ++i) {
            switch(args[i]) {
                case "--input":
                case "-i": {
                    if (i + 1 >= args.length) {
                        printUsage();
                        System.err.println("ERROR: Missing value for --input");
                        System.exit(1);
                    }
                    inputFile = args[++i];
                    break;
                }
                case "--output":
                case "-o": {
                    if (i + 1 >= args.length) {
                        printUsage();
                        System.err.println("ERROR: Missing value for --output");
                        System.exit(1);
                    }
                    outputFile = args[++i];
                    break;
                }
                default: {
                    printUsage();
                    System.err.printf("ERROR: Unknown option `%s`%n", args[i]);
                    System.exit(1);
                    break;
                }
            }
        }

        if (inputFile == null) {
            printUsage();
            System.err.println("ERROR: No inputfile provided");
            System.exit(1);
        }

        if (outputFile == null) {
            printUsage();
            System.err.println("ERROR: No outputfile provided");
            System.exit(1);
        }

        return new CliConfig(mode, inputFile, outputFile, false, false);
    }

    public static void decode(
        HuffmanNode root,
        long count,
        DataInputStream in,
        DataOutputStream out
    ) throws IOException {
        BitReader bitReader = new BitReader(in);
        for (long i = 0; i < count; ++i) {
            HuffmanNode current = root;
            while(!current.isLetterNode()) {
                current = bitReader.read() ? current.getRight() : current.getLeft();
            }
            out.writeByte(current.getValue());
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

    private static void printHelp() {
        System.out.println("Juffman - Huffman Encoder / Decoder");
        System.out.println();

        System.out.println("Usage:");
        System.out.println("  juffman encode -i <input> -o <output>");
        System.out.println("  juffman decode -i <input> -o <output>");
        System.out.println();

        System.out.println("Options:");
        System.out.println("  -i, --input <file>     Input file");
        System.out.println("  -o, --output <file>    Output file");
        System.out.println("  -h, --help             Show this help message");
        System.out.println("  -v, --version          Show version information");
        System.out.println();

        System.out.println("Subcommands:");
        System.out.println("  encode        Compress a file using Huffman coding");
        System.out.println("  decode        Decompress a Huffman-encoded file");
        System.out.println();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  juffman encode -i <input> -o <output>");
        System.out.println("  juffman decode -i <input> -o <output>");
        System.out.println();
        System.out.println("Try 'juffman --help' for more information.");
    }

    private static void printVersion() {
        System.out.println("v1.0-SNAPSHOT");
    }

    private static void printIllegalCommand() {
        System.err.println("Illegal command");
    }

    private static void encode(String inputFile, String outputFile) throws IOException {
        Path inputPath = Path.of(inputFile);
        if (!Files.exists(inputPath)) {
            System.err.printf("ERROR: '%s' does not exist", inputPath);
            System.exit(1);
        }
        byte[] data = Files.readAllBytes(inputPath);
        FrequencyTable table = FrequencyTable.fromBytes(data);
        HuffmanNode root = generateHuffmanTree(table);

        HuffmanCode[] codeTable = generateHuffmanCodesForLetters(root);
        try (DataOutputStream out = new DataOutputStream(
            new FileOutputStream(outputFile)))
        {
            table.writeToStream(out);
            Juffman.encode(data, codeTable, out);
            System.out.printf("[INFO] Encoded `%s` into `%s`%n", inputFile, outputFile);
        }
    }

    private static void decode(String inputFile, String outputFile) throws IOException {
        try (DataInputStream in = new DataInputStream(
            new FileInputStream(inputFile)))
        {
            FrequencyTable table = FrequencyTable.fromStream(in);
            long totalCount = table.totalCount();
            HuffmanNode root = generateHuffmanTree(table);
            try(DataOutputStream out = new DataOutputStream(
                new FileOutputStream(outputFile)))
            {
                Juffman.decode(root, totalCount, in, out);
                System.out.printf(
                    "[INFO] Decoded `%s` into `%s`%n", inputFile, outputFile);
            }
        }
    }

    public static void run(CliConfig config) throws IOException {
        if (config.help()) printHelp();
        else if (config.version()) printVersion();
        else if (config.mode() == CliMode.ENCODE)
            encode(config.inputFile(), config.outputFile());
        else if (config.mode() == CliMode.DECODE)
            decode(config.inputFile(), config.outputFile());
        else printIllegalCommand();
    }
}

class BitReader {
    private final DataInputStream in;
    private Byte b = null;
    private int index = 7;

    public BitReader(DataInputStream in) {
        this.in = in;
    }

    public boolean read() throws IOException {
        if (b == null) {
            b = in.readByte();
            index = 7;
        }
        int mask = 1 << index;
        boolean bit = (b & mask) > 0;
        index--;
        if (index < 0) {
            b = null;
        }
        return bit;
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

enum CliMode {
    ENCODE,
    DECODE;
}

record CliConfig(
    CliMode mode,
    String inputFile,
    String outputFile,
    boolean help,
    boolean version
) { }
