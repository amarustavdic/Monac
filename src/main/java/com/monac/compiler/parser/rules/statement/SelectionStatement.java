package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

/**
 * Parses selection statements.
 *
 * <pre>{@code
 * <selection-statement> ::= if ( <expression> ) <statement>
 * | if ( <expression> ) <statement> else <statement>
 * | switch ( <expression> ) <statement>
 * }</pre>
 */
public final class SelectionStatement {

    public static Node parse(Parser parser) {

        // Parse if statement
        if (parser.match(TokenType.IF)) {
            Token token = parser.previous();
            Node expression, statement1, statement2;

            // Expect '(' after 'if'
            if (!parser.match(TokenType.LPAREN)) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected '(' after 'if'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "'('",
                        "An 'if' statement must be followed by a condition inside parentheses, e.g., 'if (x > 0)'."
                ));
                parser.synchronize();
                return null;
            }

            // Parse the condition
            expression = Expression.parse(parser);
            if (expression == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected a valid condition inside '( ... )' after 'if'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<expression>",
                        "The 'if' statement must have a condition, e.g., 'if (x > 0)'."
                ));
                parser.synchronize();
                return null;
            }

            // Expect ')' after the condition
            if (!parser.match(TokenType.RPAREN)) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected ')' to close condition in 'if ( ... )'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "')'",
                        "Ensure the condition inside 'if' is properly enclosed, e.g., 'if (x > 0)'."
                ));
                parser.synchronize();
                return null;
            }

            // Parse the statement inside the 'if' block
            statement1 = Statement.parse(parser);
            if (statement1 == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected a statement after 'if (condition)'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<statement>",
                        "The 'if' statement must be followed by a valid statement or block, e.g., 'if (x > 0) { ... }'."
                ));
                parser.synchronize();
                return null;
            }

            // Handle optional 'else' clause
            if (!parser.match(TokenType.ELSE)) {
                Node result = new Node(NodeType.SELECTION_STATEMENT, token.getLine(), token.getColumn());
                result.setChildren(List.of(expression, statement1));
                result.setLiteral(token.getLexeme());
                return result;
            }

            // Parse the 'else' statement
            statement2 = Statement.parse(parser);
            if (statement2 == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected a statement after 'else'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<statement>",
                        "The 'else' must be followed by a valid statement, e.g., 'else { ... }'."
                ));
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.SELECTION_STATEMENT, token.getLine(), token.getColumn());
            result.setChildren(List.of(expression, statement1, statement2));
            result.setLiteral(token.getLexeme());
            return result;
        }


        // todo; Parse switch statement


        return null;
    }

}
