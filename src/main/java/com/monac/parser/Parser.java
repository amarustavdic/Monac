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

    private Node additiveExpression() {
        return null;
    }

    private Node multiplicativeExpression() {
        return null;
    }

    private Node castExpression() {
        return null;
    }

    private Node unaryExpression() {
        return null;
    }

    private Node postfixExpression() {
        return null;
    }

    private Node primaryExpression() {
        return null;
    }


    private Node constant() {

        Token token = tokens.get(cursor);
        if (token.getType() == TokenType.INTEGER_CONSTANT) {
            cursor++;
            return new Node(NodeType.CONSTANT, token);
        } else if (token.getType() == TokenType.CHARACTER_CONSTANT) {
            cursor++;
            return new Node(NodeType.CONSTANT, token);
        }

        return null;
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
        return null;
    }

}
