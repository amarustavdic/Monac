package com.monac.compiler.parser;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.rules.expression.ConstantRule;
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

    // Actual method for generating final AST

    public Node parse() {
        return new ConstantRule().parse(this);
    }

}
