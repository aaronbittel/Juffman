package com.github.aaronbittel.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.github.aaronbittel.HuffmanDecoder;

public record Decode(Path inputPath, Path outputPath) implements Command {

    @Override
    public void execute() throws Exception {
        long start = System.nanoTime();

        try (DataInputStream in = new DataInputStream(
            new BufferedInputStream(Files.newInputStream(inputPath))))
        {
            try(DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(outputPath))))
            {
                HuffmanDecoder.decompress(in, out);
            }
        }

        long end = System.nanoTime();

        System.out.printf("[INFO] Decoded `%s` into `%s`%n", inputPath, outputPath);
        System.out.printf(
            "[INFO] Took %.5f seconds%n", (end - start) / 1_000_000_000.0);
    }
}
