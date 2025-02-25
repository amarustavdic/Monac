package com.monac.parser.ast.nodes.statements;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

public class CompoundStatementNode implements ASTNode {
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
