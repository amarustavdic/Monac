package com.monac.compiler.parser.rules.constant;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.Parser;

public final class Constant {

    // TODO: Implement other constants in Constant

    public static Node parse(Parser parser) {
        return IntegerConstant.parse(parser);
    }

}
