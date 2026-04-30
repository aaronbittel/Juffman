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

        try(InputStream in = Files.newInputStream(inputPath)) {
            try(OutputStream out = Files.newOutputStream(outputPath)) {
                HuffmanEncoder.compress(in, out);
            }
        }

        long end = System.nanoTime();

        long inputFileSize = Files.size(inputPath);
        long outputFileSize = Files.size(outputPath);

        double ratio = (double) outputFileSize / inputFileSize;
        double savings = 1.0 - ratio;

        System.out.printf("[INFO] Encoded `%s` into `%s`%n", inputPath, outputPath);
        System.out.printf("[INFO] Size: %,d → %,d bytes (%.2f%% saved, ratio %.2f:1)%n",
            inputFileSize,
            outputFileSize,
            savings * 100,
            1 / ratio);
        System.out.printf(
            "[INFO] Took %.5f seconds%n", (end - start) / 1_000_000_000.0);
    }
}

