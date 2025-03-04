package com.monac.compiler.util;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.ParseTreeVisitor;

import java.util.List;

public class ParseTreePrinter implements ParseTreeVisitor {

    @Override
    public void visit(Node node) {
        printNode(node, "", true);
    }

    private void printNode(Node node, String currentPrefix, boolean isLast) {
        if (node == null) return;

        System.out.println(currentPrefix + (isLast ? "└── " : "├── ") + formatNode(node));
        String childPrefix = currentPrefix + (isLast ? "    " : "│   ");

        List<Node> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            int size = children.size();
            for (int i = 0; i < size; i++) {
                printNode(children.get(i), childPrefix, i == size - 1);
            }
        }
    }

    public String formatNode(Node node) {
        Object literal = node.getLiteral();
        if (literal != null) {
            if (literal instanceof Integer) {
                return "\033[34m" + literal.toString() + "\033[0m"; // Blue for integers
            } else {
                return "\033[32m" + literal.toString() + "\033[0m"; // Green for other literals
            }
        } else {
            return node.getType().toString();
        }
    }

}
