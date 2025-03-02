package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.Nonterminal;
import com.monac.compiler.parser.rules.Terminal;
import com.monac.compiler.parser.tree.Node;

public class PrimaryExpressionRule implements Nonterminal, Terminal {

    // <primary-expression> ::= <identifier>
    //                        | <constant>
    //                        | <string>
    //                        | ( <expression> )

    @Override
    public Node parse(Parser parser, Terminal terminal) {

        if (terminal instanceof ConstantRule c) {

            // Since for now I am handling only <constant>
            return c.parse(parser);
        }
        return null;
    }

    @Override
    public Node parse(Parser parser) {

        // This class has to be nonterminal, but in order to recursively
        // express ast, using rules, has to be also marked as nonterminal
        // in order to be able to pass it through

        // ==> thus figure out what to do in that case, leave this method empty?

        return null;
    }

}
