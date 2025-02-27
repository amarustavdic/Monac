package com.monac.compiler.parser;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
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

    // -------------------- probably will be no use ---------------------------------------------

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

    // ------------------------------------------------------------------------------------------

    /**
     * <pre>{@code
     * <expression> ::= <assignment-expression>
     *      | <expression> , <assignment-expression>
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
     *      | <unary-expression> <assignment-operator> <assignment-expression>
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
     * <conditional-expression> ::= <logical-or-expression>
     *      | <logical-or-expression> ? <expression> : <conditional-expression>
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
     *          | <logical-or-expression> || <logical-and-expression>
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
     *      | <logical-and-expression> && <inclusive-or-expression>
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
     *     | <inclusive-or-expression> | <exclusive-or-expression>
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
     *      | <exclusive-or-expression> ^ <and-expression>
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
     *      | <and-expression> & <equality-expression>
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
     * | <equality-expression> == <relational-expression>
     * | <equality-expression> != <relational-expression>
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
     * | <relational-expression> < <shift-expression>
     * | <relational-expression> > <shift-expression>
     * | <relational-expression> <= <shift-expression>
     * | <relational-expression> >= <shift-expression>
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
     * | <shift-expression> << <additive-expression>
     * | <shift-expression> >> <additive-expression>
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
     * | <additive-expression> + <multiplicative-expression>
     * | <additive-expression> - <multiplicative-expression>
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
     * | <multiplicative-expression> * <cast-expression>
     * | <multiplicative-expression> / <cast-expression>
     * | <multiplicative-expression> % <cast-expression>
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
     *      | ( <type-name> ) <cast-expression>
     * }</pre>
     */
    public Node castExpression() throws ParseException {
        // TODO: Handle cast (but probably wont be needed)
        return new Node(NodeType.CAST_EXPRESSION, List.of(unaryExpression()), null);
    }

    public Node unaryExpression() throws ParseException {
        return new Node(NodeType.UNARY_EXPRESSION, List.of(postfixExpression()), null);
    }

    public Node postfixExpression() throws ParseException {
        return new Node(NodeType.POSTFIX_EXPRESSION, List.of(primaryExpression()), null);
    }

    public Node primaryExpression() throws ParseException {
        Node child = null;

        // TODO: Figure out how to handle exceptions properly

        try {
            child = identifier();
        } catch (ParseException e) {
            // System.err.println("Error parsing identifier: " + e.getMessage());
        }

        if (child == null) {
            try {
                child = constant();
            } catch (ParseException e) {
                // System.err.println("Error parsing constant: " + e.getMessage());
            }
        }

        if (child == null) {
            throw new ParseException("Expected <primary-expression>", cursor);
        }

        return new Node(NodeType.PRIMARY_EXPRESSION, List.of(child), null);
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

    public Node constant() throws ParseException {
        return new Node(NodeType.CONSTANT, List.of(integerConstant()), null);
    }

    public Node integerConstant() throws ParseException {
        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.INTEGER_LITERAL) {
            cursor++;
            return new Node(NodeType.INTEGER_CONSTANT, null, token.getLexeme());
        } else {
            throw new ParseException("Expected an integer constant, but found: " + token.getType(), cursor);
        }
    }

    public Node identifier() throws ParseException {
        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.IDENTIFIER) {
            cursor++;
            return new Node(NodeType.IDENTIFIER, null, token.getLexeme());
        } else {
            throw new ParseException("Expected an identifier, but found: " + token.getType(), cursor);
        }
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



    // TODO: Those two bellow, initializer and initializer-list hast to be tested
    // ------------------------------------------------------------------------------------------

    /**
     * <pre>{@code
     * <initializer> ::= <assignment-expression>
     * | { <initializer-list> }
     * | { <initializer-list> , }
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
     * | <initializer-list> , <initializer>
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

    // ------------------------------------------------------------------------------------------

    public Node parse() throws ParseException {

        // TODO: Here might also want to handle case when there is more tokens, but should not be

         return expression();
    }

}
