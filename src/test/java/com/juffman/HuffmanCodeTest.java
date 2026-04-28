package com.juffman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HuffmanCodeTest {

    @Test
    public void huffmanCode() {
        HuffmanCode code = new HuffmanCode(14, 4);
        assertEquals(4, code.size());
        assertEquals("1110", code.toString());
    }
}
