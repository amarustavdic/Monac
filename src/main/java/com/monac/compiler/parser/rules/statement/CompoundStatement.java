package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.declaration.Declaration;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses compound statement.
 *
 * <p><b>Grammar Rule:</b></p>
 * <pre>{@code
 * <compound-statement> ::= { {<declaration>}* {<statement>}* }
 * }</pre>
 */
public final class CompoundStatement {

    /**
     * Parses a compound statement.
     *
     * @param parser The parser instance handling the source code.
     * @return A {@link Node} representing the compound statement, or {@code null} if parsing fails.
     */
    public static Node parse(Parser parser) {

        if (parser.match(TokenType.LBRACE)) {
            Token token = parser.previous();
            List<Node> children = new ArrayList<>();

            // Parse multiple declarations (zero or more)
            Node declaration;
            while ((declaration = Declaration.parse(parser)) != null) children.add(declaration);

            // Parse multiple statements (zero or more)
            Node statement;
            while ((statement = Statement.parse(parser)) != null) children.add(statement);

            // Expect right brace at end of compound
            if (!parser.match(TokenType.RBRACE)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected closing '}', at the end of compound statement.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "}", "Make sure that every '{' has its closing pair '}'"
                ));
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.COMPOUND_STATEMENT, token.getLine(), token.getColumn());
            result.setChildren(children);

            return result;
        }
        return null;
    }

}
