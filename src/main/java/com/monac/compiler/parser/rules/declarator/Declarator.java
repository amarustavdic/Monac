package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class Declarator {

    // <declarator> ::= {<pointer>}? <direct-declarator>

    public static Node parse(Parser parser) {

        // for now not supporting pointers
        return DirectDeclarator.parse(parser);
    }

}
