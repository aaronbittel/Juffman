package com.juffman;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JuffmanTest {

    @Test
    public void countFrequeciesEmpty() {
        String empty = "";
        int[] frequencies = Juffman.countFrequencies(empty.getBytes());
        assertEquals(getTotalCount(frequencies), empty.length());
        for (int f : frequencies) {
            assertEquals(f, 0);
        }
    }

    @Test
    public void countFrequeciesString() {
        String str = "AAAaaB";
        int[] frequencies = Juffman.countFrequencies(str.getBytes());
        assertEquals(getTotalCount(frequencies), str.length());
        for (int i = 0; i < frequencies.length; ++i) {
            int f = frequencies[i];
            if (i == 'A') assertEquals(f, 3);
            else if (i == 'a') assertEquals(f, 2);
            else if (i == 'B') assertEquals(f, 1);
            else assertEquals(f, 0);
        }
    }

    @Test
    public void countFrequeciesUnicode() {
        String unicode = "€é漢";
        byte[] bytes = unicode.getBytes();
        int[] frequencies = Juffman.countFrequencies(unicode.getBytes());
        assertEquals(getTotalCount(frequencies), bytes.length);
        for (byte b : bytes) {
            int unsigned = Byte.toUnsignedInt(b);
            assertEquals(1, frequencies[unsigned]);
        }
    }

    private int getTotalCount(int[] frequencies) {
        int sum = 0;
        for (int f : frequencies) {
            sum += f;
        }
        return sum;
    }
}
