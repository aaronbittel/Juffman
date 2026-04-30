package com.github.aaronbittel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HuffmanDecoder {

    private HuffmanDecoder() { }

    public static void decompress(
        InputStream inStream,
        OutputStream outStream
    ) throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(inStream));
        DataOutputStream out = new DataOutputStream(
            new BufferedOutputStream(outStream));

        FrequencyTable table = FrequencyTable.readFrom(in);
        long count = table.totalCount();
        if (count == 0) return;

        HuffmanNode root = HuffmanTreeBuilder.build(table);
        decode(root, count, in, out);
        out.flush();
    }

    public static void decode(
        HuffmanNode root,
        long count,
        DataInputStream in,
        DataOutputStream out
    ) throws IOException {
        BitReader bitReader = new BitReader(in);
        for (long i = 0; i < count; ++i) {
            HuffmanNode current = root;
            while(!current.isLetterNode()) {
                current = bitReader.read() ? current.right() : current.left();
            }
            out.writeByte(current.value());
        }
    }
}
