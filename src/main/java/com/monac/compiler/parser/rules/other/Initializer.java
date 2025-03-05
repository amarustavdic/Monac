package com.monac.compiler.parser.rules.other;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.expression.AssignmentExpression;
import com.monac.compiler.parser.tree.Node;

/**
 * Parses initializer.
 *
 * <pre>{@code
 * <initializer> ::= <assignment-expression>
 * | { <initializer-list> }
 * | { <initializer-list> , }
 * }</pre>
 */
public final class Initializer {

    /**
     * Parses an initializer expression.
     * <p>
     * An initializer can either be an assignment expression or a list of initializers enclosed in curly braces.
     *
     * @param parser The parser instance that handles token processing.
     * @return A node representing the initializer, or null if parsing fails.
     */
    public static Node parse(Parser parser) {

        // Handle initializer list enclosed in braces.
        if (parser.match(TokenType.LBRACE)) {
            Node initializerList = InitializerList.parse(parser);

            // Handle trailing comma after the initializer list
            if (!parser.match(TokenType.COMMA)) {
                if (parser.match(TokenType.RBRACE)) {
                    return initializerList;
                } else {
                    Token actual = parser.peek();
                    parser.addError(new ParserException(
                            "Expected closing '}' for initializer list.",
                            actual.getLine(), actual.getColumn(), actual.getLexeme(),
                            "}",
                            "Ensure that every initializer list is properly closed with '}'."
                    ));
                    parser.synchronize();
                    return null;
                }
            } else {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected closing '}' for initializer list. (this is in Initializer class)",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "}",
                        "Ensure that every initializer list is properly closed with '}'."
                ));
                parser.synchronize();
                return null;
            }
        }

        // Handle assignment expression as a fallback.
        return AssignmentExpression.parse(parser);
    }

}
