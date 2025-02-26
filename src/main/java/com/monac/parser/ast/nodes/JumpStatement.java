package com.monac.parser.ast.nodes;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

public class JumpStatement implements ASTNode {

    private final String keyword;
    private ASTNode identifier;

    public JumpStatement(String keyword, ASTNode identifier) {
        this.keyword = keyword;
        this.identifier = identifier;
    }

    public JumpStatement(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }

    public String getKeyword() {
        return keyword;
    }

    public ASTNode getIdentifier() {
        return identifier;
    }

}
