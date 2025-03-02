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
        Node left = terminal.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    private Node parsePrime(Parser parser, Node left) {
//
//        while (parser.match(TokenType.LBRACKET, TokenType.LPAREN, TokenType.DOT,
//                TokenType.ARROW, TokenType.INCREMENT, TokenType.DECREMENT
//        )) {
//            // TODO: To be continued...
//        }

        return left;
    }

}
