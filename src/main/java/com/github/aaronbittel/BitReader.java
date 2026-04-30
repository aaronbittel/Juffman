package com.github.aaronbittel;

import java.io.DataInputStream;
import java.io.IOException;

class BitReader {
    private final DataInputStream in;
    private Byte b = null;
    private int index = 7;

    public BitReader(DataInputStream in) {
        this.in = in;
    }

    public boolean read() throws IOException {
        if (b == null) {
            b = in.readByte();
            index = 7;
        }
        int mask = 1 << index;
        boolean bit = (b & mask) > 0;
        index--;
        if (index < 0) {
            b = null;
        }
        return bit;
    }
}
