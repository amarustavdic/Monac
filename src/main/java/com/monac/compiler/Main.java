package com.monac.compiler;

import com.monac.compiler.lexer.Token;

import java.util.List;

public class Main {


}

class Node {

}

class ParserX {

    List<Token> tokens;

    public ParserX(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Node parse() {
        return new PrimaryExpression().parse(null, this);
    }

}

interface Nonterminal {
    Node parse(Nonterminal nonterminal, ParserX parser);
}

class PrimaryExpression implements Nonterminal {

    @Override
    public Node parse(Nonterminal nonterminal, ParserX parser) {
        return null;
    }
}
