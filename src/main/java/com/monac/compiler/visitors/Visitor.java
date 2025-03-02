package com.monac.compiler.visitors;

import com.monac.compiler.parser.tree.Node;

public interface Visitor {
    void visit(Node node);
}
