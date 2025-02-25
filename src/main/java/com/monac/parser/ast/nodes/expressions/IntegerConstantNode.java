package com.monac.parser.ast.nodes.expressions;

import com.monac.visitors.ASTVisitor;

public class IntegerConstantNode extends ExpressionNode {

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
