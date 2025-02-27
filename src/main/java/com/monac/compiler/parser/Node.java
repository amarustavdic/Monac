package com.monac.compiler.parser;

import com.monac.compiler.visitors.Visitor;

import java.util.List;

public class Node {

    private final Type type;
    private final List<Node> children;

    public Node(Type type, List<Node> children) {
        this.type = type;
        this.children = children;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Type getType() {
        return type;
    }

    public List<Node> getChildren() {
        return children;
    }

}
