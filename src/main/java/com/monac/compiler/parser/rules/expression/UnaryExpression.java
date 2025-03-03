package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class UnaryExpression {

    // <unary-expression> ::= <postfix-expression>
    //| ++ <unary-expression>
    //| -- <unary-expression>
    //| <unary-operator> <cast-expression> TODO: here is left-recursion to fix
    //| sizeof <unary-expression>
    //| sizeof <type-name>

    public static Node parse(Parser parser) throws Exception {

        if (parser.match(TokenType.INCREMENT, TokenType.DECREMENT)) {
            Token operator = parser.previous();
            Node unaryExpression = UnaryExpression.parse(parser);
            if (unaryExpression != null) {
                Node result = new Node(NodeType.UNARY_EXPRESSION, operator.getLine(), operator.getColumn());
                result.setChildren(List.of(unaryExpression));
                result.setLiteral(operator.getLexeme());
                return result;
            } else {
                throw new Exception("Expected unary expression after '" + operator.getLexeme() + "'");
            }
        }

        return PostfixExpression.parse(parser);
    }

}
