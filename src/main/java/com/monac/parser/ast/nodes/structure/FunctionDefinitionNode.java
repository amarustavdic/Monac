package com.monac.parser.ast.nodes.structure;

import com.monac.parser.ast.ASTNode;
import com.monac.visitors.ASTVisitor;

// Represents a function definition (e.g., int main() { ... })
public class FunctionDefinitionNode implements ASTNode {



    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
