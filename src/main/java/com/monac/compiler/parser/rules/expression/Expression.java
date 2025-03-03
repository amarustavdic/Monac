package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class Expression {


    // <expression> ::= <assignment-expression>
    //  | <expression> , <assignment-expression>

    // without left recursion

    // <expression> ::= <assignment-expression> <expression'>
    //
    // <expression'> ::= , <assignment-expression>
    //                 | epsilon

    public static Node parse(Parser parser) {
        Node left = AssignmentExpression.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    public static Node parsePrime(Parser parser, Node left) {
        return left;
    }

}
