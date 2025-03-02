package com.monac.compiler.parser.tree.nodes.expression;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;
import com.monac.compiler.visitors.Visitor;

public class MultiplicativeExpressionNode extends Node {

    public MultiplicativeExpressionNode(NodeType type, int line, int column) {
        super(type, line, column);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
