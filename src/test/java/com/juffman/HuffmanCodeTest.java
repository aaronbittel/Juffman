package com.juffman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HuffmanCodeTest {

    @Test
    public void huffmanCode() {
        HuffmanCode code = new HuffmanCode(14, 4);
        assertEquals(code.size(), 4);
        assertEquals(code.toString(), "1110");
    }
}
