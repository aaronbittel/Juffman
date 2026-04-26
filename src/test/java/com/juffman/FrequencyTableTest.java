package com.juffman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FrequencyTableTest {
    @Test
    public void writeFrequencyTableHeader() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(baos)) {
            FrequencyTable table = sampleFrequencyTable();
            table.writeToStream(out);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (DataInputStream in = new DataInputStream(bais)) {
            // check header
            assertEquals('H', in.readByte());
            assertEquals('U', in.readByte());
            assertEquals('F', in.readByte());

            // check FrequencyFormat BYTE
            assertEquals(1, in.readByte());
            // check unique byte count
            assertEquals(8, in.readByte());

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

            assertEquals(in.available(), 0);
        }
    }

    @Test
    public void writeAndReadFrequencyHeader() throws Exception {
        FrequencyTable expectedTable = sampleFrequencyTable();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        expectedTable.writeToStream(out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream in = new DataInputStream(bais);
        FrequencyTable actualTable = FrequencyTable.fromStream(in);

        assertEquals(expectedTable, actualTable);
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
