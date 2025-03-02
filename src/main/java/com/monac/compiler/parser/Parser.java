package com.monac.compiler.parser;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.lexer.Token;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(Lexer lexer) {
        this.tokens = lexer.tokenize();
    }

}
