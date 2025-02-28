package com.monac.parser;


import com.monac.lexer.Token;
import com.monac.visitors.Visitor;

import java.util.List;

public class Node {

    private final NodeType type;
    private List<Node> children;
    private Token token;
    private String value;

    public Node(NodeType type, Token token) {
        this.type = type;
        this.token = token;
    }

    public Node(NodeType type, List<Node> children, String value) {
        this.type = type;
        this.children = children;
        this.value = value;
    }

    public NodeType getType() {
        return type;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Token getToken() {
        return token;
    }

    public String getValue() {
        return value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
