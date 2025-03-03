package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.Arrays;

public final class LogicalAndExpression {

    // <logical-and-expression> ::= <inclusive-or-expression>
    //| <logical-and-expression> && <inclusive-or-expression>

    public static Node parse(Parser parser) {
        Node left = InclusiveOrExpression.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    public static Node parsePrime(Parser parser, Node left) {
        while (parser.match(TokenType.LAND)) {
            Token operator = parser.previous();
            Node right = InclusiveOrExpression.parse(parser);

            if (right == null) {
                Token actual = parser.peek();
                // TODO: Make nice error message
                parser.addError(null);
                parser.synchronize();
            }

            Node result = new Node(NodeType.LOGICAL_AND_EXPRESSION, operator.getLine(), operator.getColumn());
            result.setLiteral(operator.getLexeme());
            result.setChildren(Arrays.asList(left, right));

            left = result;
        }
        return left;
    }

}
