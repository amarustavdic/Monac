package com.monac.compiler.parser.rules;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.Parser;

public interface Nonterminal {
    Node parse(Parser parser, Terminal terminal);
}
