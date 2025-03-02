package com.monac.compiler.parser.symbols;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public interface Terminal {
    Node parse(Parser parser);
}
