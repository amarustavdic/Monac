package com.monac.compiler.parser.rules.list;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.declaration.ParameterDeclaration;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class ParameterList {

    // <parameter-list> ::= <parameter-declaration>
    //| <parameter-list> , <parameter-declaration>

    // -------------------- left fucking recursion....

    // <parameter-list> ::= <parameter-declaration> <parameter-list'>
    //
    // <parameter-list'> ::= , <parameter-declaration>
    //                     | epsilon

    public static Node parse(Parser parser) {
        Node parameterDeclaration = ParameterDeclaration.parse(parser);
        if (parameterDeclaration == null) return null;
        return parsePrime(parser, parameterDeclaration);
    }

    public static Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.COMMA)) {
            Node parameterDeclaration = ParameterDeclaration.parse(parser);

            // Expect parameter declaration now
            if (parameterDeclaration == null) {
                parser.addError(null);
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.PARAMETER_LIST, 0, 0);
            result.setChildren(List.of(left, parameterDeclaration));

            left = result;
        }
        return left;
    }

}
