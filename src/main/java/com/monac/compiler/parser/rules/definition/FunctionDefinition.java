package com.monac.compiler.parser.rules.definition;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.declaration.Declaration;
import com.monac.compiler.parser.rules.declarator.Declarator;
import com.monac.compiler.parser.rules.specifier.DeclarationSpecifier;
import com.monac.compiler.parser.rules.statement.CompoundStatement;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class FunctionDefinition {

    // <function-definition> ::= {<declaration-specifier>}* <declarator> {<declaration>}* <compound-statement>

    public static Node parse(Parser parser) {

        List<Node> children = declarationSpecifiers(parser); // {<declaration-specifier>}* zero or more





        Node declarator = Declarator.parse(parser); // <declarator>
        if (declarator == null) return null;
        children.add(declarator);

        children.addAll(declarations(parser)); // {<declaration>}*

        Node compoundStatement = CompoundStatement.parse(parser); // <compound-statement>
        if (compoundStatement == null) {
            parser.addError(null); // todo
            parser.synchronize();
            return null;
        }

        Node result = new Node(NodeType.FUNCTION_DEFINITION, children.getFirst().getLine(), children.getLast().getColumn());
        result.setChildren(children);
        result.setLiteral("<function-definition>");
        return result;
    }

    private static List<Node> declarationSpecifiers(Parser parser) {
        List<Node> declarationSpecifiers = new ArrayList<>();
        Node declarationSpecifier;
        while ((declarationSpecifier = DeclarationSpecifier.parse(parser)) != null) {
            declarationSpecifiers.add(declarationSpecifier);
        }
        return declarationSpecifiers;
    }

    private static List<Node> declarations(Parser parser) {
        List<Node> declarations = new ArrayList<>();
        Node declaration;
        while ((declaration = Declaration.parse(parser)) != null) {
            declarations.add(declaration);
        }
        return declarations;
    }

}
