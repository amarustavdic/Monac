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

    public static Node parse(Parser parser) throws Exception {

        try {
            Node labeledStatement = LabeledStatement.parse(parser);
            if (labeledStatement != null) return labeledStatement;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return ExpressionStatement.parse(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Node e = ExpressionStatement.parse(parser);
        if (e != null) return e;

        Node c = CompoundStatement.parse(parser);
        if (c != null) return c;

        Node s = SelectionStatement.parse(parser);
        if (s != null) return s;

        Node i = IterationStatement.parse(parser);
        if (i != null) return i;

        Node j = JumpStatement.parse(parser);
        if (j != null) return j;

        return null; // Handle error or unexpected input
    }
}
