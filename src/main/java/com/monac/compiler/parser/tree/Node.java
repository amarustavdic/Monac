package com.monac.compiler.parser.tree;

import java.util.List;

public abstract class Node {

    private final NodeType type;

    // Useful for error reporting
    private final int line;
    private final int column;

    private Node parent;
    private List<Node> children;

    public Node(NodeType type, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
    }

    // Getters

    public NodeType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    // Setters

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

}
