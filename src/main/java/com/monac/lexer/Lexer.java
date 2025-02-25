package com.monac.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final List<Token> tokens = new ArrayList<>();
    private final String input;

    public Lexer(String input) {
        this.input = input;
    }

}
