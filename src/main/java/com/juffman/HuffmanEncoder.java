package com.juffman;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class HuffmanEncoder {

    private HuffmanEncoder() { }

    public static void compress(InputStream in, OutputStream out) throws IOException {
        byte[] data = in.readAllBytes();
        FrequencyTable table = FrequencyTable.fromBytes(data);
        HuffmanNode root = HuffmanTreeBuilder.build(table);
        HuffmanCode[] codeTable = HuffmanCodeBuilder.build(root);

        table.writeTo(out);
        encode(data, codeTable, out);
    }

    private static void encode(
        byte[] data,
        HuffmanCode[] codeTable,
        OutputStream stream
    ) throws IOException {
        DataOutputStream out = new DataOutputStream(stream);
        BitWriter bitWriter = new BitWriter(out);
        for (byte b : data) {
            HuffmanCode code = codeTable[Byte.toUnsignedInt(b)];
            bitWriter.write(code);
        }
        bitWriter.flush();
    }
}
