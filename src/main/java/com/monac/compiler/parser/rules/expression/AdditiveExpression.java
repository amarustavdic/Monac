package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class AdditiveExpression {

    //<additive-expression> ::= <multiplicative-expression>
    //| <additive-expression> + <multiplicative-expression>
    //| <additive-expression> - <multiplicative-expression>

    // Converted to right recursion

    //<additive-expression> ::= <multiplicative-expression> <additive-expression'>
    //
    //<additive-expression'> ::= + <multiplicative-expression>
    //                         | - <multiplicative-expression>
    //                         | ε

    // <multiplicative-expression> is basically terminal

    public static Node parse(Parser parser) {
        Node left = MultiplicativeExpression.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    private static Node parsePrime(Parser parser, Node left) {
        while (parser.match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = parser.previous();

            Node right = MultiplicativeExpression.parse(parser);
            if (right == null) return left;

            Node node = new Node(NodeType.ADDITIVE_EXPRESSION, operator.getLine(), operator.getColumn());
            node.setLiteral(operator.getLexeme());

            List<Node> children = new ArrayList<>();
            children.add(left);
            children.add(right);
            node.setChildren(children);

            left = node;
        }

        return left;
    }

}
