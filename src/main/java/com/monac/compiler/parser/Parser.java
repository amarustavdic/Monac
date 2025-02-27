package com.monac.compiler.parser;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.parser.ast.ASTNode;

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
