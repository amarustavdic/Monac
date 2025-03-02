package com.monac.compiler.parser.tree.nodes.expression;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;
import com.monac.compiler.visitors.Visitor;

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

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
