package com.monac.parser.ast.nodes;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

public class UnaryOperator implements ASTNode {

    private final String operator;

    public UnaryOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }

    public String getOperator() {
        return operator;
    }

}
