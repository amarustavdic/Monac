package com.monac.compiler.parser.rules.other;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class TypedefName {

    public static Node parse(Parser parser) {
        return Identifier.parse(parser);
    }

}
