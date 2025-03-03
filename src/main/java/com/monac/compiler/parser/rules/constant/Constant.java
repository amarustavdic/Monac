package com.monac.compiler.parser.rules.constant;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.Parser;

public final class Constant {

    public static Node parse(Parser parser) {
        Node i = IntegerConstant.parse(parser);
        if (i != null) return i;
        Node c = CharacterConstant.parse(parser);
        if (c != null) return c;
        Node f = FloatingConstant.parse(parser);
        if (f != null) return f;
        return EnumerationConstant.parse(parser);
    }

}
