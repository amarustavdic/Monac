package com.monac.parser;


import com.monac.lexer.Token;
import com.monac.visitors.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final NodeType type;
    private List<Node> children;
    private Token token;

    public Node(NodeType type, Token token) {
        this.type = type;
        this.token = token;
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

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
