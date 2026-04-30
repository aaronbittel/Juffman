package com.github.aaronbittel;

public record HuffmanCode(int bits, int size) {
    public boolean get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("");
        }
        int mask = 1 << (size - 1 - index);
        return (bits & mask) > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            sb.append(get(i) ? '1' : '0');
        }
        return sb.toString();
    }
}
