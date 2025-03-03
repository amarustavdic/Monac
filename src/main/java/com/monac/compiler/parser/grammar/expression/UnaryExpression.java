package com.monac.compiler.parser.grammar.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class UnaryExpression {

    public static Node parse(Parser parser) {
        return PostfixExpression.parse(parser);
    }

}
