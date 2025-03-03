package com.monac.compiler.parser.grammar.declarator;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public class DirectDeclarator {

    // <direct-declarator> ::= <identifier>
    //| ( <declarator> )
    //| <direct-declarator> [ {<constant-expression>}? ]
    //| <direct-declarator> ( <parameter-type-list> )
    //| <direct-declarator> ( {<identifier>}* )

    public static Node parse(Parser parser) {
        return null;
    }

}
