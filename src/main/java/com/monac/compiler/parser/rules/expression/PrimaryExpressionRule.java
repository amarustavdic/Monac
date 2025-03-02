package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.Rule;
import com.monac.compiler.parser.tree.Node;

public class PrimaryExpressionRule implements Rule {

    // <primary-expression> ::= <identifier>
    //                        | <constant>
    //                        | <string>
    //                        | ( <expression> )


    @Override
    public Node parse(Parser parser) {
        return new ConstantRule().parse(parser);
    }

}
