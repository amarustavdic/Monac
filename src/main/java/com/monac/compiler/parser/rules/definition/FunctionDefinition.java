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

        List<Node> children = new ArrayList<>();

        Node declarationSpecifier; // void, char or int
        while ((declarationSpecifier = DeclarationSpecifier.parse(parser)) != null) {
            children.add(declarationSpecifier);
        }

        Node declarator = Declarator.parse(parser);

        if (declarator != null) {
            children.add(declarator);

            Node declaration; // optional
            while ((declaration = Declaration.parse(parser)) != null) {
                children.add(declaration);
            }

            Node compoundStatement = CompoundStatement.parse(parser);
            if (compoundStatement != null) {
                children.add(compoundStatement);

                Node result = new Node(
                        NodeType.FUNCTION_DEFINITION,
                        children.getFirst().getLine(),
                        children.getFirst().getColumn()
                );
                result.setChildren(children);
                result.setLiteral("function");
                return result;
            } else {
                // error handling and sync todo
                return null;
            }
        } else {
            return null;
        }
    }

}
