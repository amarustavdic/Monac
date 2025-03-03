package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class CastExpression {

    // <cast-expression> ::= <unary-expression>
    //| ( <type-name> ) <cast-expression>

    public static Node parse(Parser parser) {

        try {
            return UnaryExpression.parse(parser);

        } catch (Exception e) {
            return null;
        }
    }

}
