package com.monac.compiler.parser.grammar.other;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

/**
 * A utility class for parsing identifiers in the source code.
 * <p>
 * This class provides a method to parse an identifier token from the parser
 * and convert it into a corresponding AST (Abstract Syntax Tree) node.
 * </p>
 */
public final class Identifier {

    /**
     * Parses an identifier from the parser if the next token matches an identifier.
     * <p>
     * If the parser encounters an identifier token, this method creates a
     * {@link Node} representing the identifier and sets its literal value
     * to the identifier's lexeme.
     * </p>
     *
     * @param parser The parser instance used to analyze the token stream.
     * @return A {@link Node} representing the identifier in the AST,
     *         or {@code null} if no identifier is found.
     */
    public static Node parse(Parser parser) {
        if (parser.match(TokenType.IDENTIFIER)) {
            Token identifier = parser.previous();
            Node result = new Node(NodeType.IDENTIFIER, identifier.getLine(), identifier.getColumn());
            result.setLiteral(identifier.getLexeme());
            return result;
        }
        return null;
    }

}
