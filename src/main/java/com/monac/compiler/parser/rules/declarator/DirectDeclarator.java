package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.ParameterTypeList;
import com.monac.compiler.parser.rules.other.Identifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public class DirectDeclarator {

    // <direct-declarator> ::= <identifier>
    //| ( <declarator> )
    //| <direct-declarator> [ {<constant-expression>}? ]
    //| <direct-declarator> ( <parameter-type-list> )
    //| <direct-declarator> ( {<identifier>}* )

    // well but there is a problem ofc, left-recursion....

    // well this bellow could be right I am not sure if I did it correctly

    // <direct-declarator> ::= <identifier> <direct-declarator'> | ( <declarator> )
    //
    // <direct-declarator'> ::= [ {<constant-expression>}? ]
    //                        | ( <parameter-type-list> )
    //                        | ( {<identifier>}* )
    //                        | ɛ

    public static Node parse(Parser parser) {

        // Handle ( <declarator> )
        if (parser.match(TokenType.LPAREN)) {
            Node declarator = Declarator.parse(parser);
            if (declarator != null) {
                if (parser.match(TokenType.RPAREN)) {
                    return declarator;
                } else {
                    // todo error and sync
                    return null;
                }
            } else {
                // todo error sync
                return null;
            }
        }

        // The rest :P
        Node identifier = Identifier.parse(parser);
        if (identifier == null) return null;
        return parsePrime(parser, identifier);
    }

    public static Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.LBRACKET, TokenType.LPAREN)) {
            Token token = parser.previous();

            if (token.getType() == TokenType.LBRACKET) {

            }

            if (token.getType() == TokenType.LPAREN) {
                Token startToken = parser.previous();

                Node parameterTypeList = ParameterTypeList.parse(parser);
                if (parameterTypeList != null) {
                    if (parser.match(TokenType.RPAREN)) {
                        Node result = new Node(NodeType.DIRECT_DECLARATOR, startToken.getLine(), startToken.getColumn());
                        result.setLiteral("(parameter-type-list)");
                        result.setChildren(List.of(left, parameterTypeList));
                        left = result;
                    } else {
                        // todo handle error and parser sync
                        return null;
                    }
                } else {
                    List<Node> children = new ArrayList<>();
                    children.add(left);

                    Node identifier;
                    while ((identifier = Identifier.parse(parser)) != null) {
                        children.add(identifier);
                    }

                    if (parser.match(TokenType.RPAREN)) {
                        Node result = new Node(NodeType.DIRECT_DECLARATOR, startToken.getLine(), startToken.getColumn());
                        result.setLiteral("(...)");
                        result.setChildren(children);

                        left = result;
                    } else {
                        // todo handle error and sync
                        return null;
                    }
                }
            }
        }
        return left;
    }

}
