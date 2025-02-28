package com.monac.parser;

import com.monac.lexer.Token;
import com.monac.lexer.TokenType;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Node relationalExpression() {
        Node left = shiftExpression();
        if (left == null) return null;
        while (match(TokenType.LESS_THAN, TokenType.GREATER_THAN, TokenType.LESS_EQUALS, TokenType.GREATER_EQUALS)) {
            Token operator = previous();
            Node right = shiftExpression();
            if (right == null) {
                System.out.println("Expected expression after " + operator);
                return null;
            }
            left = new Node(NodeType.RELATIONAL_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    private Node shiftExpression() {
        Node left = additiveExpression();
        if (left == null) return null;
        while (match(TokenType.LEFT_SHIFT, TokenType.RIGHT_SHIFT)) {
            Token operator = previous();
            Node right = additiveExpression();
            if (right == null) {
                System.out.println("Expected expression after " + operator);
                return null;
            }
            left = new Node(NodeType.SHIFT_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    private Node additiveExpression() {
        Node left = multiplicativeExpression();
        if (left == null) return null;
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Node right = multiplicativeExpression();
            if (right == null) {
                // throw new ParseException("Expected expression after " + operator);
                System.out.println("Expected expression after " + operator);
            }
            left = new Node(NodeType.ADDITIVE_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    private Node multiplicativeExpression() {
        Node left = castExpression();
        if (left == null) return null;
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            Token operator = previous();
            Node right = castExpression();
            if (right == null) {
                System.out.println("Expected expression after " + operator);
                // here should handle error, panic mode probably
            }
            left = new Node(NodeType.MULTIPLICATIVE_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    private Node castExpression() {
        return unaryExpression();
    }

    private Node unaryExpression() {
        return postfixExpression();
    }

    private Node postfixExpression() {
        return primaryExpression();
    }

    private Node primaryExpression() {

        if (match(TokenType.IDENTIFIER, TokenType.STRING)) {
            return new Node(NodeType.PRIMARY_EXPRESSION, previous());
        }

        Node constant = constant();
        if (constant != null) return constant;

        System.out.println("Error in primary expression");
        return null;
    }

    // For now handling an only couple of constants from c bnf grammar
    private Node constant() {
        if (match(TokenType.INTEGER_CONSTANT, TokenType.CHARACTER_CONSTANT)) {
            return new Node(NodeType.CONSTANT, previous());
        } else {
            System.out.println("Expected <integer-constant> or <character-constant>");
            return null;
        }
    }

    // ----------------- HELPER METHODS BELLOW

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) cursor++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(cursor);
    }

    private Token previous() {
        return tokens.get(cursor - 1);
    }

    public Node parse() {
        return relationalExpression();
    }

}
