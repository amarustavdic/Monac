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
        return identifier();
    }

}
