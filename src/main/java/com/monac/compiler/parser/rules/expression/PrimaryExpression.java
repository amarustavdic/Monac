package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.constant.Constant;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

public final class PrimaryExpression {

    // <primary-expression> ::= <identifier>
    //                        | <constant>
    //                        | <string>
    //                        | ( <expression> )

    public static Node parse(Parser parser) {
        if (parser.match(TokenType.IDENTIFIER, TokenType.STRING)) {
            Token token = parser.previous();
            Node node = new Node(NodeType.PRIMARY_EXPRESSION, token.getLine(), token.getColumn());
            node.setLiteral(token.getLexeme());
            return node;
        } else {
            if (parser.match(TokenType.LPAREN)) {

                Node node = Expression.parse(parser);

                if (node == null || !parser.match(TokenType.RPAREN)) {
                    // TODO: Should handle error here and sync parser in caller
                    return null;
                } else {
                    return node;
                }
            } else {
                return Constant.parse(parser);
            }
        }
    }

}
