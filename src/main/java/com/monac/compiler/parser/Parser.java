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
     * <assignment-expression> ::= <conditional-expression>
     *      | <unary-expression> <assignment-operator> <assignment-expression>
     * }</pre>
     */
    public Node assignmentExpression() throws ParseException {
        // TODO: Handle conditionals later...
        List<Node> children = new ArrayList<>();
        children.add(unaryExpression());


        return null;
    }

    /**
     * <pre>{@code
     * <conditional-expression> ::= <logical-or-expression>
     *      | <logical-or-expression> ? <expression> : <conditional-expression>
     * }</pre>
     */
    public Node conditionalExpression() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <logical-or-expression> ::= <logical-and-expression>
     *          | <logical-or-expression> || <logical-and-expression>
     * }</pre>
     */
    public Node logicalOrExpression() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <logical-and-expression> ::= <inclusive-or-expression>
     *      | <logical-and-expression> && <inclusive-or-expression>
     * }</pre>
     */
    public Node logicalAndExpression() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <inclusive-or-expression> ::= <exclusive-or-expression>
     *     | <inclusive-or-expression> | <exclusive-or-expression>
     * }</pre>
     */
    public Node inclusiveOrExpression() throws ParseException {
        return null;
    }

    /**
     * <pre>{@code
     * <exclusive-or-expression> ::= <and-expression>
     *      | <exclusive-or-expression> ^ <and-expression>
     * }</pre>
     */
    public Node exclusiveOrExpression() throws ParseException {
        return null;
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
            if (token.getType() == TokenType.MULTIPLY ||
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

    public Node parse() throws ParseException {

        // TODO: Here might also want to handle case when there is more tokens, but should not be

        return andExpression();
    }

}
