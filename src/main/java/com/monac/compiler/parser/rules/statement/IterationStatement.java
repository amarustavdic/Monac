package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses iteration statements, including:
 * <ul>
 *     <li><b>while-loop</b></li>
 *     <li><b>do-while-loop</b></li>
 *     <li><b>for-loop</b></li>
 * </ul>
 *
 * <pre>{@code
 * <iteration-statement> ::= while ( <expression> ) <statement>
 * | do <statement> while ( <expression> ) ;
 * | for ( {<expression>}? ; {<expression>}? ; {<expression>}? ) <statement>
 * }</pre>
 */
public final class IterationStatement {

    /**
     * Parses an iteration statement (while, do-while, or for-loop).
     * @param parser The parser instance.
     * @return A {@link Node} representing the loop structure, or {@code null} if parsing fails.
     */
    public static Node parse(Parser parser) {

        // Process while statement
        if (parser.match(TokenType.WHILE)) {
            Token token = parser.previous();

            if (!parser.match(TokenType.LPAREN)) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected '(' after keyword while.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "(", "Make sure that 'while' is followed by '('."
                ));
                parser.synchronize();
                return null;
            }

            Node expression = Expression.parse(parser);
            if (expression == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "While statement is missing <expression> inside '( <expression> )'.",
                        actual.getLine(),
                        actual.getColumn(),
                        actual.getLexeme(),
                        "<expression>, e.g.: 'x < 5'...",
                        "Make sure that there is a condition/expression inside parenthesis of iteration statements."
                ));
                parser.synchronize();
                return null;
            }

            if (!parser.match(TokenType.RPAREN)) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected ')' after condition/expression of while statement.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "')'",
                        "Make sure that for every '(' you have matching ')' (closing parenthesis)"
                ));
                parser.synchronize();
                return null;
            }

            Node statement = Statement.parse(parser);
            if (statement == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "While statement is missing actual statement to iterate over.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<statement>", "Make sure that there is something to iterate over bro/gal."
                ));
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.ITERATOR_STATEMENT, token.getLine(), token.getColumn());
            result.setChildren(List.of(expression, statement));
            result.setLiteral(token.getLexeme());

            return result;
        }

        // Process do statement
        if (parser.match(TokenType.DO)) {
            Token token = parser.previous();

            // Parse the loop body
            Node statement = Statement.parse(parser);
            if (statement == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected a statement after 'do'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<statement>",
                        "The 'do' keyword must be followed by a valid statement or block (e.g., 'do { ... } while (condition);')."
                ));
                parser.synchronize();
                return null;
            }

            // Expect 'while' after the statement
            if (!parser.match(TokenType.WHILE)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected 'while' after the 'do' statement.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "'while'",
                        "The 'do' statement must be followed by 'while (condition);'."
                ));
                parser.synchronize();
                return null;
            }

            // Expect '(' after 'while'
            if (!parser.match(TokenType.LPAREN)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected '(' after 'while'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "'('",
                        "The 'while' keyword in a 'do-while' loop must be followed by a condition inside parentheses, e.g., 'while (x < 5)'."
                ));
                parser.synchronize();
                return null;
            }

            // Parse the condition
            Node expression = Expression.parse(parser);
            if (expression == null) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected a condition inside '( ... )' after 'while'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<expression>",
                        "A 'do-while' loop must have a valid condition inside parentheses, e.g., 'while (x < 10)'."
                ));
                parser.synchronize();
                return null;
            }

            // Expect ')' after the condition
            if (!parser.match(TokenType.RPAREN)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected ')' to close the condition after 'while ( ... )'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "')'",
                        "Ensure that your condition is properly enclosed in parentheses, e.g., 'while (x < 5)'."
                ));
                parser.synchronize();
                return null;
            }

            // Expect ';' at the end of do-while
            if (!parser.match(TokenType.SEMICOLON)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected ';' after 'do-while' statement.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "';'",
                        "A 'do-while' loop must end with a semicolon, e.g., 'while (x < 5);'."
                ));
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.ITERATOR_STATEMENT, token.getLine(), token.getColumn());
            result.setChildren(List.of(statement, expression));
            result.setLiteral(token.getLexeme());

            return result;
        }

        // Process for statement
        if (parser.match(TokenType.FOR)) {
            Token token = parser.previous();
            Node expr1, expr2, expr3, statement;

            // Expect '(' after 'for'
            if (!parser.match(TokenType.LPAREN)) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected '(' after 'for'.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "'('",
                        "A 'for' loop must start with an opening parenthesis, e.g., 'for (int i = 0; i < 10; i++)'."
                ));
                parser.synchronize();
                return null;
            }

            // Parse first expression (optional)
            expr1 = Expression.parse(parser);

            // Expect first semicolon
            if (!parser.match(TokenType.SEMICOLON)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected ';' after initialization in 'for' loop.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "';'",
                        "A 'for' loop must have semicolons separating expressions, e.g., 'for (int i = 0; i < 10; i++)'."
                ));
                parser.synchronize();
                return null;
            }

            // Parse second expression (optional)
            expr2 = Expression.parse(parser);

            // Expect second semicolon
            if (!parser.match(TokenType.SEMICOLON)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected ';' after condition in 'for' loop.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "';'",
                        "The second semicolon is required in a 'for' loop, e.g., 'for (int i = 0; i < 10; i++)'."
                ));
                parser.synchronize();
                return null;
            }

            // Parse third expression (optional)
            expr3 = Expression.parse(parser);

            // Expect closing parenthesis
            if (!parser.match(TokenType.RPAREN)) {
                Token actual = parser.previous();
                parser.addError(new ParserException(
                        "Expected ')' to close 'for' loop header.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "')'",
                        "Ensure that your 'for' loop header is correctly closed, e.g., 'for (int i = 0; i < 10; i++)'."
                ));
                parser.synchronize();
                return null;
            }

            // Parse loop body
            statement = Statement.parse(parser);
            if (statement == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected a statement or block after 'for' loop header.",
                        actual.getLine(), actual.getColumn(), actual.getLexeme(),
                        "<statement>",
                        "A 'for' loop must have a valid body, e.g., 'for (...) { ... }'."
                ));
                parser.synchronize();
                return null;
            }

            List<Node> children = new ArrayList<>();
            if (expr1 != null) children.add(expr1);
            if (expr2 != null) children.add(expr2);
            if (expr3 != null) children.add(expr3);
            children.add(statement);

            Node result = new Node(NodeType.ITERATOR_STATEMENT, token.getLine(), token.getColumn());
            result.setChildren(children);
            result.setLiteral(token.getLexeme());

            return result;
        }

        return null;
    }

}
