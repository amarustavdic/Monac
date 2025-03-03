package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class MultiplicativeExpression {

    public static Node parse(Parser parser) {
        Node left = CastExpression.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    private static Node parsePrime(Parser parser, Node left) {
        while (parser.match(TokenType.MUL, TokenType.DIV, TokenType.MOD)) {
            Token operator = parser.previous();
            Node right = CastExpression.parse(parser);

            if (right == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Invalid multiplicative expression. Expected a valid operand after '" + actual.getLexeme() + "'.",
                        actual.getLine(),
                        actual.getColumn(),
                        actual.getLexeme(),
                        "valid cast expression.",
                        "Ensure that an expression or a valid type cast follows the operator ('*', '/', '%')."
                ));
                parser.synchronize();
            }

            Node node = new Node(NodeType.MULTIPLICATIVE_EXPRESSION, operator.getLine(), operator.getColumn());
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