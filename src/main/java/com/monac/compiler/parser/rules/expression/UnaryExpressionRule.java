package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.Rule;
import com.monac.compiler.parser.tree.Node;

public class UnaryExpressionRule implements Rule {

    private final Rule terminal;

    public UnaryExpressionRule(Rule terminal) {
        this.terminal = terminal;
    }

    @Override
    public Node parse(Parser parser) {
        return terminal.parse(parser);
    }

}
