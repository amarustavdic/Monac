package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;


public final class Declarator {

    // <declarator> ::= {<pointer>}? <direct-declarator> todo

    public static Node parse(Parser parser) {
        return DirectDeclarator.parse(parser);
    }

}
