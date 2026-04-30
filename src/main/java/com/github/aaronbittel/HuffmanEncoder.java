package com.github.aaronbittel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HuffmanEncoder {

    private HuffmanEncoder() { }

    public static void compress(
        InputStream inStream,
        OutputStream outStream
    ) throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(inStream));
        DataOutputStream out = new DataOutputStream(
            new BufferedOutputStream(outStream));
        // TODO: dont read all bytes into memory, use chunked reading and 2 streams
        byte[] data = in.readAllBytes();

        FrequencyTable table = FrequencyTable.fromBytes(data);

        if (data.length == 0) {
            table.writeTo(out);
            out.flush();
            return;
        }

        HuffmanNode root = HuffmanTreeBuilder.build(table);
        HuffmanCode[] codeTable = HuffmanCodeBuilder.build(root);
        table.writeTo(out);
        encode(data, codeTable, out);
        out.flush();
    }

    private static void encode(
        byte[] data,
        HuffmanCode[] codeTable,
        DataOutputStream out
    ) throws IOException {
        BitWriter bitWriter = new BitWriter(out);
        for (byte b : data) {
            HuffmanCode code = codeTable[Byte.toUnsignedInt(b)];
            bitWriter.write(code);
        }
        bitWriter.flush();
    }
}
