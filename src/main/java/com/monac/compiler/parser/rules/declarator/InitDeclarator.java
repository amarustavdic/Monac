package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.Initializer;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

/**
 * Parses an initialized declarator, which defines a variable with or without an initializer.
 *
 * <p>An initialized declarator can be one of the following:</p>
 * <ul>
 *     <li>A simple declarator (e.g., {@code int x;})</li>
 *     <li>A declarator with an initializer (e.g., {@code int x = 5;})</li>
 * </ul>
 *
 * <p>Grammar rule:</p>
 * <pre>{@code
 * <init-declarator> ::= <declarator>
 * | <declarator> = <initializer>
 * }</pre>
 *
 * <p>This rule is crucial for parsing variable definitions with optional initial values.</p>
 */
public final class InitDeclarator {

    /**
     * Parses an initialized declarator from the provided parser.
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

        // Check if there is an assignment ('=') indicating an initializer
        if (parser.match(TokenType.ASSIGN)) {
            Node initializer = Initializer.parse(parser);

            if (initializer != null) {
                // Create a new node representing the initialized declarator
                Node result = new Node(NodeType.INIT_DECLARATOR, declarator.getLine(), declarator.getColumn());
                result.setChildren(List.of(declarator, initializer));
                result.setLiteral("=");
                return result;
            } else {
                parser.addError(new ParserException(
                        "Invalid initializer",
                        parser.peek().getLine(),
                        parser.peek().getColumn(),
                        parser.peek().getLexeme(),
                        "Expected an initializer after '='",
                        "Provide a valid expression or value after '='"
                ));
                parser.synchronize();
                return null;
            }
        }

        // If no initializer is present, return the declarator as is
        return declarator;
    }

}
