package com.monac.parser.ast.nodes.statements;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

import java.util.List;

public class CompoundStatementNode implements ASTNode {

    public final List<ASTNode> statements;

    public CompoundStatementNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
