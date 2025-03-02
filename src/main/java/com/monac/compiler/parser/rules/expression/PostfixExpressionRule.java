package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.Rule;
import com.monac.compiler.parser.tree.Node;

public class PostfixExpressionRule implements Rule {

    private final Rule terminal;

    public PostfixExpressionRule(Rule terminal) {
        this.terminal = terminal;
    }

    //<postfix-expression> ::= <primary-expression>
    //                       | <postfix-expression> [ <expression> ]
    //                       | <postfix-expression> ( {<assignment-expression>}* )
    //                       | <postfix-expression> . <identifier>
    //                       | <postfix-expression> -> <identifier>
    //                       | <postfix-expression> ++
    //                       | <postfix-expression> --

    // In order to implement above rule defined in BNF in recursive descent parser
    // I have to get rid of left recursion first...

    //<postfix-expression> ::= <primary-expression> <postfix-expression'>
    //
    //<postfix-expression'> ::= [ <expression> ] <postfix-expression'>
    //    | ( {<assignment-expression>}* ) <postfix-expression'>
    //    | . <identifier> <postfix-expression'>
    //    | -> <identifier> <postfix-expression'>
    //    | ++ <postfix-expression'>
    //    | -- <postfix-expression'>
    //    | ε

    // Came up with this, well I hope I did it right

    // -----------------------------------------------------------------------
    //              FOR NOW HANDLING ONLY INCREMENT AND DECREMENT
    // -----------------------------------------------------------------------

    @Override
    public Node parse(Parser parser) {

        // Probably would be nice to have rules implement static parse method instead...

        return null;
    }

    private Node parsePrime(Parser parser) {
        return null;
    }

}
