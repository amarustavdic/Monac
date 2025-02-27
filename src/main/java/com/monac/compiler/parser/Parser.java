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
     */
    public Node conditionalExpression() throws ParseException {
        Node condition = logicalOrExpression();
        if (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.QUESTION_MARK) {
                cursor++;
                Node trueExpression = expression();
                if (cursor < tokens.size() && tokens.get(cursor).getType() == TokenType.COLON) {
                    cursor++;
                    Node falseExpression = conditionalExpression();
                    List<Node> children = new ArrayList<>();
                    children.add(condition);
                    children.add(trueExpression);
                    children.add(falseExpression);
                    return new Node(NodeType.CONDITIONAL_EXPRESSION, children, token.getLexeme());
                } else {
                    throw new ParseException("Expected ':' after '?'", cursor);
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
     */
    public Node logicalOrExpression() throws ParseException {
        Node left = logicalAndExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.LOGICAL_OR) {
                cursor++;
                Node right = logicalAndExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.LOGICAL_OR_EXPRESSION, children, token.getLexeme());
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
     */
    public Node logicalAndExpression() throws ParseException {
        Node left = inclusiveOrExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.LOGICAL_AND) {
                cursor++;
                Node right = inclusiveOrExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.LOGICAL_AND_EXPRESSION, children, token.getLexeme());
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
     */
    public Node inclusiveOrExpression() throws ParseException {
        Node left = exclusiveOrExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.BITWISE_OR) {
                cursor++;
                Node right = exclusiveOrExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.INCLUSIVE_OR_EXPRESSION, children, token.getLexeme());
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
     */
    public Node exclusiveOrExpression() throws ParseException {
        Node left = andExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.BITWISE_XOR) {
                cursor++;
                Node right = andExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.EXCLUSIVE_OR_EXPRESSION, children, token.getLexeme());
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
     */
    public Node andExpression() throws ParseException {
        Node left = equalityExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.BITWISE_AND) {
                cursor++;
                Node right = equalityExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.AND_EXPRESSION, children, token.getLexeme());
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
     */
    public Node equalityExpression() throws ParseException {
        Node left = relationalExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.EQUALS_EQUALS || token.getType() == TokenType.NOT_EQUALS) {
                cursor++;
                Node right = relationalExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.EQUALITY_EXPRESSION, children, token.getLexeme());
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
     */
    public Node relationalExpression() throws ParseException {
        Node left = shiftExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.LESS_THAN ||
                    token.getType() == TokenType.GREATER_THAN ||
                    token.getType() == TokenType.LESS_EQUALS ||
                    token.getType() == TokenType.GREATER_EQUALS) {
                cursor++;
                Node right = shiftExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.RELATIONAL_EXPRESSION, children, token.getLexeme());
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
     */
    public Node shiftExpression() throws ParseException {
        Node left = additiveExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);

            // TODO: Lexer is not tokenizing shift right, thinks that it is less then...

            if (token.getType() == TokenType.LEFT_SHIFT || token.getType() == TokenType.RIGHT_SHIFT) {
                cursor++;
                Node right = additiveExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                left = new Node(NodeType.SHIFT_EXPRESSION, children, token.getLexeme());
            } else {
                break;
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
    public Node additiveExpression() throws ParseException {
        Node left = multiplicativeExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.PLUS || token.getType() == TokenType.MINUS) {
                cursor++;
                Node right = multiplicativeExpression();
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
    public Node multiplicativeExpression() throws ParseException {
        Node left = unaryExpression();
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.STAR ||
                    token.getType() == TokenType.DIVIDE ||
                    token.getType() == TokenType.MODULO) {
                cursor++;
                Node right = unaryExpression();
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
    public Node castExpression() throws ParseException {
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
    public Node unaryExpression() throws ParseException {
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
    public Node postfixExpression() throws ParseException {

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
    public Node primaryExpression() throws ParseException {

        // Attempt to parse parenthesized expression
        if (check(TokenType.LEFT_PAREN)) {
            match(TokenType.LEFT_PAREN);
            Node expression = expression();
            match(TokenType.RIGHT_PAREN);
            return new Node(NodeType.PRIMARY_EXPRESSION, List.of(expression), null);
        }

        // Try parsing identifier

        // Try parsing constant
        Node constantNode = constant();
        if (constantNode != null) {
            return constantNode;
        }

        // Try parsing string
        Node stringNode = string();
        if (stringNode != null) {
            return stringNode;
        }

        // If none of the cases succeeded, throw an error
        throw new ParseException("Expected primary expression but found: " + peek(), cursor);
    }


    /**
     * <pre>{@code
     * <constant> ::= <integer-constant>
     *              | <character-constant>
     *              | <floating-constant>
     *              | <enumeration-constant>
     * }</pre>
     */
    public Node constant() {
        return new Node(NodeType.CONSTANT, List.of(integerConstant()), null);
    }

    /**
     * <pre>{@code
     * <expression> ::= <assignment-expression>
     *                | <expression> , <assignment-expression>
     * }</pre>
     */
    public Node expression() throws ParseException {
        Node left = assignmentExpression();
        List<Node> children = new ArrayList<>();
        children.add(left);
        while (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.COMMA) {
                cursor++;
                Node right = assignmentExpression();
                children.add(right);
            } else {
                break;
            }
        }
        return new Node(NodeType.EXPRESSION, children, null);
    }

    /**
     * <pre>{@code
     * <assignment-expression> ::= <conditional-expression>
     *                           | <unary-expression> <assignment-operator> <assignment-expression>
     * }</pre>
     */
    public Node assignmentExpression() throws ParseException {
        Node left = conditionalExpression();
        if (cursor < tokens.size()) {
            Token token = tokens.get(cursor);
            if (token.getType() == TokenType.EQUALS || token.getType() == TokenType.PLUS_ASSIGN ||
                    token.getType() == TokenType.MINUS_ASSIGN || token.getType() == TokenType.MUL_ASSIGN ||
                    token.getType() == TokenType.DIV_ASSIGN || token.getType() == TokenType.MOD_ASSIGN) {
                cursor++;
                Node right = assignmentExpression();
                List<Node> children = new ArrayList<>();
                children.add(left);
                children.add(right);
                return new Node(NodeType.ASSIGNMENT_EXPRESSION, children, token.getLexeme());
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
    public Node initializer() throws ParseException {
        if (cursor >= tokens.size()) {
            throw new ParseException("Unexpected end of input while parsing initializer", cursor);
        }
        Token token = tokens.get(cursor);

        // Case: { <initializer-list> } or { <initializer-list> , }
        if (token.getType() == TokenType.LEFT_BRACE) {
            cursor++; // Consume '{'

            Node initializerListNode = initializerList();
            List<Node> children = initializerListNode.getChildren();

            // Check for optional trailing comma
            if (cursor < tokens.size() && tokens.get(cursor).getType() == TokenType.COMMA) {
                cursor++; // Consume ','
            }

            // Expect closing '}'
            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.RIGHT_BRACE) {
                throw new ParseException("Expected '}' after initializer-list", cursor);
            }
            cursor++; // Consume '}'
            return new Node(NodeType.INITIALIZER, children, null);
        }
        return assignmentExpression();
    }

    /**
     * <pre>{@code
     * <initializer-list> ::= <initializer>
     *                      | <initializer-list> , <initializer>
     * }</pre>
     */
    public Node initializerList() throws ParseException {
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
    public Node compoundStatement() throws ParseException {
        expect(TokenType.LEFT_BRACE, "Expecting left brace <compound-statement>");
        List<Node> children = new ArrayList<>();
        children.add(declaration());
        children.add(statement());
        expect(TokenType.RIGHT_BRACE, "Expecting right brace <compound-statement>");
        return new Node(NodeType.COMPOUND_STATEMENT, children, null);
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
    public Node statement() throws ParseException {

        Node child = labeledStatement();
        if (child != null) {
            return new Node(NodeType.STATEMENT, List.of(child), null);
        }

        child = expressionStatement();
        if (child != null) {
            return new Node(NodeType.STATEMENT, List.of(child), null);
        }

        child = compoundStatement();
        if (child != null) {
            return new Node(NodeType.STATEMENT, List.of(child), null);
        }

        child = selectionStatement();
        if (child != null) {
            return new Node(NodeType.STATEMENT, List.of(child), null);
        }

        child = iterationStatement();
        if (child != null) {
            return new Node(NodeType.STATEMENT, List.of(child), null);
        }

        child = jumpStatement();
        if (child != null) {
            return new Node(NodeType.STATEMENT, List.of(child), null);
        }

        throw new ParseException("Expected a valid statement, but none found", cursor);
    }

    /**
     * <pre>{@code
     * <labeled-statement> ::= <identifier> : <statement>
     *                       | case <constant-expression> : <statement>
     *                       | default : <statement>
     * }</pre>
     */
    public Node labeledStatement() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <expression-statement> ::= {<expression>}? ;
     * }</pre>
     */
    public Node expressionStatement() throws ParseException {
        if (Objects.requireNonNull(peek()).getType() == TokenType.SEMICOLON) {
            cursor++; // Consume ';'
            return new Node(NodeType.EXPRESSION_STATEMENT, null, null);
        }
        Node expr = expression();
        expect(TokenType.SEMICOLON, "Expected ';' after expression");
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
    public Node iterationStatement() throws ParseException {
        if (cursor >= tokens.size()) {
            throw new ParseException("Unexpected end of input. Expected an iteration statement.", cursor);
        }

        Token token = tokens.get(cursor);

        if (token.getType() == TokenType.WHILE) {
            cursor++; // Consume 'while'
            expect(TokenType.LEFT_PAREN, "Expected '(' after 'while'");
            Node condition = expression();
            expect(TokenType.RIGHT_PAREN, "Expected ')' after condition in 'while' statement");
            Node body = statement();
            return new Node(NodeType.ITERATION_STATEMENT, List.of(condition, body), "while");
        }

        if (token.getType() == TokenType.DO) {
            cursor++; // Consume 'do'
            Node body = statement();
            expect(TokenType.WHILE, "Expected 'while' after 'do' statement");
            expect(TokenType.LEFT_PAREN, "Expected '(' after 'while' in do-while loop");
            Node condition = expression();
            expect(TokenType.RIGHT_PAREN, "Expected ')' after condition in 'do-while' statement");
            expect(TokenType.SEMICOLON, "Expected ';' after 'do-while' statement");
            return new Node(NodeType.ITERATION_STATEMENT, List.of(body, condition), "do-while");
        }

        if (token.getType() == TokenType.FOR) {
            cursor++; // Consume 'for'
            expect(TokenType.LEFT_PAREN, "Expected '(' after 'for'");
            Node init = null, condition = null, increment = null;

            // First expression (optional)
            if (tokens.get(cursor).getType() != TokenType.SEMICOLON) {
                init = expression();
            }
            expect(TokenType.SEMICOLON, "Expected ';' after initialization in 'for' loop");

            // Second expression (optional)
            if (tokens.get(cursor).getType() != TokenType.SEMICOLON) {
                condition = expression();
            }
            expect(TokenType.SEMICOLON, "Expected ';' after condition in 'for' loop");

            // Third expression (optional)
            if (tokens.get(cursor).getType() != TokenType.RIGHT_PAREN) {
                increment = expression();
            }
            expect(TokenType.RIGHT_PAREN, "Expected ')' after increment in 'for' loop");

            Node body = statement();

            List<Node> children = new ArrayList<>();
            if (init != null) children.add(init);
            if (condition != null) children.add(condition);
            if (increment != null) children.add(increment);
            children.add(body);

            return new Node(NodeType.ITERATION_STATEMENT, children, "for");
        }

        throw new ParseException("Expected an iteration statement (while, do-while, or for)", cursor);
    }

    /**
     * <pre>{@code
     * <jump-statement> ::= goto <identifier> ;
     *                    | continue ;
     *                    | break ;
     *                    | return {<expression>}? ;
     * }</pre>
     */
    public Node jumpStatement() throws ParseException {
        Token token = tokens.get(cursor);

        // GOTO <identifier> ;
        if (token.getType() == TokenType.GOTO) {
            cursor++; // Consume "goto"
            // Node label = identifier();

            // Ensure a semicolon follows
            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
                throw new ParseException("Expected ';' after 'goto' statement", cursor);
            }
            cursor++; // Consume ";"

            return new Node(NodeType.JUMP_STATEMENT, null, "goto");
        }

        // CONTINUE ;
        if (token.getType() == TokenType.CONTINUE) {
            cursor++; // Consume "continue"

            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
                throw new ParseException("Expected ';' after 'continue' statement", cursor);
            }
            cursor++; // Consume ";"

            return new Node(NodeType.JUMP_STATEMENT, Collections.emptyList(), "continue");
        }

        // BREAK ;
        if (token.getType() == TokenType.BREAK) {
            cursor++; // Consume "break"

            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
                throw new ParseException("Expected ';' after 'break' statement", cursor);
            }
            cursor++; // Consume ";"

            return new Node(NodeType.JUMP_STATEMENT, Collections.emptyList(), "break");
        }

        // RETURN {<expression>}? ;
        if (token.getType() == TokenType.RETURN) {
            cursor++; // Consume "return"
            Node expression = null;

            if (cursor < tokens.size() && tokens.get(cursor).getType() != TokenType.SEMICOLON) {
                expression = expression();
            }

            if (cursor >= tokens.size() || tokens.get(cursor).getType() != TokenType.SEMICOLON) {
                throw new ParseException("Expected ';' after 'return' statement", cursor);
            }
            cursor++; // Consume ";"

            return new Node(NodeType.JUMP_STATEMENT,
                    expression == null ? Collections.emptyList() : List.of(expression),
                    "return");
        }

        throw new ParseException("Expected a jump statement (goto, continue, break, or return)", cursor);
    }

    // -------------------------------------------------------------------------------

    public Node string() {
        Token token = peek();
        if (match(TokenType.STRING_LITERAL)) {
            return new Node(NodeType.STRING, Collections.emptyList(), token.getLexeme());
        }
        throw error(token, "Expected string literal");
    }

    public Node integerConstant() {
        Token token = peek();
        if (match(TokenType.INTEGER_LITERAL)) {
            return new Node(NodeType.INTEGER_CONSTANT, Collections.emptyList(), token.getLexeme());
        }
        throw error(token, "Expected an integer literal");
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
        return identifier();
    }

}
