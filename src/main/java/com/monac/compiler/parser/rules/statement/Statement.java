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

        // todo; implement parsing of labeled statement
        Node ls = LabeledStatement.parse(parser);
        if (ls != null) return ls;

        // done
        Node es = ExpressionStatement.parse(parser);
        if (es != null) return es;

        // done
        Node cs = CompoundStatement.parse(parser);
        if (cs != null) return cs;

        // todo; implement switch...
        Node ss = SelectionStatement.parse(parser);
        if (ss != null) return ss;

        // done
        Node is = IterationStatement.parse(parser);
        if (is != null) return is;

        // done
        return JumpStatement.parse(parser);
    }

}
