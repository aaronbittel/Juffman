package com.juffman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Files;

public class Juffman {
    static void main(String[] args) {
        try {
            CliConfig config = parseArgs(args);
            run(config);
        } catch(IllegalArgumentException i) {
            printUsage();
            System.err.println("ERROR: " + i.getMessage());
            System.exit(1);
        } catch(IOException e) {
            System.err.printf("Something went wrong: %s%n", e.getMessage());
            System.exit(1);
        }
    }

    private static void decode(String inputFile, String outputFile) throws IOException {
        Path inputPath = Path.of(inputFile);
        if (!Files.exists(inputPath)) {
            System.err.printf("ERROR: '%s' does not exist", inputFile);
            System.exit(1);
        }
        try (InputStream in = Files.newInputStream(inputPath))
        {
            try(OutputStream out = Files.newOutputStream(Path.of(outputFile)))
            {
                HuffmanDecoder.decompress(in, out);
                System.out.printf(
                    "[INFO] Decoded `%s` into `%s`%n", inputFile, outputFile);
            }
        }
    }

    private static void encode(String inputFile, String outputFile) throws IOException {
        Path inputPath = Path.of(inputFile);
        if (!Files.exists(inputPath)) {
            System.err.printf("ERROR: '%s' does not exist", inputPath);
            System.exit(1);
        }
        try(InputStream in = Files.newInputStream(inputPath)) {
            try(OutputStream out = Files.newOutputStream(Path.of(outputFile))) {
                HuffmanEncoder.compress(in, out);
                System.out.printf("[INFO] Encoded `%s` into `%s`%n", inputFile, outputFile);
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

    private static CliConfig helpConfig() {
        return new CliConfig(null, null, null, true, false);
    }

    private static CliConfig versionConfig() {
        return new CliConfig(null, null, null, false, true);
    }

    public static CliConfig parseArgs(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No subcommand provided");
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
        CliMode mode;

        String subcommand = args[0];

        if (subcommand.equals("decode")) mode = CliMode.DECODE;
        else if (subcommand.equals("encode")) mode = CliMode.ENCODE;
        else {
            throw new IllegalArgumentException(
                String.format("Unknown subcommand `%s`", subcommand));
        }

        for (int i = 1; i < args.length; ++i) {
            switch(args[i]) {
                case "--input":
                case "-i": {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(
                            "Missing value for --input");
                    }
                    inputFile = args[++i];
                    break;
                }
                case "--output":
                case "-o": {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(
                            "Missing value for --output");
                    }
                    outputFile = args[++i];
                    break;
                }
                default: {
                    throw new IllegalArgumentException(
                        String.format("Unknown option `%s`", args[i]));
                }
            }
        }

        if (inputFile == null) {
            throw new IllegalArgumentException("No input file provided");
        }

        if (outputFile == null) {
            throw new IllegalArgumentException("No outfile provided");
        }

        return new CliConfig(mode, inputFile, outputFile, false, false);
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
}

enum CliMode {
    ENCODE,
    DECODE
}

record CliConfig(
    CliMode mode,
    String inputFile,
    String outputFile,
    boolean help,
    boolean version
) { }
