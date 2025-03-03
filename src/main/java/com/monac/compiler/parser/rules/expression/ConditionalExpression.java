package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class ConditionalExpression {

    // <conditional-expression> ::= <logical-or-expression>
    //| <logical-or-expression> ? <expression> : <conditional-expression>

    public static Node parse(Parser parser) {
        Node left = LogicalOrExpression.parse(parser);

        if (parser.match(TokenType.QUESTION)) {
            // TODO: later man

            Node middle = Expression.parse(parser);
        }

        return left;
    }

}
