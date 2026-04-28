package com.juffman.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.io.OutputStream;

import com.juffman.HuffmanEncoder;

public record Encode(Path inputPath, Path outputPath) implements Command {

    @Override
    public void execute() throws Exception {
        try(InputStream in = Files.newInputStream(inputPath)) {
            try(OutputStream out = Files.newOutputStream(outputPath)) {
                HuffmanEncoder.compress(in, out);
            }
        }

        System.out.printf("[INFO] Encoded `%s` into `%s`%n", inputPath, outputPath);
    }
}

