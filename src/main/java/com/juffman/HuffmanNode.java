package com.juffman;

public record HuffmanNode(Byte value, long count, HuffmanNode left, HuffmanNode right) {

    public HuffmanNode(Byte value, long count) {
        this(value, count, null, null);
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }

    public boolean isLetterNode() {
        return value != null;
    }

    public int getIndex() {
        if (value == null) throw new IllegalStateException(
                "Cannot compute index: value is null.");
        return Byte.toUnsignedInt(value);
    }

    @Override
    public String toString() {
        return toStringIndent(0);
    }

    public String toStringIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.repeat(" ", indent)
            .append(nodeToString())
            .append("\n");

        if (left != null) sb.append(left.toStringIndent(indent + 2));
        if (right != null) sb.append(right.toStringIndent(indent + 2));

        return sb.toString();
    }

    private String nodeToString() {
        if (value == null) return String.valueOf(count);

        byte v = value;

        if (v == 32) return "' ' (" + count + ")";
        if (v > 32 && v <= 126) return ((char) v) + " (" + count + ")";

        return v + " (" + count + ")";
    }
}
