package com.monac.compiler.parser;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.rules.unit.TranslationUnit;
import com.monac.compiler.parser.tree.Node;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<ParserException> errors = new ArrayList<>();
    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(Lexer lexer) {
        this.tokens = lexer.tokenize();
    }

    // Helper methods for terminal classes

    public Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        // throw error(peek(), message);
        return null;
    }

    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    public boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType().equals(type);
    }

    public Token advance() {
        if (!isAtEnd()) cursor++;
        return previous();
    }

    public boolean isAtEnd() {
        return peek().getType().equals(TokenType.EOF);
    }

    public Token peek() {
        return tokens.get(cursor);
    }

    public Token previous() {
        return tokens.get(cursor - 1);
    }

    public boolean hadErrors() {
        return !errors.isEmpty();
    }

    public List<ParserException> getErrors() {
        return errors;
    }

    public void addError(ParserException e) {
        errors.add(e);
    }

    // Method for putting parser back in sync after error encountering
    public void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;
//
//            switch (peek().getType()) {
//
//            }
            advance();
        }
        advance();
    }

    public Node parse() {
        return TranslationUnit.parse(this);
    }

}
