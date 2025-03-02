package com.monac.compiler.parser.rules;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.Parser;

// TODO: Refactor class Constant since by c-lang BNF this should be nonterminal

public class ConstantRule implements Rule {

    @Override
    public Node parse(Parser parser) {
        return IntegerConstant.parse(parser);
    }

}
