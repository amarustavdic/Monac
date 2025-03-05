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

        Node cs = CompoundStatement.parse(parser);
        if (cs != null) return cs;

        Node ss = SelectionStatement.parse(parser);
        if (ss != null) return ss;

        Node is = IterationStatement.parse(parser);
        if (is != null) return is;

        Node js = JumpStatement.parse(parser);
        if (js != null) return js;

        return ExpressionStatement.parse(parser);
    }

}
