package com.monac.compiler.parser.expression;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.parser.Node;
import com.monac.compiler.parser.NodeType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrimaryExpressionTest {

    @Test
    void testValidIdentifierPrimaryExpression() throws ParserException {
        String code = "identifier32";
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer.tokenize());
        Node result = parser.primaryExpression();

        assertNotNull(result);
        assertEquals(NodeType.PRIMARY_EXPRESSION, result.getType());
        assertEquals(NodeType.IDENTIFIER, result.getChildren().getFirst().getType());
        assertEquals("identifier32", result.getChildren().getFirst().getValue());
    }

    @Test
    void testValidConstantPrimaryExpression() throws ParserException {
        String code = "123";
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer.tokenize());
        Node result = parser.primaryExpression();

        assertNotNull(result);
        assertEquals(NodeType.PRIMARY_EXPRESSION, result.getType());
        assertEquals(NodeType.CONSTANT, result.getChildren().getFirst().getType());
        assertEquals("123", result.getChildren().getFirst().getValue());
    }

    @Test
    void testValidStringPrimaryExpression() throws ParserException {
        String code = "\"hello\"";
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer.tokenize());
        Node result = parser.primaryExpression();

        assertNotNull(result);
        assertEquals(NodeType.PRIMARY_EXPRESSION, result.getType());
        assertEquals(NodeType.STRING, result.getChildren().getFirst().getType());
        assertEquals("hello", result.getChildren().getFirst().getValue());
    }

    @Test
    void testValidParenthesizedExpression() throws ParserException {

    }

    @Test
    void testInvalidTokenTypeForPrimaryExpression() {
        // Test case for an invalid token type (e.g., a keyword or operator instead of an identifier, constant, or string).
    }

    @Test
    void testUnmatchedParentheses() {
        // Test case for a mismatched parentheses scenario (e.g., a left parenthesis without a right parenthesis).
    }

    @Test
    void testParserExceptionForInvalidPrimaryExpression() {
        // Test case to ensure the correct exception message is thrown when no valid primary expression is found.
    }

    @Test
    void testEmptyInput() {
        // Test case for empty input or an unexpected end of input while parsing a primary expression.
    }

    @Test
    void testMultipleValidPrimaryExpressions() {
        // Test case where multiple valid primary expressions are parsed sequentially.
    }

    @Test
    void testInvalidConstantTypeInPrimaryExpression() {
        // Test case where a token that looks like a constant but is not valid (e.g., malformed or invalid number format).
    }

    @Test
    void testEmptyParentheses() {
        // Test case for an empty set of parentheses (e.g., "()"), which should ideally be considered invalid.
    }

    @Test
    void testParserExceptionForParenthesesWithoutExpression() {
        // Test case where parentheses are used but no valid expression is inside (e.g., "()").
    }

}
