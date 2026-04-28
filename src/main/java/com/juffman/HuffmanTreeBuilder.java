package com.juffman;

import java.util.ArrayList;
import java.util.List;

public class HuffmanTreeBuilder {

    private HuffmanTreeBuilder() { }

    public static HuffmanNode build(FrequencyTable table) {
        List<HuffmanNode> nodes = new ArrayList<>(table.getSize());
        for (int i = 0; i < table.getSize(); ++i) {
            long freq = table.get(i);
            if (freq == 0L) continue;
            nodes.add(new HuffmanNode((byte) i, freq));
        }
        nodes.sort((a, b) -> (int)(a.count() - b.count()));

        while(nodes.size() > 1) {
            HuffmanNode first = nodes.removeFirst();
            HuffmanNode second = nodes.removeFirst();
            nodes.add(new HuffmanNode(
                null, first.count() + second.count(), first, second));
            nodes.sort((a, b) -> (int)(a.count() - b.count()));
        }

        return nodes.getFirst();
    }
}
