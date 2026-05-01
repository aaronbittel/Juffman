package com.github.aaronbittel.cli;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.aaronbittel.HuffmanDecoder;

public record Decode(Path inputPath, Path outputPath) implements Command {

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
                HuffmanDecoder.decompress(in, out);
            }
        }

        long end = System.nanoTime();

        if (inputPath != null && outputPath != null) {
            System.out.printf("[INFO] Decoded `%s` into `%s`%n", inputPath, outputPath);
        }
        System.out.printf(
            "[INFO] Took %.5f seconds%n", (end - start) / 1_000_000_000.0);
    }
}
