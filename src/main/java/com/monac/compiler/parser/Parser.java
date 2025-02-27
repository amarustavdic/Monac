package com.monac.compiler.parser;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;

import java.text.ParseException;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    public Node primaryExpression() throws ParseException {
        Node child = null;

        try {
            child = identifier();
        } catch (ParseException e) {
            System.err.println("Error parsing identifier: " + e.getMessage());
        }

        if (child == null) {
            try {
                child = constant();
            } catch (ParseException e) {
                System.err.println("Error parsing constant: " + e.getMessage());
            }
        }

        if (child == null) {
            throw new ParseException("Expected <primary-expression>", cursor);
        }

        return new Node(NodeType.PRIMARY_EXPRESSION, List.of(child), null);
    }

    public Node constant() throws ParseException {
        return null;
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
        return primaryExpression();
    }

}
