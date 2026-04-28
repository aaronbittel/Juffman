package com.juffman.cli;

import java.nio.file.Path;

public class ArgParser {
    public static Command parse(String[] args) {
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

        if (help) return new Help();
        if (version) return new Version();

        String inputFile = null;
        String outputFile = null;

        String subcommand = args[0];

        if (subcommand.equals("encode")) {
            return parseEncode(args);
        } else if (subcommand.equals("decode")) {
            return parseDecode(args);
        } else {
            throw new IllegalArgumentException(
                String.format("Unknown subcommand `%s`", subcommand));
        }
    }

    private static String requireValue(String[] args, int i, String flag) {
        if (i + 1 >= args.length) {
            throw new IllegalArgumentException(
                "Missing value for flag '" + flag + "'"
            );
        }
        return args[i + 1];
    }

    private static Encode parseEncode(String[] args) {
        Path input = null;
        Path output = null;

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "-i", "--input" -> {
                    input = Path.of(requireValue(args, i, "input"));
                    i++;
                }
                case "-o", "--output" -> {
                    output = Path.of(requireValue(args, i, "output"));
                    i++;
                }
            }
        }

        if (input == null) {
            throw new IllegalArgumentException("Missing input file");
        }

        if (output == null) {
            throw new IllegalArgumentException("Missing output file");
        }

        return new Encode(input, output);
    }

    private static Decode parseDecode(String[] args) {
        Path input = null;
        Path output = null;

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "-i", "--input" -> {
                    input = Path.of(requireValue(args, i, "input"));
                    i++;
                }
                case "-o", "--output" -> {
                    output = Path.of(requireValue(args, i, "output"));
                    i++;
                }
            }
        }

        if (input == null) {
            throw new IllegalArgumentException("Missing input file");
        }

        if (output == null) {
            throw new IllegalArgumentException("Missing output file");
        }

        return new Decode(input, output);
    }
}
