package com.monac.compiler.parser.rules.declaration;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.declarator.InitDeclarator;
import com.monac.compiler.parser.rules.specifier.DeclarationSpecifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class Declaration {

    // <declaration> ::= {<declaration-specifier>}+ {<init-declarator>}* ;

    public static Node parse(Parser parser) {

        List<Node> declarationSpecifiers = new ArrayList<>();
        Node declarationSpecifier;
        while ((declarationSpecifier = DeclarationSpecifier.parse(parser)) != null) {
            declarationSpecifiers.add(declarationSpecifier);
        }

        if (declarationSpecifiers.isEmpty()) {
            return null;
        }

        Node initDeclarator;
        while ((initDeclarator = InitDeclarator.parse(parser)) != null) {
            declarationSpecifiers.add(initDeclarator);
        }

        if (parser.match(TokenType.SEMICOLON)) {
            Node result = new Node(
                    NodeType.DECLARATION,
                    declarationSpecifiers.getFirst().getLine(),
                    declarationSpecifiers.getFirst().getColumn()
            );
            result.setChildren(declarationSpecifiers);
            return result;
        }

        return null;
    }

}
