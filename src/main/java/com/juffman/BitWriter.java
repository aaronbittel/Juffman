package com.juffman;

import java.io.DataOutputStream;
import java.io.IOException;

class BitWriter {
    private final DataOutputStream out;
    private byte b = 0;
    private int index = 7;

    public BitWriter(DataOutputStream out) {
        this.out = out;
    }

    public void write(HuffmanCode code) throws IOException {
        for (int i = 0; i < code.size(); ++i) {
            if (code.get(i)) {
                int mask = 1 << index;
                b |= (byte) mask;
            }
            index--;
            if (index < 0) {
                flushByte();
            }
        }
    }

    private void flushByte() throws IOException {
        out.writeByte((int)b);
        index = 7;
        b = 0;
    }

    public void flush() throws IOException {
        if (index < 7) flushByte();
    }
}
