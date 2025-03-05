package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

/**
 * Parses an expression statement.
 *
 * <p>Grammar:</p>
 * <pre>{@code
 * <expression-statement> ::= {<expression>}? ;
 * }</pre>
 */
public final class ExpressionStatement {

    /**
     * Parses an expression statement.
     *
     * @param parser The parser instance.
     * @return A {@link Node} representing the parsed expression statement, or {@code null} if parsing fails.
     */
    public static Node parse(Parser parser) {
        Node expression = Expression.parse(parser);

        if (expression != null && !parser.match(TokenType.SEMICOLON)) {
            Token actual = parser.peek();
            parser.addError(new ParserException(
                    "Expected ';' at the end of the expression statement.",
                    actual.getLine(), actual.getColumn(), actual.getLexeme(),
                    "';'",
                    "Make sure every expression statement ends with a semicolon, e.g., 'x = 5;'."
            ));
            parser.synchronize();
            return null;
        }

        return expression;
    }

}
