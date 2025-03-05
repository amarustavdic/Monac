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

public final class IterationStatement {

    // <iteration-statement> ::= while ( <expression> ) <statement>
    //| do <statement> while ( <expression> ) ;
    //| for ( {<expression>}? ; {<expression>}? ; {<expression>}? ) <statement>

    public static Node parse(Parser parser) {

        if (parser.match(TokenType.WHILE)) {
            Token token = parser.previous();

            if (!parser.match(TokenType.LPAREN)) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected '(' after keyword while.",
                        token.getLine(),
                        token.getColumn(),
                        token.getLexeme(),
                        "(",
                        "Make sure that 'while' is followed by '('."
                ));
                parser.synchronize();
                return null;
            }

            Node expression = Expression.parse(parser);
            if (expression == null) {
                parser.addError(null); // todo
                parser.synchronize();
                return null;
            }

            if (!parser.match(TokenType.RPAREN)) {
                parser.addError(null); // todo
                parser.synchronize();
                return null;
            }

            Node statement = Statement.parse(parser);
            if (statement == null) {
                parser.addError(null);
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.ITERATOR_STATEMENT, token.getLine(), token.getColumn());
            result.setChildren(List.of(expression, statement));
            result.setLiteral(token.getLexeme());

            return result;
        }

        return null;
    }

}
