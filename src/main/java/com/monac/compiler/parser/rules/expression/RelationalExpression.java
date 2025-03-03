package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.Arrays;
import java.util.List;

public final class RelationalExpression {

    public static Node parse(Parser parser) {
        Node left = ShiftExpression.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    private static Node parsePrime(Parser parser, Node left) {
        while (parser.match(TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE)) {
            Token operator = parser.previous();
            Node right = ShiftExpression.parse(parser);

            if (right == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Invalid relational expression. Expected a valid operand after '" + actual.getLexeme() + "'.",
                        actual.getLine(),
                        actual.getColumn(),
                        actual.getLexeme(),
                        "Valid shift expression",
                        "Ensure that a valid shift expression follows the relational operator ('<', '>', '<=', '>=' )."
                ));
                parser.synchronize();
            }

            Node result = new Node(NodeType.RELATIONAL_EXPRESSION, operator.getLine(), operator.getColumn());
            result.setLiteral(operator.getLexeme());
            result.setChildren(Arrays.asList(left, right));

            left = result;
        }
        return left;
    }
}
