package com.juffman.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.io.OutputStream;

import com.juffman.HuffmanDecoder;

public record Decode(Path inputPath, Path outputPath) implements Command {

    @Override
    public void execute() throws Exception {
        try (InputStream in = Files.newInputStream(inputPath))
        {
            try(OutputStream out = Files.newOutputStream(outputPath))
            {
                HuffmanDecoder.decompress(in, out);
            }
        }

        System.out.printf("[INFO] Decoded `%s` into `%s`%n", inputPath, outputPath);
    }
}
