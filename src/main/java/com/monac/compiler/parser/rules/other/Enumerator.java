package com.monac.compiler.parser.rules.other;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.expression.ConstantExpression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

/**
 * Parses enumerator.
 *
 * <pre>{@code
 * <enumerator> ::= <identifier>
 * | <identifier> = <constant-expression>
 * }</pre>
 */
public final class Enumerator {

    /**
     * Parses an enumerator, either as just an identifier or as an identifier with a constant expression.
     *
     * @param parser The parser instance used to extract tokens.
     * @return A {@link Node} representing the parsed enumerator, or {@code null} if a parsing error occurs.
     */
    public static Node parse(Parser parser) {

        // Parse identifier
        Node identifier = Identifier.parse(parser);
        if (identifier == null) return null;
        if (!parser.match(TokenType.ASSIGN)) return identifier;

        // Expect constant
        Node constant = ConstantExpression.parse(parser);
        if (constant == null) {
            Token actual = parser.peek();
            parser.addError(new ParserException(
                    "Expected a constant expression after '='.",
                    actual.getLine(),
                    actual.getColumn(),
                    actual.getLexeme(),
                    "Constant expression",
                    "Make sure to provide a valid constant expression after '='. E.g., 'x = 10'."
            ));
            parser.synchronize();
            return null;
        }

        Node result = new Node(NodeType.ENUMERATOR, identifier.getLine(), identifier.getColumn());
        result.setChildren(List.of(identifier, constant));
        return result;
    }

}
