package com.monac.compiler.parser.rules;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;

public interface Rule {
    Node parse(Parser parser);
}
