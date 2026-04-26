package com.juffman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public final class FrequencyTable {
    private static final int SIZE = 256;

    private final long[] frequencies;

    public FrequencyTable (long[] frequencies) {
        if (frequencies == null) {
            throw new NullPointerException("Frequency array must not be null");
        }
        if (frequencies.length != SIZE) {
            throw new IllegalArgumentException(
                "Frequency array must have length " + SIZE +
                " (one entry per byte value 0–255), but was " +
                frequencies.length
            );
        }
        this.frequencies = Arrays.copyOf(frequencies, SIZE);
    }

    public static FrequencyTable fromBytes(byte[] data) {
        long[] frequencies = new long[SIZE];
        for (byte b : data) {
            frequencies[Byte.toUnsignedInt(b)]++;
        }
        return new FrequencyTable(frequencies);
    }

    public void writeToStream(DataOutputStream out) throws IOException {
        // magic
        out.writeByte((byte)'H');
        out.writeByte((byte)'U');
        out.writeByte((byte)'F');

        long max = Long.MIN_VALUE;
        int size = 0;

        for (long f : frequencies) {
            if (f == 0) continue;
            size++;
            if (f > max) max = f;
        }

        FrequencyFormat format;
        if (max <= Byte.MAX_VALUE) format = FrequencyFormat.BYTE;
        else if (max <= Short.MAX_VALUE) format = FrequencyFormat.SHORT;
        else if (max <= Integer.MAX_VALUE) format = FrequencyFormat.INT;
        else format = FrequencyFormat.LONG;

        out.writeByte(format.getLength()); // length of frequency fields
        out.writeByte(size);               // count of unique bytes

        for (int i = 0; i < SIZE; ++i) {
            long freq = frequencies[i];
            if (freq == 0) continue;
            out.writeByte((byte)i);
            format.writeFrequency(freq, out);
        }
    }

    public static FrequencyTable fromStream(DataInputStream in) throws IOException {
        if (in.readByte() != 'H' || in.readByte() != 'U' || in.readByte() != 'F')
            throw new IOException("Invalid magic header: expected 'HUF'");

        FrequencyFormat format = FrequencyFormat.fromCode(in.readByte());
        int size = in.readByte();
        long[] frequencies = new long[SIZE];

        for (int i = 0; i < size; ++i) {
            int index = Byte.toUnsignedInt(in.readByte());
            frequencies[index] = format.readFrequency(in);
        }

        return new FrequencyTable(frequencies);
    }

    public long get(int index) {
        if (index < 0 || index >= SIZE) throw new IndexOutOfBoundsException(index);
        return frequencies[index];
    }

    public void set(int index, long value) {
        if (index < 0 || index >= SIZE) throw new IndexOutOfBoundsException(index);
        frequencies[index] = value;
    }

    public long totalCount() {
        long count = 0;
        for (long freq : frequencies) {
            count += freq;
        }
        return count;
    }

    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrequencyTable)) return false;
        FrequencyTable other = (FrequencyTable)o;
        return Arrays.equals(frequencies, other.frequencies);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(frequencies);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < SIZE; ++i) {
            if (frequencies[i] == 0) continue;
            if (i == ' ') sb.append(' ');
            else if (i > ' ' && i <= 126) sb.append((char)i);
            else sb.append(i);

            sb.append(" -> ").append(frequencies[i]).append("\n");
        }

        return sb.toString();
    }
}

enum FrequencyFormat {

    BYTE(1) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeByte((byte)freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return (long)in.readByte();
        }
    },
    SHORT(2) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeShort((short)freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return (long)in.readShort();
        }
    },
    INT(4) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeInt((int)freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return (long)in.readInt();
        }
    },
    LONG(8) {
        @Override
        void writeFrequency(long freq, DataOutputStream out) throws IOException {
            out.writeLong(freq);
        }

        @Override
        long readFrequency(DataInputStream in) throws IOException {
            return in.readLong();
        }
    };

    private final int length;

    FrequencyFormat(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public static FrequencyFormat fromCode(int code) {
        return switch(code) {
            case 1 -> BYTE;
            case 2 -> SHORT;
            case 4 -> INT;
            case 8 -> LONG;
            default -> throw new IllegalArgumentException(
                "Unknown FrequencyFormat code: " + code
            );
        };
    }

    abstract void writeFrequency(long freq, DataOutputStream out) throws IOException;
    abstract long readFrequency(DataInputStream in) throws IOException;
}
