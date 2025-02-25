package com.monac.parser.ast.nodes;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

public class Identifier implements ASTNode {

    private final String identifier;

    public Identifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }

    public String getIdentifier() {
        return identifier;
    }

}
