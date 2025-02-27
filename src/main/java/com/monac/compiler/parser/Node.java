package com.monac.compiler.parser;

import com.monac.compiler.visitors.Visitor;

import java.util.List;

public class Node {

    private final NodeType type;
    private final List<Node> children;
    private final String value;

    public Node(NodeType type, List<Node> children, String value) {
        this.type = type;
        this.children = children;
        this.value = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public NodeType getType() {
        return type;
    }

    public List<Node> getChildren() {
        return children;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + (value != null ? ": " + value : "");
    }

}
