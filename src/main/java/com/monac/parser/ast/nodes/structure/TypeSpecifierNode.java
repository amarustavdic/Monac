package com.monac.parser.ast.nodes.structure;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

public class TypeSpecifierNode implements ASTNode {
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
