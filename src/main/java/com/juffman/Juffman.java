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

import com.juffman.cli.ArgParser;
import com.juffman.cli.Command;

public class Juffman {
    static void main(String[] args) {
        try {
            Command cmd = ArgParser.parse(args);
            cmd.execute();
        } catch(IllegalArgumentException i) {
            printUsage();
            System.err.println("ERROR: " + i.getMessage());
            System.exit(1);
        } catch(Exception e) {
            System.err.printf("ERROR: Something went wrong: %s%n", e.getMessage());
            System.exit(1);
       }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  juffman encode -i <input> -o <output>");
        System.out.println("  juffman decode -i <input> -o <output>");
        System.out.println();
        System.out.println("Try 'juffman --help' for more information.");
    }
}
