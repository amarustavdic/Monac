package com.monac.compiler.parser.grammar.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class ConstantExpression {

    public static Node parse(Parser parser) {
        return ConditionalExpression.parse(parser);
    }

}
