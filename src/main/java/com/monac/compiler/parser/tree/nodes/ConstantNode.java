package com.monac.compiler.parser.tree.nodes;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

public class ConstantNode extends Node {

    // Since currently handling only int constants
    private final int value;

    public ConstantNode(NodeType type, int line, int column, int value) {
        super(type, line, column);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
