package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.tree.Node;

public final class ExpressionStatement {

    // <expression-statement> ::= {<expression>}? ;

    public static Node parse(Parser parser) {

        Node e = Expression.parse(parser);
        if (e != null) {
            parser.consume(TokenType.SEMICOLON, "");
            return e;
        }
        return null;
    }

}
