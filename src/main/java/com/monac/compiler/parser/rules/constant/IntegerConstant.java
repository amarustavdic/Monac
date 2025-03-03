package com.monac.compiler.parser.rules.constant;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

/**
 * A utility class for parsing integer constants in the source code.
 * <p>
 * This class provides a method to parse integer constant tokens
 * from the parser and convert them into a corresponding AST (Abstract Syntax Tree) node.
 * </p>
 **/
public final class IntegerConstant {

    /**
     * Parses an integer constant from the parser if the next token matches an integer constant.
     *
     * @param parser The parser instance used to analyze the token stream.
     * @return A {@link Node} representing the integer constant in the AST,
     *         or {@code null} if no integer constant is found.
     */
    public static Node parse(Parser parser) {
        if (parser.match(TokenType.INTEGER_CONSTANT)) {
            Token token = parser.previous();
            Node node = new Node(NodeType.INTEGER_CONSTANT, token.getLine(), token.getColumn());
            node.setLiteral(Integer.parseInt(token.getLexeme()));
            return node;
        }
        return null;
    }

}
