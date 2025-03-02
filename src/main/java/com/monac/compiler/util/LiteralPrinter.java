package com.monac.compiler.util;

import com.monac.compiler.parser.tree.Node;

public class LiteralPrinter extends ParseTreePrinter {

    public String formatNode(Node node) {
        Object literal = node.getLiteral();
        if (literal instanceof Integer) {
            return "\033[34m" + literal.toString() + "\033[0m"; // Blue for integers
        } else {
            return "\033[32m" + literal.toString() + "\033[0m"; // Green for other literals
        }
    }

}
