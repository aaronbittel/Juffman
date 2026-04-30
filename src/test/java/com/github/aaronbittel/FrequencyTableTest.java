package com.github.aaronbittel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FrequencyTableTest {
    @Test
    public void writeFrequencyTableHeader() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DataOutputStream out = new DataOutputStream(baos);
        FrequencyTable table = sampleFrequencyTable();
        table.writeTo(out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (DataInputStream in = new DataInputStream(bais)) {
            // check header
            assertEquals('H', in.readByte());
            assertEquals('U', in.readByte());
            assertEquals('F', in.readByte());

            // check FrequencyFormat BYTE
            assertEquals(1, in.readByte());
            // check unique byte count
            assertEquals(8, in.readShort());

            // check frequencies
            // frequencies['C'] = 32;
            assertEquals('C', in.readByte());
            assertEquals(32, in.readByte());
            // frequencies['D'] = 42;
            assertEquals('D', in.readByte());
            assertEquals(42, in.readByte());
            // frequencies['E'] = 120;
            assertEquals('E', in.readByte());
            assertEquals(120, in.readByte());
            // frequencies['K'] = 7;
            assertEquals('K', in.readByte());
            assertEquals(7, in.readByte());
            // frequencies['L'] = 42;
            assertEquals('L', in.readByte());
            assertEquals(42, in.readByte());
            // frequencies['M'] = 24;
            assertEquals('M', in.readByte());
            assertEquals(24, in.readByte());
            // frequencies['U'] = 37;
            assertEquals('U', in.readByte());
            assertEquals(37, in.readByte());
            // frequencies['Z'] = 2;
            assertEquals('Z', in.readByte());
            assertEquals(2, in.readByte());

            assertEquals(0, in.available());
        }
    }

    @Test
    public void writeAndReadFrequencyHeader() throws Exception {
        FrequencyTable expectedTable = sampleFrequencyTable();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        expectedTable.writeTo(out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream in = new DataInputStream(bais);
        FrequencyTable actualTable = FrequencyTable.readFrom(in);

        assertEquals(expectedTable, actualTable);
    }

    @Test
    public void allBytesOnce() throws IOException {
        byte[] allBytes = new byte[256];
        for (int i = 0; i < 256; ++i) {
            allBytes[i] = (byte)i;
        }
        FrequencyTable table = FrequencyTable.fromBytes(allBytes);
        for (int i = 0; i < 256; ++i) {
            assertEquals(1, table.get(i));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        table.writeTo(out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream in = new DataInputStream(bais);

        assertEquals('H', in.readUnsignedByte());
        assertEquals('U', in.readUnsignedByte());
        assertEquals('F', in.readUnsignedByte());

        assertEquals(1, in.readUnsignedByte()); // frequency format (BYTE)
        assertEquals(256, in.readShort());      // count unique

        for (int i = 0; i < 256; ++i) {
            assertEquals(i, in.readUnsignedByte());
            assertEquals(1, in.readUnsignedByte());
        }
    }

    public static FrequencyTable sampleFrequencyTable() {
        long[] frequencies = new long[256];
        frequencies['C'] = 32;
        frequencies['D'] = 42;
        frequencies['E'] = 120;
        frequencies['K'] = 7;
        frequencies['L'] = 42;
        frequencies['M'] = 24;
        frequencies['U'] = 37;
        frequencies['Z'] = 2;
        return new FrequencyTable(frequencies);
    }
}
