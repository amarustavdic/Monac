package com.monac.compiler.parser;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    /**
     * <pre>{@code
     * <translation-unit> ::= {<external-declaration>}*
     * }</pre>
     */
    public Node translationUnit() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <external-declaration> ::= <function-definition> | <declaration>
     * }</pre>
     */
    public Node externalDeclaration() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <function-definition> ::= {<declaration-specifier>}* <declarator> {<declaration>}* <compound-statement>
     * }</pre>
     */
    public Node functionDefinition() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <declaration-specifier> ::= <storage-class-specifier>
     *                           | <type-specifier>
     *                           | <type-qualifier>
     * }</pre>
     */
    public Node declarationSpecifier() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <storage-class-specifier> ::= auto | register | static | extern | typedef
     * }</pre>
     */
    public Node storageClassSpecifier() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <type-specifier> ::= void | char | short | int | long | float | double | signed | unsigned
     *                    | <struct-or-union-specifier> | <enum-specifier> | <typedef-name>
     * }</pre>
     */
    public Node typeSpecifier() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <struct-or-union-specifier> ::= <struct-or-union> <identifier> { {<struct-declaration>}+ }
     *                               | <struct-or-union> { {<struct-declaration>}+ }
     *                               | <struct-or-union> <identifier>
     * }</pre>
     */
    public Node structOrUnionSpecifier() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <struct-or-union> ::= struct | union
     * }</pre>
     */
    public Node unionOrStruct() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <struct-declaration> ::= {<specifier-qualifier>}* <struct-declarator-list>
     * }</pre>
     */
    public Node structDeclaration() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <specifier-qualifier> ::= <type-specifier> | <type-qualifier>
     * }</pre>
     */
    public Node specifierQualifier() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <struct-declarator-list> ::= <struct-declarator>
     *                            | <struct-declarator-list> , <struct-declarator>
     * }</pre>
     */
    public Node structDeclaratorList() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <struct-declarator> ::= <declarator>
     *                       | <declarator> : <constant-expression>
     *                       | : <constant-expression>
     * }</pre>
     */
    public Node structDeclarator() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <declarator> ::= {<pointer>}? <direct-declarator>
     * }</pre>
     */
    public Node declarator() throws ParseException {
        return null;
    }

    /**
     * <pointer> ::= * {<type-qualifier>}* {<pointer>}?
     */
    public Node pointer() throws ParseException {
        if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.STAR) {
            throw new ParseException("Expected '*' for pointer declaration", cursor);
        }
        cursor++;

        List<Node> qualifiers = new ArrayList<>();
        while (cursor < tokens.size() &&
                (tokens.get(cursor).getType() == TokenType.CONST || tokens.get(cursor).getType() == TokenType.VOLATILE)) {
            qualifiers.add(typeQualifier());
        }

        // Optionally parse (nested pointers)
        Node nestedPointer = null;
        if (cursor < tokens.size() && tokens.get(cursor).getType() == TokenType.STAR) {
            nestedPointer = pointer();
        }

        List<Node> children = new ArrayList<>(qualifiers);
        if (nestedPointer != null) {
            children.add(nestedPointer);
        }
        return new Node(NodeType.POINTER, children, "*");
    }

    /**
     * <pre>{@code
     * <type-qualifier> ::= const | volatile
     * }</pre>
     */
    public Node typeQualifier() throws ParseException {
        if (cursor >= tokens.size()) {
            throw new ParseException("Unexpected end of input while parsing type-qualifier", cursor);
        }
        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.CONST || token.getType() == TokenType.VOLATILE) {
            cursor++;
            return new Node(NodeType.TYPE_QUALIFIER, null, token.getLexeme());
        }
        throw new ParseException("Expected 'const' or 'volatile' for type-qualifier", cursor);
    }

    /**
     * <pre>{@code
     * <struct-declarator> ::= <declarator>
     *                       | <declarator> : <constant-expression>
     *                       | : <constant-expression>
     * }</pre>
     */
    public Node directDeclarator() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <constant-expression> ::= <conditional-expression>
     * }</pre>
     */
    public Node constantExpression() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <conditional-expression> ::= <logical-or-expression>
     *                            | <logical-or-expression> ? <expression> : <conditional-expression>
     * }</pre>
     *
     * Parses a conditional expression (ternary operator).
     *
     * @return A Node representing the parsed conditional expression.
     * @throws ParserException If the expression is not valid.
     */
    public Node conditionalExpression() throws ParserException {
        Node condition;

        try {
            condition = logicalOrExpression(); // Parse logical OR expression
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid conditional expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        if (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            if (token.getType() == TokenType.QUESTION_MARK) {
                cursor++; // Consume '?'
                Node trueExpression;

                try {
                    trueExpression = expression(); // Parse the true branch
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected an expression after '?' but got: " + tokens.get(cursor).getType(),
                            "expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                if (cursor < tokens.size() && tokens.get(cursor).getType() == TokenType.COLON) {
                    cursor++; // Consume ':'
                    Node falseExpression;

                    try {
                        falseExpression = conditionalExpression(); // Parse the false branch
                    } catch (ParserException e) {
                        throw new ParserException(
                                "Expected an expression after ':' but got: " + tokens.get(cursor).getType(),
                                "expression",
                                tokens.get(cursor).getLine(),
                                tokens.get(cursor).getColumn()
                        );
                    }

                    return new Node(NodeType.CONDITIONAL_EXPRESSION, List.of(condition, trueExpression, falseExpression), token.getLexeme());
                } else {
                    Token unexpected = cursor < tokens.size() ? tokens.get(cursor) : null;
                    throw new ParserException(
                            "Expected ':' after '?' but found: " + (unexpected != null ? unexpected.getType() : "EOF"),
                            ":",
                            unexpected != null ? unexpected.getLine() : -1,
                            unexpected != null ? unexpected.getColumn() : -1
                    );
                }
            }
        }

        return condition;
    }

    /**
     * <pre>{@code
     * <logical-or-expression> ::= <logical-and-expression>
     *                           | <logical-or-expression> || <logical-and-expression>
     * }</pre>
     *
     * Parses a logical OR expression, handling multiple '||' chained operations.
     *
     * @return A Node representing the parsed logical OR expression.
     * @throws ParserException If parsing fails due to unexpected tokens.
     */
    public Node logicalOrExpression() throws ParserException {
        Node left;

        try {
            left = logicalAndExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid logical OR expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            if (token.getType() == TokenType.LOGICAL_OR) {
                cursor++; // Consume '||'
                Node right;

                try {
                    right = logicalAndExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected expression after '||', but found: " + tokens.get(cursor).getType(),
                            "logical-and-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                left = new Node(NodeType.LOGICAL_OR_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <logical-and-expression> ::= <inclusive-or-expression>
     *                            | <logical-and-expression> && <inclusive-or-expression>
     * }</pre>
     *
     * Parses a logical AND expression, handling multiple '&&' chained operations.
     *
     * @return A Node representing the parsed logical AND expression.
     * @throws ParserException If parsing fails due to unexpected tokens.
     */
    public Node logicalAndExpression() throws ParserException {
        Node left;

        try {
            left = inclusiveOrExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid logical AND expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            if (token.getType() == TokenType.LOGICAL_AND) {
                cursor++; // Consume '&&'
                Node right;

                try {
                    right = inclusiveOrExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected expression after '&&', but found: " + tokens.get(cursor).getType(),
                            "inclusive-or-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                left = new Node(NodeType.LOGICAL_AND_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <inclusive-or-expression> ::= <exclusive-or-expression>
     *                             | <inclusive-or-expression> | <exclusive-or-expression>
     * }</pre>
     *
     * Parses an inclusive OR expression, handling multiple '|' operations.
     *
     * @return A Node representing the parsed inclusive OR expression.
     * @throws ParserException If parsing fails due to unexpected tokens.
     */
    public Node inclusiveOrExpression() throws ParserException {
        Node left;

        try {
            left = exclusiveOrExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid inclusive OR expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            if (token.getType() == TokenType.BITWISE_OR) {
                cursor++; // Consume '|'
                Node right;

                try {
                    right = exclusiveOrExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected expression after '|', but found: " + tokens.get(cursor).getType(),
                            "exclusive-or-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                left = new Node(NodeType.INCLUSIVE_OR_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <exclusive-or-expression> ::= <and-expression>
     *                             | <exclusive-or-expression> ^ <and-expression>
     * }</pre>
     *
     * Parses an exclusive OR expression, handling multiple '^' operations.
     *
     * @return A Node representing the parsed exclusive OR expression.
     * @throws ParserException If parsing fails due to unexpected tokens.
     */
    public Node exclusiveOrExpression() throws ParserException {
        Node left;

        try {
            left = andExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid exclusive OR expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            if (token.getType() == TokenType.BITWISE_XOR) {
                cursor++; // Consume '^'
                Node right;

                try {
                    right = andExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected expression after '^', but found: " + tokens.get(cursor).getType(),
                            "and-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                left = new Node(NodeType.EXCLUSIVE_OR_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <and-expression> ::= <equality-expression>
     *                    | <and-expression> & <equality-expression>
     * }</pre>
     *
     * Parses a bitwise AND expression, allowing multiple '&' operations.
     *
     * @return A Node representing the parsed AND expression.
     * @throws ParserException If parsing fails due to unexpected tokens.
     */
    public Node andExpression() throws ParserException {
        Node left;

        try {
            left = equalityExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid bitwise AND expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            if (token.getType() == TokenType.BITWISE_AND) {
                cursor++; // Consume '&'
                Node right;

                try {
                    right = equalityExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected expression after '&', but found: " + tokens.get(cursor).getType(),
                            "equality-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                left = new Node(NodeType.AND_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <equality-expression> ::= <relational-expression>
     *                         | <equality-expression> == <relational-expression>
     *                         | <equality-expression> != <relational-expression>
     * }</pre>
     *
     * Parses equality expressions, supporting both `==` and `!=` operators.
     *
     * @return A Node representing the parsed equality expression.
     * @throws ParserException If parsing fails due to unexpected tokens.
     */
    public Node equalityExpression() throws ParserException {
        Node left;

        try {
            left = relationalExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid equality expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            // Check for equality operators (== or !=)
            if (token.getType() == TokenType.EQUALS_EQUALS || token.getType() == TokenType.NOT_EQUALS) {
                cursor++; // Consume '==' or '!='
                Node right;

                try {
                    right = relationalExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected relational expression after '" + token.getLexeme() + "', but found: " + tokens.get(cursor).getType(),
                            "relational-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                left = new Node(NodeType.EQUALITY_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <relational-expression> ::= <shift-expression>
     *                           | <relational-expression> < <shift-expression>
     *                           | <relational-expression> > <shift-expression>
     *                           | <relational-expression> <= <shift-expression>
     *                           | <relational-expression> >= <shift-expression>
     * }</pre>
     *
     * Parses relational expressions, supporting operators like `<`, `>`, `<=`, and `>=`.
     *
     * @return A Node representing the parsed relational expression.
     * @throws ParserException If parsing fails due to unexpected tokens.
     */
    public Node relationalExpression() throws ParserException {
        Node left;
        try {
            left = shiftExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid relational expression: " + e.getMessage(),
                    e.getFound(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            // Check for relational operators (<, >, <=, >=)
            if (token.getType() == TokenType.LESS_THAN ||
                    token.getType() == TokenType.GREATER_THAN ||
                    token.getType() == TokenType.LESS_EQUALS ||
                    token.getType() == TokenType.GREATER_EQUALS) {

                cursor++; // Consume the relational operator

                Node right;
                try {
                    right = shiftExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected shift expression after relational operator '" + token.getLexeme() + "', but found: " + tokens.get(cursor).getType(),
                            "shift-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                left = new Node(NodeType.RELATIONAL_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <shift-expression> ::= <additive-expression>
     *                      | <shift-expression> << <additive-expression>
     *                      | <shift-expression> >> <additive-expression>
     * }</pre>
     *
     * Parses shift expressions involving left and right shifts (`<<`, `>>`) applied to additive expressions.
     *
     * @return A Node representing the parsed shift expression.
     * @throws ParserException If an error occurs while parsing the shift expression.
     */
    public Node shiftExpression() throws ParserException {
        Node left;
        try {
            left = additiveExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid shift expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            // Handle the shift operators (<< and >>)
            if (token.getType() == TokenType.LEFT_SHIFT || token.getType() == TokenType.RIGHT_SHIFT) {
                cursor++; // Consume the shift operator

                Node right;
                try {
                    right = additiveExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected an additive expression after shift operator '" + token.getLexeme() + "', but found: " + tokens.get(cursor).getType(),
                            "additive-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                // Create a node for the shift expression and continue parsing
                left = new Node(NodeType.SHIFT_EXPRESSION, List.of(left, right), token.getLexeme());
            } else {
                break; // Exit loop if no shift operators are found
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <additive-expression> ::= <multiplicative-expression>
     *                         | <additive-expression> + <multiplicative-expression>
     *                         | <additive-expression> - <multiplicative-expression>
     * }</pre>
     */
    public Node additiveExpression() throws ParserException {
        Node left = multiplicativeExpression();

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            // Check if the token is a "+" or "-" operator
            if (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS) {
                cursor++; // Consume the operator token

                Node right;
                try {
                    right = multiplicativeExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected a multiplicative expression after operator '" + token.getLexeme() + "', but found: " + tokens.get(cursor).getType(),
                            "multiplicative-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.ADDITIVE_EXPRESSION, children, token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <multiplicative-expression> ::= <cast-expression>
     *                               | <multiplicative-expression> * <cast-expression>
     *                               | <multiplicative-expression> / <cast-expression>
     *                               | <multiplicative-expression> % <cast-expression>
     * }</pre>
     */
    public Node multiplicativeExpression() throws ParserException {
        Node left = castExpression();

        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            // Check if the token is a "*", "/", or "%" operator
            if (token.getType() == TokenType.STAR ||
                    token.getType() == TokenType.DIVIDE ||
                    token.getType() == TokenType.MODULO) {

                cursor++; // Consume the operator token

                Node right;
                try {
                    right = castExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected a cast expression after operator '" + token.getLexeme() + "', but found: " + tokens.get(cursor).getType(),
                            "cast-expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                // Create a new node for this multiplicative expression
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.MULTIPLICATIVE_EXPRESSION, children, token.getLexeme());
            } else {
                break;
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <cast-expression> ::= <unary-expression>
     *                     | ( <type-name> ) <cast-expression>
     * }</pre>
     */
    public Node castExpression() throws ParserException {
        // TODO: Handle cast (but probably wont be needed)
        return new Node(NodeType.CAST_EXPRESSION, List.of(unaryExpression()), null);
    }

    /**
     * <pre>{@code
     * <unary-expression> ::= <postfix-expression>
     *                      | ++ <unary-expression>
     *                      | -- <unary-expression>
     *                      | <unary-operator> <cast-expression>
     *                      | sizeof <unary-expression>
     *                      | sizeof <type-name>
     * }</pre>
     */
    public Node unaryExpression() throws ParserException {
        return new Node(NodeType.UNARY_EXPRESSION, List.of(postfixExpression()), null);
    }

    /**
     * <pre>{@code
     * <postfix-expression> ::= <primary-expression>
     *                        | <postfix-expression> [ <expression> ]
     *                        | <postfix-expression> ( {<assignment-expression>}* )
     *                        | <postfix-expression> . <identifier>
     *                        | <postfix-expression> -> <identifier>
     *                        | <postfix-expression> ++
     *                        | <postfix-expression> --
     * }</pre>
     */
    public Node postfixExpression() throws ParserException {

        Node child = primaryExpression();
        if (child != null) {
            return new Node(NodeType.POSTFIX_EXPRESSION, List.of(primaryExpression()), null);
        } else {
            return null;
        }
    }

    /**
     * <pre>{@code
     * <primary-expression> ::= <identifier>
     *                        | <constant>
     *                        | <string>
     *                        | ( <expression> )
     * }</pre>
     */
    public Node primaryExpression() throws ParserException {

        try {
            return new Node(NodeType.PRIMARY_EXPRESSION, List.of(identifier()), null);
        } catch (ParserException e) {
            // This one is expected, try next
        }

        try {
            return new Node(NodeType.PRIMARY_EXPRESSION, List.of(constant()), null);
        } catch (ParserException e) {
            // This one is expected, try next
        }

        try {
            return new Node(NodeType.PRIMARY_EXPRESSION, List.of(string()), null);
        } catch (ParserException e) {
            // This one is expected, try next
        }

        try {
            Token leftParen = tokens.get(cursor);
            if (leftParen.getType() == TokenType.LEFT_PAREN) {

                Node expression = expression();

                Token rightParen = tokens.get(cursor);
                if (rightParen.getType() == TokenType.RIGHT_PAREN) {

                    return new Node(NodeType.PRIMARY_EXPRESSION, List.of(expression), null);

                } else {
                    throw new ParserException(null, null, 0, 0);
                }
            } else {
                throw new ParserException(null, null, 0, 0);
            }
        } catch (ParserException e) {
            // This one is expected, try next
        }

        throw new ParserException(
                "Expected an identifier, constant, or string, but found something else.",
                "none",
                tokens.get(cursor).getLine(),
                tokens.get(cursor).getColumn()
        );
    }

    /**
     * <pre>{@code
     * <constant> ::= <integer-constant>
     *              | <character-constant>
     *              | <floating-constant>
     *              | <enumeration-constant>
     * }</pre>
     */
    public Node constant() throws ParserException {
        Node node = integerConstant();
        return new Node(NodeType.CONSTANT, List.of(node), node.getValue());
    }

    /**
     * <pre>{@code
     * <expression> ::= <assignment-expression>
     *                | <expression> , <assignment-expression>
     * }</pre>
     *
     * Parses an expression, which can be a single assignment-expression
     * or a sequence of assignment-expressions separated by commas.
     *
     * @return A Node representing the parsed expression.
     * @throws ParserException If an invalid expression is encountered.
     */
    public Node expression() throws ParserException {
        List<Node> children = new ArrayList<>();

        try {
            Node left = assignmentExpression();
            children.add(left);

            while (cursor < tokens.size()) {
                Token token = tokens.get(cursor);

                if (token.getType() == TokenType.COMMA) {
                    cursor++; // Consume the comma

                    try {
                        Node right = assignmentExpression();
                        children.add(right);
                    } catch (ParserException e) {
                        throw new ParserException(
                                "Expected an assignment expression after ',' but got: " + tokens.get(cursor).getType(),
                                tokens.get(cursor).getType().toString(),
                                tokens.get(cursor).getLine(),
                                tokens.get(cursor).getColumn()
                        );
                    }

                } else {
                    break; // Stop parsing if no more commas
                }
            }
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        return new Node(NodeType.EXPRESSION, children, null);
    }

    /**
     * <pre>{@code
     * <assignment-expression> ::= <conditional-expression>
     *                           | <unary-expression> <assignment-operator> <assignment-expression>
     * }</pre>
     *
     * Parses an assignment expression, which can be either:
     *  - A conditional expression
     *  - A unary expression followed by an assignment operator and another assignment expression.
     *
     * @return A Node representing the parsed assignment expression.
     * @throws ParserException If an invalid assignment expression is encountered.
     */
    public Node assignmentExpression() throws ParserException {
        Node left;

        try {
            left = conditionalExpression();
        } catch (ParserException e) {
            throw new ParserException(
                    "Invalid assignment expression: " + e.getMessage(),
                    e.getExpected(),
                    e.getLine(),
                    e.getColumn()
            );
        }

        if (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            // Check if the current token is an assignment operator
            if (token.getType() == TokenType.EQUALS || token.getType() == TokenType.PLUS_ASSIGN ||
                    token.getType() == TokenType.MINUS_ASSIGN || token.getType() == TokenType.MUL_ASSIGN ||
                    token.getType() == TokenType.DIV_ASSIGN || token.getType() == TokenType.MOD_ASSIGN) {

                cursor++; // Consume the assignment operator

                Node right;
                try {
                    right = assignmentExpression();
                } catch (ParserException e) {
                    throw new ParserException(
                            "Expected an expression after assignment operator '" + token.getLexeme() + "', but found: " + tokens.get(cursor).getType(),
                            "assignment expression",
                            tokens.get(cursor).getLine(),
                            tokens.get(cursor).getColumn()
                    );
                }

                return new Node(NodeType.ASSIGNMENT_EXPRESSION, List.of(left, right), token.getLexeme());
            }
        }

        return left;
    }

    /**
     * <pre>{@code
     * <assignment-operator> ::= = | *= | /= | %= | += | -= | <<= | >>= | &= | ^= | |=
     * }</pre>
     */
    public Node assignmentOperator() throws ParseException {

        // TODO: Ain't going to be handling all for now, just '=' for now

        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.EQUALS) {
            cursor++;
            return new Node(NodeType.EQUALS, null, token.getLexeme());
        } else {
            throw new ParseException("Expected an EQUALS, but found: " + token.getType(), cursor);
        }
    }

    /**
     * <pre>{@code
     * <unary-operator> ::= & | * | + | - | ~ | !
     * }</pre>
     */
    public Node unaryOperator() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <type-name> ::= {<specifier-qualifier>}+ {<abstract-declarator>}?
     * }</pre>
     */
    public Node typeName() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <parameter-type-list> ::= <parameter-list>
     *                         | <parameter-list> , ...
     * }</pre>
     */
    public Node parameterTypeList() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <parameter-list> ::= <parameter-declaration>
     *                    | <parameter-list> , <parameter-declaration>
     * }</pre>
     */
    public Node parameterList() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <parameter-declaration> ::= {<declaration-specifier>}+ <declarator>
     *                           | {<declaration-specifier>}+ <abstract-declarator>
     *                           | {<declaration-specifier>}+
     * }</pre>
     */
    public Node parameterDeclaration() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <abstract-declarator> ::= <pointer>
     *                         | <pointer> <direct-abstract-declarator>
     *                         | <direct-abstract-declarator>
     * }</pre>
     */
    public Node abstractDeclarator() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <direct-abstract-declarator> ::= ( <abstract-declarator> )
     *                                | {<direct-abstract-declarator>}? [ {<constant-expression>}? ]
     *                                | {<direct-abstract-declarator>}? ( {<parameter-type-list>}? )
     * }</pre>
     */
    public Node directAbstractDeclarator() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <enum-specifier> ::= enum <identifier> { <enumerator-list> }
     *                    | enum { <enumerator-list> }
     *                    | enum <identifier>
     * }</pre>
     */
    public Node enumSpecifier() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <enumerator-list> ::= <enumerator>
     *                     | <enumerator-list> , <enumerator>
     * }</pre>
     */
    public Node enumeratorList() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <enumerator> ::= <identifier>
     *                | <identifier> = <constant-expression>
     * }</pre>
     */
    public Node enumerator() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <typedef-name> ::= <identifier>
     * }</pre>
     */
    public Node typedefName() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <declaration> ::= {<declaration-specifier>}+ {<init-declarator>}* ;
     * }</pre>
     */
    public Node declaration() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <init-declarator> ::= <declarator> | <declarator> = <initializer>
     * }</pre>
     */
    public Node initDeclarator() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <initializer> ::= <assignment-expression>
     *                 | { <initializer-list> }
     *                 | { <initializer-list> , }
     * }</pre>
     */
    public Node initializer() throws ParserException {
//        if (cursor >= tokens.size()) {
//            throw new ParseException("Unexpected end of input while parsing initializer", cursor);
//        }
//        Token token = tokens.get(cursor);
//
//        // Case: { <initializer-list> } or { <initializer-list> , }
//        if (token.getType() == TokenType.LEFT_BRACE) {
//            cursor++; // Consume '{'
//
//            Node initializerListNode = initializerList();
//            List<Node> children = initializerListNode.getChildren();
//
//            // Check for optional trailing comma
//            if (cursor < tokens.size() && tokens.get(cursor).getType() == TokenType.COMMA) {
//                cursor++; // Consume ','
//            }
//
//            // Expect closing '}'
//            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.RIGHT_BRACE) {
//                throw new ParseException("Expected '}' after initializer-list", cursor);
//            }
//            cursor++; // Consume '}'
//            return new Node(NodeType.INITIALIZER, children, null);
//        }
//        return assignmentExpression();
        return null;
    }

    /**
     * <pre>{@code
     * <initializer-list> ::= <initializer>
     *                      | <initializer-list> , <initializer>
     * }</pre>
     */
    public Node initializerList() throws ParserException {
        List<Node> children = new ArrayList<>();
        children.add(initializer());
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.COMMA) {
                cursor++; // Consume ','
                children.add(initializer());
            } else {
                break;
            }
        }
        return new Node(NodeType.INITIALIZER_LIST, children, null);
    }

    /**
     * <pre>{@code
     * <compound-statement> ::= { {<declaration>}* {<statement>}* }
     * }</pre>
     */
    public Node compoundStatement() throws ParserException {
//        expect(TokenType.LEFT_BRACE, "Expecting left brace <compound-statement>");
//        List<Node> children = new ArrayList<>();
//        children.add(declaration());
//        children.add(statement());
//        expect(TokenType.RIGHT_BRACE, "Expecting right brace <compound-statement>");
//        return new Node(NodeType.COMPOUND_STATEMENT, children, null);
        return null;
    }

    /**
     * <pre>{@code
     * <statement> ::= <labeled-statement>
     *               | <expression-statement>
     *               | <compound-statement>
     *               | <selection-statement>
     *               | <iteration-statement>
     *               | <jump-statement>
     * }</pre>
     */
    public Node statement() throws ParserException {
//
//        Node child = labeledStatement();
//        if (child != null) {
//            return new Node(NodeType.STATEMENT, List.of(child), null);
//        }
//
//        child = expressionStatement();
//        if (child != null) {
//            return new Node(NodeType.STATEMENT, List.of(child), null);
//        }
//
//        child = compoundStatement();
//        if (child != null) {
//            return new Node(NodeType.STATEMENT, List.of(child), null);
//        }
//
//        child = selectionStatement();
//        if (child != null) {
//            return new Node(NodeType.STATEMENT, List.of(child), null);
//        }
//
//        child = iterationStatement();
//        if (child != null) {
//            return new Node(NodeType.STATEMENT, List.of(child), null);
//        }
//
//        child = jumpStatement();
//        if (child != null) {
//            return new Node(NodeType.STATEMENT, List.of(child), null);
//        }
//
//        throw new ParseException("Expected a valid statement, but none found", cursor);
        return null;
    }

    /**
     * <pre>{@code
     * <labeled-statement> ::= <identifier> : <statement>
     *                       | case <constant-expression> : <statement>
     *                       | default : <statement>
     * }</pre>
     */
    public Node labeledStatement() throws ParserException {
        return null;
    }

    /**
     * <pre>{@code
     * <expression-statement> ::= {<expression>}? ;
     * }</pre>
     */
    public Node expressionStatement() throws ParserException {
        if (Objects.requireNonNull(peek()).getType() == TokenType.SEMICOLON) {
            cursor++; // Consume ';'
            return new Node(NodeType.EXPRESSION_STATEMENT, null, null);
        }
        Node expr = expression();
        // expect(TokenType.SEMICOLON, "Expected ';' after expression");
        return new Node(NodeType.EXPRESSION_STATEMENT, List.of(expr), null);
    }

    /**
     * <pre>{@code
     * <selection-statement> ::= if ( <expression> ) <statement>
     *                         | if ( <expression> ) <statement> else <statement>
     *                         | switch ( <expression> ) <statement>
     * }</pre>
     */
    public Node selectionStatement() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <iteration-statement> ::= while ( <expression> ) <statement>
     *                         | do <statement> while ( <expression> ) ;
     *                         | for ( {<expression>}? ; {<expression>}? ; {<expression>}? ) <statement>
     * }</pre>
     */
    public Node iterationStatement() throws ParserException {
//        if (cursor >= tokens.size()) {
//            throw new ParserException("Unexpected end of input. Expected an iteration statement.", );
//        }
//
//        Token token = tokens.get(cursor);
//
//        if (token.getType() == TokenType.WHILE) {
//            cursor++; // Consume 'while'
//            expect(TokenType.LEFT_PAREN, "Expected '(' after 'while'");
//            Node condition = expression();
//            expect(TokenType.RIGHT_PAREN, "Expected ')' after condition in 'while' statement");
//            Node body = statement();
//            return new Node(NodeType.ITERATION_STATEMENT, List.of(condition, body), "while");
//        }
//
//        if (token.getType() == TokenType.DO) {
//            cursor++; // Consume 'do'
//            Node body = statement();
//            expect(TokenType.WHILE, "Expected 'while' after 'do' statement");
//            expect(TokenType.LEFT_PAREN, "Expected '(' after 'while' in do-while loop");
//            Node condition = expression();
//            expect(TokenType.RIGHT_PAREN, "Expected ')' after condition in 'do-while' statement");
//            expect(TokenType.SEMICOLON, "Expected ';' after 'do-while' statement");
//            return new Node(NodeType.ITERATION_STATEMENT, List.of(body, condition), "do-while");
//        }
//
//        if (token.getType() == TokenType.FOR) {
//            cursor++; // Consume 'for'
//            expect(TokenType.LEFT_PAREN, "Expected '(' after 'for'");
//            Node init = null, condition = null, increment = null;
//
//            // First expression (optional)
//            if (tokens.get(cursor).getType() != TokenType.SEMICOLON) {
//                init = expression();
//            }
//            expect(TokenType.SEMICOLON, "Expected ';' after initialization in 'for' loop");
//
//            // Second expression (optional)
//            if (tokens.get(cursor).getType() != TokenType.SEMICOLON) {
//                condition = expression();
//            }
//            expect(TokenType.SEMICOLON, "Expected ';' after condition in 'for' loop");
//
//            // Third expression (optional)
//            if (tokens.get(cursor).getType() != TokenType.RIGHT_PAREN) {
//                increment = expression();
//            }
//            expect(TokenType.RIGHT_PAREN, "Expected ')' after increment in 'for' loop");
//
//            Node body = statement();
//
//            List<Node> children = new ArrayList<>();
//            if (init != null) children.add(init);
//            if (condition != null) children.add(condition);
//            if (increment != null) children.add(increment);
//            children.add(body);
//
//            return new Node(NodeType.ITERATION_STATEMENT, children, "for");
//        }
//
//        throw new ParseException("Expected an iteration statement (while, do-while, or for)", cursor);
        return null;
    }

    /**
     * <pre>{@code
     * <jump-statement> ::= goto <identifier> ;
     *                    | continue ;
     *                    | break ;
     *                    | return {<expression>}? ;
     * }</pre>
     */
    public Node jumpStatement() throws ParserException {
//        Token token = tokens.get(cursor);
//
//        // GOTO <identifier> ;
//        if (token.getType() == TokenType.GOTO) {
//            cursor++; // Consume "goto"
//            // Node label = identifier();
//
//            // Ensure a semicolon follows
//            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
//                throw new ParseException("Expected ';' after 'goto' statement", cursor);
//            }
//            cursor++; // Consume ";"
//
//            return new Node(NodeType.JUMP_STATEMENT, null, "goto");
//        }
//
//        // CONTINUE ;
//        if (token.getType() == TokenType.CONTINUE) {
//            cursor++; // Consume "continue"
//
//            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
//                throw new ParseException("Expected ';' after 'continue' statement", cursor);
//            }
//            cursor++; // Consume ";"
//
//            return new Node(NodeType.JUMP_STATEMENT, Collections.emptyList(), "continue");
//        }
//
//        // BREAK ;
//        if (token.getType() == TokenType.BREAK) {
//            cursor++; // Consume "break"
//
//            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
//                throw new ParseException("Expected ';' after 'break' statement", cursor);
//            }
//            cursor++; // Consume ";"
//
//            return new Node(NodeType.JUMP_STATEMENT, Collections.emptyList(), "break");
//        }
//
//        // RETURN {<expression>}? ;
//        if (token.getType() == TokenType.RETURN) {
//            cursor++; // Consume "return"
//            Node expression = null;
//
//            if (cursor < tokens.size() && tokens.get(cursor).getType() != TokenType.SEMICOLON) {
//                expression = expression();
//            }
//
//            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
//                throw new ParseException("Expected ';' after 'return' statement", cursor);
//            }
//            cursor++; // Consume ";"
//
//            return new Node(NodeType.JUMP_STATEMENT,
//                    expression == null ? Collections.emptyList() : List.of(expression),
//                    "return");
//        }
//
//        throw new ParseException("Expected a jump statement (goto, continue, break, or return)", cursor);
        return null;
    }

    // -------------------------------------------------------------------------------

    /**
     * Parses a string literal token. If successful, creates a {@link Node} with the string literal's lexeme.
     * Throws a {@link ParserException} if the token is not a string literal.
     *
     * @return A {@link Node} representing the string literal token.
     * @throws ParserException If the current token is not a string literal.
     */
    public Node string() throws ParserException {
        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.STRING_LITERAL) {
            cursor++;
            return new Node(NodeType.STRING, Collections.emptyList(), token.getLexeme());
        }
        throw new ParserException(
                TokenType.STRING_LITERAL.toString(),
                token.getType().toString(),
                token.getLine(),
                token.getColumn()
        );
    }

    /**
     * Parses an integer constant token. If successful, creates a {@link Node} with the integer constant's lexeme.
     * Throws a {@link ParserException} if the token is not an integer literal.
     *
     * @return A {@link Node} representing the integer constant token.
     * @throws ParserException If the current token is not an integer literal.
     */
    public Node integerConstant() throws ParserException {
        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.INTEGER_LITERAL) {
            cursor++;
            return new Node(NodeType.INTEGER_CONSTANT, Collections.emptyList(), token.getLexeme());
        }
        throw new ParserException(
                TokenType.INTEGER_LITERAL.toString(),
                token.getType().toString(),
                token.getLine(),
                token.getColumn()
        );
    }

    /**
     * Parses an identifier token. If successful, creates a {@link Node} with the identifier's lexeme.
     * Throws a {@link ParserException} if the token is not an identifier.
     *
     * @return A {@link Node} representing the identifier token.
     * @throws ParserException If the current token is not an identifier.
     */
    public Node identifier() throws ParserException {
        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.IDENTIFIER) {
            cursor++;
            return new Node(NodeType.IDENTIFIER, Collections.emptyList(), token.getLexeme());
        }
        throw new ParserException(
                TokenType.IDENTIFIER.toString(),
                token.getType().toString(),
                token.getLine(),
                token.getColumn()
        );
    }

    // -----------------------------------------------------------------------------------------
    //                                 HELPER METHODS
    // -----------------------------------------------------------------------------------------

    /**
     * Checks if the current token matches the expected type. If not, throws a
     * ParseException with the given error message. Advances the cursor if the
     * token matches.
     *
     * @param expectedType The expected token type.
     * @param errorMessage The error message to include if the token type doesn't match.
     * @throws ParseException If the current token doesn't match the expected type.
     */
    private void expect(TokenType expectedType, String errorMessage) throws ParseException {
        if (cursor >= tokens.size()) {
            throw new ParseException("Unexpected end of input. " + errorMessage, cursor);
        }
        Token token = tokens.get(cursor);
        if (token.getType() != expectedType) {
            throw new ParseException(errorMessage + " Found: " + token.getType(), cursor);
        }
        cursor++;
    }

    /**
     * Checks if the current token matches any of the provided token types.
     * Advances the cursor if a match is found and returns true. Otherwise,
     * returns false.
     *
     * @param types The token types to check.
     * @return True if the current token matches one of the provided types, otherwise false.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the current token matches the given type without advancing the cursor.
     *
     * @param type The token type to check.
     * @return True if the current token matches the given type, otherwise false.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    /**
     * Advances the cursor and returns the current token.
     * If the cursor is at the end, no change happens.
     *
     * @return The current token after advancing the cursor.
     */
    private Token advance() {
        if (!isAtEnd()) cursor++;
        return previous();
    }

    /**
     * Checks if the cursor is at the end of the token list (i.e., the token is of type EOF).
     *
     * @return True if the cursor is at the end, otherwise false.
     */
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    /**
     * Returns the current token without advancing the cursor.
     *
     * @return The current token.
     */
    private Token peek() {
        return tokens.get(cursor);
    }

    /**
     * Returns the previous token (one token before the current token).
     *
     * @return The previous token.
     */
    private Token previous() {
        return tokens.get(cursor - 1);
    }

    /**
     * Advances the cursor and returns the current token if it matches the given type.
     * If not, throws a parse error with the provided message.
     *
     * @param type    The expected token type.
     * @param message The error message to use if the token doesn't match the expected type.
     * @return The current token if it matches the type.
     * @throws ParseException If the current token doesn't match the expected type.
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    /**
     * Logs the error with the provided token and message, then returns a ParseError.
     *
     * @param token   The token that caused the error.
     * @param message The error message.
     * @return A new ParseError instance.
     */
    public ParseError error(Token token, String message) {
        System.out.println(token + " " + message);
        return new ParseError();
    }

    // -----------------------------------------------------------------------------------------

    public Node parse() throws ParserException {
        return expression();
    }

}
