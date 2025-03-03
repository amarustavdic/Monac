package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class AndExpression {

    // <and-expression> ::= <equality-expression>
    //| <and-expression> & <equality-expression>

    // fix left recursion first

    public static Node parse(Parser parser) {
        return null;
    }

}
