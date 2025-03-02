package com.monac.compiler.parser;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.rules.expression.MultiplicativeExpressionRule;
import com.monac.compiler.parser.rules.expression.PrimaryExpressionRule;
import com.monac.compiler.parser.tree.Node;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(Lexer lexer) {
        this.tokens = lexer.tokenize();
    }

    // Helper methods for terminal classes

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

    // Method for putting parser back in sync after error encountering
    public void synchronize() {
        // TODO: Implement sync method for parser
    }

    // Actual method for generating final AST
    public Node parse() {

        // And this is how parser is going to be recursively built
        // the reason I decided to make it this modular, is cuz first
        // time I was doing it in one file, and it was hard to change things

        return new MultiplicativeExpressionRule().parse(this, new PrimaryExpressionRule());

        // TODO: Maybe handle errors?
    }

}
