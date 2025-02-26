package com.monac.parser.ast.nodes;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

public class Expression implements ASTNode {
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
