package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class Statement {

    // <statement> ::= <labeled-statement>
    //| <expression-statement>
    //| <compound-statement>
    //| <selection-statement>
    //| <iteration-statement>
    //| <jump-statement>

    public static Node parse(Parser parser) {

        return ExpressionStatement.parse(parser);
    }
}
