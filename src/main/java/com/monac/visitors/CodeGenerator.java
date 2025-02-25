package com.monac.visitors;

import com.monac.parser.ast.nodes.expressions.*;
import com.monac.parser.ast.nodes.statements.*;
import com.monac.parser.ast.nodes.structure.FunctionDefinitionNode;
import com.monac.parser.ast.nodes.structure.TranslationUnitNode;
import com.monac.parser.ast.nodes.structure.TypeSpecifierNode;

public class CodeGenerator implements ASTVisitor<Void> {

    private final StringBuilder asm = new StringBuilder();

    @Override
    public Void visit(TranslationUnitNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionDefinitionNode node) {
        return null;
    }

    @Override
    public Void visit(TypeSpecifierNode node) {
        return null;
    }

    @Override
    public Void visit(CompoundStatementNode node) {
        return null;
    }

    @Override
    public Void visit(ReturnStatementNode node) {
        return null;
    }

    @Override
    public Void visit(IfStatementNode node) {
        return null;
    }

    @Override
    public Void visit(WhileStatementNode node) {
        return null;
    }

    @Override
    public Void visit(ForStatementNode node) {
        return null;
    }

    @Override
    public Void visit(IdentifierNode node) {
        return null;
    }

    @Override
    public Void visit(IntegerConstantNode node) {
        return null;
    }

    @Override
    public Void visit(AssignmentExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(BinaryExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(UnaryExpressionNode node) {
        return null;
    }
}
