package com.github.aaronbittel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FrequencyTable {
    static enum FrequencyFormat {

        BYTE(1) {
            @Override
            void writeTo(long freq, DataOutputStream out) throws IOException {
                out.writeByte((byte)freq);
            }

            @Override
            long readFrom(DataInputStream in) throws IOException {
                return in.readUnsignedByte();
            }
        },
        SHORT(2) {
            @Override
            void writeTo(long freq, DataOutputStream out) throws IOException {
                out.writeShort((short)freq);
            }

            @Override
            long readFrom(DataInputStream in) throws IOException {
                return in.readUnsignedShort();
            }
        },
        INT(4) {
            @Override
            void writeTo(long freq, DataOutputStream out) throws IOException {
                out.writeInt((int)freq);
            }

            @Override
            long readFrom(DataInputStream in) throws IOException {
                return Integer.toUnsignedLong(in.readInt());
            }
        },
        LONG(8) {
            @Override
            void writeTo(long freq, DataOutputStream out) throws IOException {
                out.writeLong(freq);
            }

            @Override
            long readFrom(DataInputStream in) throws IOException {
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

        abstract void writeTo(long freq, DataOutputStream out) throws IOException;
        abstract long readFrom(DataInputStream in) throws IOException;
    }

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

    public void writeTo(DataOutputStream out) throws IOException {
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

        out.writeByte(format.getLength());  // length of frequency fields
        out.writeShort(size);               // count of unique bytes

        for (int i = 0; i < SIZE; ++i) {
            long freq = frequencies[i];
            if (freq == 0) continue;
            out.writeByte((byte)i);
            format.writeTo(freq, out);
        }
    }

    public static FrequencyTable readFrom(DataInputStream in) throws IOException {
        if (in.readUnsignedByte() != 'H'
            || in.readUnsignedByte() != 'U'
            || in.readUnsignedByte() != 'F')
        {
            throw new IOException("Invalid magic header: expected 'HUF'");
        }

        FrequencyFormat format = FrequencyFormat.fromCode(in.readUnsignedByte());
        // need short: 0..=256 possible values for size
        // TODO: assert size 0 <= size <= 256
        int size = in.readUnsignedShort();
        long[] frequencies = new long[SIZE];

        for (int i = 0; i < size; ++i) {
            int index = in.readUnsignedByte();
            frequencies[index] = format.readFrom(in);
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

    public boolean isEmpty() {
        return totalCount() == 0;
    }

    public int getSize() {
        return SIZE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrequencyTable other)) return false;
        return Arrays.equals(frequencies, other.frequencies);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(frequencies);
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

    @SuppressWarnings("unused")
    public void dump(DataOutputStream out) throws IOException {
        for (int i = 0; i < getSize(); ++i) {
            long freq = get(i);
            if (freq > Integer.MAX_VALUE) {
                throw new IllegalStateException(
                    "Frequency too large to materialize: " + freq +
                    " (max supported: " + Integer.MAX_VALUE + ")"
                );
            }
            if (freq == 0L) continue;
            out.write(String.valueOf((char)i).repeat((int)freq).getBytes());
        }
    }
}
