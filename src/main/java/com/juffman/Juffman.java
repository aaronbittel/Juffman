package com.juffman;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
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
            System.out.println(frequencies['X']);
            System.out.println(frequencies['t']);
        } catch(IOException e) {
            System.err.printf("ERROR: reading '%s': %s%n", filepath, e.getMessage());
            System.exit(1);
        }
    }

    public static int[] countFrequencies(byte[] content) {
        int[] frequencies = new int[256];
        for (byte b : content) {
            frequencies[Byte.toUnsignedInt(b)]++;
        }
        return frequencies;
    }
}
