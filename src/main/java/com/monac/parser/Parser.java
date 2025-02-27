package com.monac.parser;

import com.monac.lexer.Token;
import com.monac.parser.ast.ASTNode;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ASTNode parse() {
        return null;
    }

}
