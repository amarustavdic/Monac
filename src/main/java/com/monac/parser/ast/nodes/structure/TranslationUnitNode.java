package com.monac.parser.ast.nodes.structure;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

import java.util.List;

// Represents the root of the AST, containing multiple functions
public class TranslationUnitNode implements ASTNode {

    public final List<FunctionDefinitionNode> functions;

    public TranslationUnitNode(List<FunctionDefinitionNode> functions) {
        this.functions = functions;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
