package com.juffman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HuffmanCodeTest {

    @Test
    public void huffmanCode() {
        HuffmanCode code = new HuffmanCode("1110");
        assertEquals(code.getSize(), 4);
        assertEquals(code.toString(), "1110");
    }
}
