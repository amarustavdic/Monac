package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

/**
 * <pre>{@code
 * <statement> ::= <labeled-statement>
 * | <expression-statement>
 * | <compound-statement>
 * | <selection-statement>
 * | <iteration-statement>
 * | <jump-statement>
 * }</pre>
 */
public final class Statement {

    public static Node parse(Parser parser) {

        Node ls = LabeledStatement.parse(parser);
        if (ls != null) return ls;

        Node es = ExpressionStatement.parse(parser);
        if (es != null) return es;

        Node cs = CompoundStatement.parse(parser);
        if (cs != null) return cs;

        Node ss = SelectionStatement.parse(parser);
        if (ss != null) return ss;

        Node is = IterationStatement.parse(parser);
        if (is != null) return is;

        return JumpStatement.parse(parser);
    }

}
