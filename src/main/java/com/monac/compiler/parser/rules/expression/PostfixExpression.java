package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;


/**
 * A utility class for parsing postfix expressions in the source code.
 * <p>
 * A postfix expression can take multiple forms, including:
 * <ul>
 *   <li>A primary expression</li>
 *   <li>An indexed expression (array subscript)</li>
 *   <li>A function call</li>
 *   <li>Member access using dot (.) or arrow (->)</li>
 *   <li>Post-increment (++) and post-decrement (--)</li>
 * </ul>
 * <p>
 * Since the original BNF grammar contains left recursion, it is rewritten
 * to be suitable for recursive descent parsing. However, for now, only
 * increment (++) and decrement (--) operations are handled.
 * </p>
 */
public final class PostfixExpression {

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

    public static Node parse(Parser parser) {
        try {
            return PrimaryExpression.parse(parser);
        } catch (Exception e) {
            parser.synchronize();
            return null;
        }
    }

    private static Node parsePrime(Parser parser, Node left) {
        return left;
    }

}
