package com.monac.compiler.parser.rules.declaration;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.declarator.AbstractDeclarator;
import com.monac.compiler.parser.rules.declarator.Declarator;
import com.monac.compiler.parser.rules.specifier.DeclarationSpecifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class ParameterDeclaration {

    // <parameter-declaration> ::= {<declaration-specifier>}+ <declarator>
    //| {<declaration-specifier>}+ <abstract-declarator>
    //| {<declaration-specifier>}+

    public static Node parse(Parser parser) {

        Node declarationSpecifier = DeclarationSpecifier.parse(parser);

        // Expect declaration specifier
        if (declarationSpecifier == null) {
            parser.addError(null);
            parser.synchronize();
            return null;
        }

        // Just can hope that this won't cause indirect left-recursion.... todo check !!! (if shit brakes ofc)
        Node declarator = Declarator.parse(parser);
        if (declarator != null) {
            Node result = new Node(NodeType.PARAMETER_DECLARATION, 0, 0); // todo later, dc now
            result.setChildren(List.of(declarationSpecifier, declarator));
            return result;
        }

        Node abstractDeclarator = AbstractDeclarator.parse(parser);
        if (abstractDeclarator != null) {
            Node result = new Node(NodeType.PARAMETER_DECLARATION, 0, 0); // todo later, dc now
            result.setChildren(List.of(declarationSpecifier, abstractDeclarator));
            return result;
        }

        return declarationSpecifier;
    }

}
