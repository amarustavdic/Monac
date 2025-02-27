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

    public Node constant() throws ParseException {
        return integerConstant();
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

        return primaryExpression();
    }

}
