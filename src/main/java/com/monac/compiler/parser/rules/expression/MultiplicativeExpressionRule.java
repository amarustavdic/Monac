package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.Nonterminal;
import com.monac.compiler.parser.rules.Terminal;
import com.monac.compiler.parser.tree.Node;

public class MultiplicativeExpressionRule implements Nonterminal {

    // Original BNF but problem is left recursion

    // <multiplicative-expression> ::= <cast-expression>
    //| <multiplicative-expression> * <cast-expression>
    //| <multiplicative-expression> / <cast-expression>
    //| <multiplicative-expression> % <cast-expression>

    // TODO: Get rid of left recursion to be able to parse this

    @Override
    public Node parse(Parser parser, Terminal terminal) {
        return null;
    }


}
