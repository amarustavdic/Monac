package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class CastExpression {

    public static Node parse(Parser parser) {
        return UnaryExpression.parse(parser);
    }

}
