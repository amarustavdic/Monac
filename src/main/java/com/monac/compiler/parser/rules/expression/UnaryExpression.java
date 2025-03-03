package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.operator.UnaryOperator;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class UnaryExpression {

    // <unary-expression> ::= <postfix-expression>
    //| ++ <unary-expression>
    //| -- <unary-expression>
    //| <unary-operator> <cast-expression>
    //| sizeof <unary-expression>
    //| sizeof <type-name>

    public static Node parse(Parser parser) {

        // Handle increment and decrement
        if (parser.match(TokenType.INCREMENT, TokenType.DECREMENT)) {
            Token operator = parser.previous();
            Node unaryExpression = UnaryExpression.parse(parser);

            // Expect unary expression
            if (unaryExpression != null) {
                Node result = new Node(NodeType.UNARY_EXPRESSION, operator.getLine(), operator.getColumn());
                result.setChildren(List.of(unaryExpression));
                result.setLiteral(operator.getLexeme());
                return result;
            } else {
                parser.addError(new ParserException(
                        "Expected unary expression after increment/decrement operator.",
                        0, 0, "", "" // todo figure out how to handle this properly
                ));
                parser.synchronize();
            }
        }

        // Handle unary operator followed by cast expression
        Node unaryOperator = UnaryOperator.parse(parser);
        if (unaryOperator != null) {
            // todo; handle unary operator
        }

        // Handle size of
        if (parser.match(TokenType.SIZEOF)) {
            // todo; work for later
        }

        // Return terminal case
        return PostfixExpression.parse(parser);
    }

}
