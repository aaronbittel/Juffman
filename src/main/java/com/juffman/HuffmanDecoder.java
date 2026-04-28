package com.juffman;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class HuffmanDecoder {

    private HuffmanDecoder() { }

    public static void decompress(InputStream in, OutputStream out) throws IOException {
        FrequencyTable table = FrequencyTable.readFrom(in);
        long count = table.totalCount();
        if (count == 0) return;

        HuffmanNode root = HuffmanTreeBuilder.build(table);
        decode(root, count, in, out);
    }

    public static void decode(
        HuffmanNode root,
        long count,
        InputStream inStream,
        OutputStream outStream
    ) throws IOException {
        DataInputStream in = new DataInputStream(inStream);
        DataOutputStream out = new DataOutputStream(outStream);
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
