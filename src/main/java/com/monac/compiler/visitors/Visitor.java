package com.monac.compiler.visitors;

import com.monac.compiler.parser.Node;

public interface Visitor {
    void visit(Node node);
}
