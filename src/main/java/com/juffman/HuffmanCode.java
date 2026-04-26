package com.juffman;

import java.util.BitSet;
import java.util.Objects;

class HuffmanCode implements Cloneable {
    private BitSet bitSet;
    private int size;

    public HuffmanCode() {
        bitSet = new BitSet();
        size = 0;
    }

    public HuffmanCode(String s) {
        bitSet = new BitSet(s.length());
        size = s.length();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '1') bitSet.set(i);
        }
    }

    public HuffmanCode(HuffmanCode other) {
        this.size = other.size;
        this.bitSet = (BitSet) other.bitSet.clone();
    }

    public void append(int v) {
        if (v == 1) bitSet.set(size++);
        else if (v == 0) bitSet.clear(size++);
        else throw new IllegalArgumentException(
            "Only 0 or 1 can be appended to HuffmanCode.");
    }

    public boolean get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("HuffmanCode access out of bounds");
        return bitSet.get(index);
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HuffmanCode)) return false;
        HuffmanCode other = (HuffmanCode)o;
        return size == other.size && bitSet.equals(other.bitSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, bitSet);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            sb.append(bitSet.get(i) ? '1' : '0');
        }
        return sb.toString();
    }
}
