package com.monac.compiler.parser.grammar.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class EqualityExpression {

    // <equality-expression> ::= <relational-expression>
    //| <equality-expression> == <relational-expression>
    //| <equality-expression> != <relational-expression>

    // fix left recursion

    public static Node parse(Parser parser) {
        return null;
    }

}
