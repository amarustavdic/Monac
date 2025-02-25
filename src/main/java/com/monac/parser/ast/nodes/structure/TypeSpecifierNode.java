package com.monac.parser.ast.nodes.structure;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

public class TypeSpecifierNode implements ASTNode {
    public final String type;

    public TypeSpecifierNode(String type) {
        this.type = type;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
