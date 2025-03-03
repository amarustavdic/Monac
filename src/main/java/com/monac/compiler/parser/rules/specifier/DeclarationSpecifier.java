package com.monac.compiler.parser.rules.specifier;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public final class DeclarationSpecifier {

    // <declaration-specifier> ::= <storage-class-specifier>
    //| <type-specifier>
    //| <type-qualifier>

    public static Node parse(Parser parser) {

        // for now only type specifier :P

        return TypeSpecifier.parse(parser);
    }

}
