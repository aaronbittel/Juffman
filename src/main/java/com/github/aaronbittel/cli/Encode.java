package com.github.aaronbittel.cli;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.aaronbittel.HuffmanEncoder;

public record Encode(Path inputPath, Path outputPath) implements Command {

    @Override
    public void execute() throws Exception {
        long start = System.nanoTime();

        InputStream in = inputPath == null
            ? System.in
            : Files.newInputStream(inputPath);

        OutputStream out = outputPath == null
            ? System.out
            : Files.newOutputStream(outputPath);

        try(in) {
            try(out) {
                HuffmanEncoder.compress(in, out);
            }
        }

        long end = System.nanoTime();

        if (inputPath != null && outputPath != null) {
            long inputFileSize = Files.size(inputPath);
            long outputFileSize = Files.size(outputPath);

            double ratio = (double) outputFileSize / inputFileSize;
            double savings = 1.0 - ratio;

            System.out.printf("[INFO] Encoded `%s` into `%s`%n", inputPath, outputPath);
            System.out.printf(
                "[INFO] Size: %,d → %,d bytes (%.2f%% saved, ratio %.2f:1)%n",
                inputFileSize,
                outputFileSize,
                savings * 100,
                1 / ratio);
        } else if (inputPath == null && outputPath != null) {
            long outputFileSize = Files.size(outputPath);
            System.err.printf(
                "[INFO] Encoded output written (%d bytes)%n", outputFileSize);
        }

        System.err.printf(
            "[INFO] Encoding took %.5f seconds%n", (end - start) / 1_000_000_000.0);
    }
}

