package com.monac.visitors;

import com.monac.parser.Node;

public interface Visitor {
    void visit(Node node);
}
