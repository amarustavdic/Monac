package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class ExclusiveOrExpression {

    // <exclusive-or-expression> ::= <and-expression>
    //| <exclusive-or-expression> ^ <and-expression>

    // fixed left recursion bellow

    // <exclusive-or-expression> ::= <and-expression> <exclusive-or-expression'>
    //
    // <exclusive-or-expression'> ::= ^ <and-expression>
    //                              | epsilon

    public static Node parse(Parser parser) {
        Node left = null;

        return null;
    }

    private static Node parsePrime(Parser parser, Node left) {
        return null;
    }

}
