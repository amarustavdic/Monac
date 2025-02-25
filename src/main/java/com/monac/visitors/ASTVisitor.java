package com.monac.visitors;

import com.monac.parser.ast.nodes.expressions.*;
import com.monac.parser.ast.nodes.statements.*;
import com.monac.parser.ast.nodes.structure.FunctionDefinitionNode;
import com.monac.parser.ast.nodes.structure.TranslationUnitNode;
import com.monac.parser.ast.nodes.structure.TypeSpecifierNode;

public interface ASTVisitor<R> {
    R visit(TranslationUnitNode node);
    R visit(FunctionDefinitionNode node);
    R visit(TypeSpecifierNode node);
    R visit(CompoundStatementNode node);
    R visit(ReturnStatementNode node);
    R visit(IfStatementNode node);
    R visit(WhileStatementNode node);
    R visit(ForStatementNode node);
    R visit(IdentifierNode node);
    R visit(IntegerConstantNode node);
    R visit(AssignmentExpressionNode node);
    R visit(BinaryExpressionNode node);
    R visit(UnaryExpressionNode node);
}
