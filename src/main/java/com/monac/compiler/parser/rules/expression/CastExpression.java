package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class CastExpression {

    // <cast-expression> ::= <unary-expression>
    //| ( <type-name> ) <cast-expression>

    public static Node parse(Parser parser) {

        if (parser.match(TokenType.LEFT_PARENTHESIS)) {
            // todo; later
        }

        return UnaryExpression.parse(parser);
    }

}
