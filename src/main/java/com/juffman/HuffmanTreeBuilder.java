package com.juffman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HuffmanTreeBuilder {

    private HuffmanTreeBuilder() { }

    //TODO: Performance Improvement:
    //  - No fully sort after each step
    //  - Removing first in List is costly
    public static HuffmanNode build(FrequencyTable table) {
        if (table.isEmpty()) throw new IllegalArgumentException(
            "Cannot build Huffman tree from empty table");

        List<HuffmanNode> nodes = new ArrayList<>(table.getSize());
        for (int i = 0; i < table.getSize(); ++i) {
            long freq = table.get(i);
            if (freq == 0L) continue;
            nodes.add(new HuffmanNode((byte) i, freq));
        }
        nodes.sort(Comparator.comparingLong(HuffmanNode::count));

        while(nodes.size() > 1) {
            HuffmanNode first = nodes.removeFirst();
            HuffmanNode second = nodes.removeFirst();
            nodes.add(new HuffmanNode(
                null, first.count() + second.count(), first, second));
            nodes.sort(Comparator.comparingLong(HuffmanNode::count));
        }

        return nodes.getFirst();
    }
}
