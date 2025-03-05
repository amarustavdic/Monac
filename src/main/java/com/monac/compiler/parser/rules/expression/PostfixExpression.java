package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

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

    public static Node parse(Parser parser) {
        Node primaryExpression = PrimaryExpression.parse(parser);
        if (primaryExpression == null) return null;
        return parsePrime(parser, primaryExpression);
    }

    private static Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.LPAREN)) {
            List<Node> assignmentExpressions = new ArrayList<>();

            // this one is optional
            Node assignmentExpression;
            while ((assignmentExpression = AssignmentExpression.parse(parser)) != null) {
                assignmentExpressions.add(assignmentExpression);
            }

            if (!parser.match(TokenType.RPAREN)) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "error message, from postfix expression",
                        actual.getLine(),
                        actual.getColumn(),
                        actual.getLexeme(),
                        "",
                        ""
                ));
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.POSTFIX_EXPRESSION, left.getLine(), left.getColumn());
            result.setChildren(assignmentExpressions);

            left = result;
        }
        return left;
    }

}
