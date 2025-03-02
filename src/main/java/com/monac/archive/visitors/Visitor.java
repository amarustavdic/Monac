package com.monac.archive.visitors;

import com.monac.archive.parser.Node;

public interface Visitor {
    void visit(Node node);
}
