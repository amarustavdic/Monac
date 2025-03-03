package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;
import java.util.ArrayList;
import java.util.List;

public final class SelectionStatement {

    // <selection-statement> ::= if ( <expression> ) <statement>
    //| if ( <expression> ) <statement> else <statement>
    //| switch ( <expression> ) <statement>

    public static Node parse(Parser parser) {

        // Try parsing if
        if (parser.match(TokenType.IF)) {
            Token ifToken = parser.previous();
            if (parser.match(TokenType.LEFT_PARENTHESIS)) {
                Node expr = Expression.parse(parser);
                if (expr != null) {
                    if (parser.match(TokenType.RIGHT_PARENTHESIS)) {
                        Node stmt = Statement.parse(parser);
                        if (stmt != null) {

                            Node result = new Node(NodeType.SELECTION_STATEMENT, ifToken.getLine(), ifToken.getColumn());
                            result.setLiteral(ifToken.getLexeme());
                            result.setChildren(List.of(expr, stmt));
                            return result;

                        } else {
                            Token actual = parser.peek();
                            parser.addError(new ParserException(
                                    "",
                                    actual.getLine(),
                                    actual.getColumn(),
                                    actual.getLexeme(),
                                    "",
                                    ""
                            ));
                            parser.synchronize();
                        }
                    } else {
                        Token actual = parser.peek();
                        parser.addError(new ParserException(
                                "",
                                actual.getLine(),
                                actual.getColumn(),
                                actual.getLexeme(),
                                "",
                                ""
                        ));
                        parser.synchronize();
                    }
                } else {
                    Token actual = parser.peek();
                    parser.addError(new ParserException(
                            "",
                            actual.getLine(),
                            actual.getColumn(),
                            actual.getLexeme(),
                            "",
                            ""
                    ));
                    parser.synchronize();
                }
            } else {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "",
                        actual.getLine(),
                        actual.getColumn(),
                        actual.getLexeme(),
                        "",
                        ""
                ));
                parser.synchronize();
            }
        }


        return null;
    }
}
