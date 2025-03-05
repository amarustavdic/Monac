package com.monac.compiler.parser.rules.other;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

/**
 * This class parses an initializer list.
 * <p>
 * It handles a sequence of initializers, where each initializer is separated by a comma.
 *
 * <p>Original grammar:</p>
 * <pre>{@code
 * <initializer-list> ::= <initializer>
 * | <initializer-list> , <initializer>
 * }</pre>
 *
 * <p>The problem with the original grammar is that it has left-recursion, and this parser
 * (top-down, recursive descent parser) cannot deal with left recursion therefore original
 * grammar had to be transformed and left-recursion eliminated.</p>
 *
 * <p>Transformed grammar:</p>
 * <pre>{@code
 * <initializer-list> ::= <initializer> <initializer-list-prime>
 * <initializer-list-prime> ::= , <initializer> | ɛ
 * }</pre>
 */
public class InitializerList {

    public static Node parse(Parser parser) {
        Node initializer = Initializer.parse(parser);
        if (initializer == null) return null;
        return parsePrime(parser, initializer);
    }

    private static Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.COMMA)) {
            Node initializer = Initializer.parse(parser);

            if (initializer == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected initializer after ',' in initializer list.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<initializer>",
                        "Ensure that every ',' is followed by a valid initializer in the initializer list."
                ));
                parser.synchronize();
                return null;
            };

            Node result = new Node(NodeType.INITIALIZER_LIST, left.getLine(), left.getColumn());
            result.setChildren(List.of(left, initializer));

            left = result;
        }

        return left;
    }

}
