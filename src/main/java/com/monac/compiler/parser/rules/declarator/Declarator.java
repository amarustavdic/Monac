package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class Declarator {

    // <declarator> ::= {<pointer>}? <direct-declarator>

    public static Node parse(Parser parser) {



        Node declarator = new Node(NodeType.DECLARATOR, 0, 0);

        Node directDeclarator = DirectDeclarator.parse(parser);
        if (directDeclarator == null) {
            return null;
        };

        declarator.setChildren(List.of(directDeclarator));
        declarator.setLiteral("<declarator>");
        return declarator;
    }

}
