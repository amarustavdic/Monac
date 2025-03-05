package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.other.Initializer;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

/**
 * Parses an initialized declarator, which defines a variable with or without an initializer.
 *
 * <p>Grammar rule:</p>
 * <pre>{@code
 * <init-declarator> ::= <declarator>
 * | <declarator> = <initializer>
 * }</pre>
 */
public final class InitDeclarator {

    /**
     * Parses an initialized declarator.
     *
     * <p>Attempts to parse a declarator. If an assignment operator ('=') is found,
     * it also attempts to parse an initializer.</p>
     *
     * @param parser The parser instance used to extract tokens.
     * @return A {@link Node} representing the parsed initialized declarator,
     *         or {@code null} if a parsing error occurs.
     */
    public static Node parse(Parser parser) {

        Node declarator = Declarator.parse(parser);
        if (!parser.match(TokenType.ASSIGN)) return declarator;

        Node initializer = Initializer.parse(parser);
        if (initializer == null) {
            Token actual = parser.peek();
            parser.addError(new ParserException(
                    "Invalid initializer",
                    actual.getLine(),
                    actual.getColumn(),
                    actual.getLexeme(),
                    "Expected an initializer expression after '='.",
                    "An initializer should be a valid expression or value, such as a constant, a variable, or an expression. For example, 'x = 5;' or 'y = x + 1;'."
            ));
            parser.synchronize();
            return null;
        }

        Node result = new Node(NodeType.INIT_DECLARATOR, declarator.getLine(), declarator.getColumn());
        result.setChildren(List.of(declarator, initializer));
        return result;
    }

}
