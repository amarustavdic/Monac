package com.monac.parser.ast.nodes.structure;

import com.monac.parser.ast.ASTNode;
import com.monac.parser.ast.nodes.statements.CompoundStatementNode;
import com.monac.visitors.ASTVisitor;

// Represents a function definition (e.g., int main() { ... })
public class FunctionDefinitionNode implements ASTNode {

    public final TypeSpecifierNode returnType;
    public final String name;
    public final CompoundStatementNode body;

    public FunctionDefinitionNode(TypeSpecifierNode returnType, String name, CompoundStatementNode body) {
        this.returnType = returnType;
        this.name = name;
        this.body = body;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return null;
    }
}
