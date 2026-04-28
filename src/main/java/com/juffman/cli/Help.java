package com.juffman.cli;

public record Help() implements Command {

    @Override
    public void execute() {
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
}

