package com.juffman;

import java.util.Objects;

class HuffmanNode {
    private Byte value;
    private long count;

    private HuffmanNode left;
    private HuffmanNode right;

    public HuffmanNode(Byte value, long count, HuffmanNode left, HuffmanNode right) {
        this.value = value;
        this.count = count;
        this.left = left;
        this.right = right;
    }

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

    public Byte getValue() {
        return value;
    }

    public int getIndex() {
        if (value == null) throw new IllegalStateException(
            "Cannot compute index: value is null.");
        return Byte.toUnsignedInt(value);
    }

    public long getCount() {
        return count;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HuffmanNode)) return false;
        HuffmanNode other = (HuffmanNode)o;
        return count == other.count
            && Objects.equals(value, other.getValue())
            && Objects.equals(left, other.getLeft())
            && Objects.equals(right, other.getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, count, left, right);
    }

    @Override
    public String toString() {
        return toStringIndent(0);
    }

    public String toStringIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(indent))
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

