package com.monac.compiler.parser.grammar.expression;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.grammar.constant.Constant;
import com.monac.compiler.parser.grammar.other.Identifier;
import com.monac.compiler.parser.grammar.other.String;
import com.monac.compiler.parser.tree.Node;

/**
 * A utility class for parsing primary expressions in the source code.
 * <p>
 * A primary expression can be an identifier, a constant, a string literal,
 * or a parenthesized expression. This class attempts to parse each of these
 * in order and returns the corresponding AST (Abstract Syntax Tree) node.
 * </p>
 */
public final class PrimaryExpression {

    /**
     * Parses a primary expression from the parser.
     * <p>
     * This method attempts to parse a primary expression, which can be:
     * <ul>
     *   <li>An identifier</li>
     *   <li>A constant</li>
     *   <li>A string literal</li>
     *   <li>A parenthesized expression</li>
     * </ul>
     * If a parenthesized expression is encountered, it ensures that a closing
     * parenthesis is present. If not, an exception is thrown.
     * </p>
     *
     * @param parser The parser instance used to analyze the token stream.
     * @return A {@link Node} representing the parsed primary expression in the AST,
     *         or {@code null} if no valid primary expression is found.
     * @throws Exception If a parenthesized expression is not properly closed.
     */
    public static Node parse(Parser parser) throws Exception {

        Node identifier = Identifier.parse(parser);
        if (identifier != null) return identifier;

        Node constant = Constant.parse(parser);
        if (constant != null) return constant;

        Node string = String.parse(parser);
        if (string != null) return string;

        if (parser.match(TokenType.LEFT_PARENTHESIS)) {
            Node expression = Expression.parse(parser);
            if (parser.match(TokenType.RIGHT_PARENTHESIS)) {
                return expression;
            } else {
                throw new Exception("Expected closed parenthesis.");
            }
        }

        return null;
    }
}
