package com.monac.compiler.parser.grammar.qualifier;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

/**
 * The TypeQualifier class is responsible for parsing type qualifiers
 * in the source code. In this case, it checks for the presence of
 * specific type qualifiers like `const` and `volatile` in the source
 * code, and creates a corresponding abstract syntax tree (AST) node
 * to represent them.
 */
public final class TypeQualifier {

    /**
     * Parses the source code for type qualifiers such as `const` or `volatile`.
     * If a type qualifier is found, it creates a new AST node representing
     * the type qualifier and returns it.
     *
     * @param parser The parser instance used to analyze the input and identify tokens.
     * @return A Node representing the type qualifier if one is found; otherwise, returns null.
     */
    public static Node parse(Parser parser) {
        if (parser.match(TokenType.CONST, TokenType.VOLATILE)) {
            Token token = parser.previous();
            Node result = new Node(NodeType.TYPE_QUALIFIER, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());
            return result;
        }
        return null;
    }

}
