package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.list.ParameterTypeList;
import com.monac.compiler.parser.rules.expression.ConstantExpression;
import com.monac.compiler.parser.rules.other.Identifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a {@code <direct-declarator>} non-terminal rule.
 *
 * <p>Originally BNF for this rule looked like:</p>
 * <pre>{@code
 * <direct-declarator> ::= <identifier>
 * | ( <declarator> )
 * | <direct-declarator> [ {<constant-expression>}? ]
 * | <direct-declarator> ( <parameter-type-list> )
 * | <direct-declarator> ( {<identifier>}* )
 * }</pre>
 *
 * <p>But original grammar rule cannot be implemented as is
 * in a top-down recursive descent parser, therefore I had
 * to transform, and eliminate left-recursion.
 * </p>
 *
 * <p>Transformed BNF (right-recursive):</p>
 * <pre>{@code
 * <direct-declarator> ::= <identifier> <direct-declarator-prime>
 * | ( <declarator> )
 *
 * <direct-declarator-prime> ::= [ {<constant-expression>}? ]
 * | ( <parameter-type-list> )
 * | ( {<identifier>}* )
 * | ɛ
 * }</pre>
 */
public class DirectDeclarator {

    /**
     * Parses a direct declarator.
     *
     * @param parser The parser instance used to extract tokens.
     * @return A {@link Node} representing the parsed direct declarator,
     * or {@code null} if parsing fails.
     */
    public static Node parse(Parser parser) {

        // Handle nested declarators in parentheses: ( <declarator> )
        if (parser.match(TokenType.LPAREN)) {
            Node declarator = Declarator.parse(parser);
            if (declarator != null) {
                Token actual = parser.peek();
                if (parser.match(TokenType.RPAREN)) {
                    return declarator;
                } else {
                    // Handle error: missing closing parenthesis
                    parser.addError(new ParserException(
                            "Syntax error: expected ')' after declarator",
                            actual.getLine(), actual.getColumn(), actual.getLexeme(),
                            "expected ')'", "Ensure the declarator is correctly enclosed in parentheses."
                    ));
                    parser.synchronize();
                    return null;
                }
            } else {
                // Handle error: invalid declarator inside parentheses
                parser.addError(new ParserException(
                        "Syntax error: invalid declarator inside parentheses",
                        parser.peek().getLine(), parser.peek().getColumn(), parser.peek().getLexeme(),
                        "expected a valid declarator", "Ensure the declarator inside parentheses is valid."
                ));
                parser.synchronize();
                return null;
            }
        }

        // Handle identifier-based declarators
        Node identifier = Identifier.parse(parser);
        if (identifier == null) return null;

        return parsePrime(parser, identifier);
    }

    /**
     * Parses the suffix of a direct declarator.
     *
     * @param parser The parser instance used to extract tokens.
     * @param left The left-hand side of the direct declarator (initially an identifier or nested declarator).
     * @return A {@link Node} representing the parsed direct declarator.
     */
    public static Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.LBRACKET, TokenType.LPAREN)) {
            Token token = parser.previous();

            // Handle array indexing: [ {<constant-expression>}? ]
            if (token.getType() == TokenType.LBRACKET) {
                Node constantExpression = ConstantExpression.parse(parser);

                if (parser.match(TokenType.RBRACKET)) {

                    Node result = new Node(NodeType.DIRECT_DECLARATOR, token.getLine(), token.getColumn());
                    result.setLiteral("[ {<constant-expression>}? ]");
                    if (constantExpression != null) {
                        result.setChildren(List.of(constantExpression));
                    }
                    left = result;
                } else {
                    // Handle error: missing closing bracket
                    parser.addError(new ParserException(
                            "Syntax error: expected ']' after constant expression",
                            token.getLine(), token.getColumn(), token.getLexeme(),
                            "expected ']'", "Ensure the array index is properly closed."
                    ));
                    parser.synchronize();
                    return null;
                }
            }

            // Handle function-like declarations: ( <parameter-type-list> ) or ( {<identifier>}* )
            if (token.getType() == TokenType.LPAREN) {
                Node parameterTypeList = ParameterTypeList.parse(parser);
                if (parameterTypeList != null) {
                    if (parser.match(TokenType.RPAREN)) {
                        Node result = new Node(NodeType.DIRECT_DECLARATOR, token.getLine(), token.getColumn());
                        result.setLiteral("( <parameter-type-list> )");
                        result.setChildren(List.of(left, parameterTypeList));
                        left = result;
                    } else {
                        // Handle error: missing closing parenthesis after parameter type list
                        parser.addError(new ParserException(
                                "Syntax error: expected ')' after parameter type list",
                                token.getLine(), token.getColumn(), token.getLexeme(),
                                "expected ')'", "Ensure the function parameters are correctly enclosed in parentheses."
                        ));
                        parser.synchronize();
                        return null;
                    }
                } else {
                    List<Node> children = new ArrayList<>();
                    children.add(left);

                    // Parse function parameter identifiers
                    Node identifier;
                    while ((identifier = Identifier.parse(parser)) != null) {
                        children.add(identifier);
                    }

                    if (parser.match(TokenType.RPAREN)) {
                        Node result = new Node(NodeType.DIRECT_DECLARATOR, token.getLine(), token.getColumn());
                        result.setLiteral("( {<identifier>}* )");
                        result.setChildren(children);

                        left = result;
                    } else {
                        // Handle error: missing closing parenthesis after identifier list
                        parser.addError(new ParserException(
                                "Syntax error: expected ')' after function parameter identifiers",
                                token.getLine(), token.getColumn(), token.getLexeme(),
                                "expected ')'", "Ensure all function parameters are enclosed in parentheses."
                        ));
                        parser.synchronize();
                        return null;
                    }
                }
            }
        }
        return left;
    }

}
