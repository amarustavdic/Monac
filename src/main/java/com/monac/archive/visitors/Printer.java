package com.monac.archive.visitors;

import com.monac.archive.parser.Node;

import java.util.List;

public class Printer implements Visitor {

    @Override
    public void visit(Node node) {
        visitNode(node, "", true);
    }

    private void visitNode(Node node, String currentPrefix, boolean isLast) {
        if (node == null) return;

        System.out.println(currentPrefix + (isLast ? "└── " : "├── ") + formatNode(node));

        String childPrefix = currentPrefix + (isLast ? "    " : "│   ");

        List<Node> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            int size = children.size();
            for (int i = 0; i < size; i++) {
                visitNode(children.get(i), childPrefix, i == size - 1);
            }
        }
    }

    private String formatNode(Node node) {
        return node.getType() + " " + ((node.getToken() != null) ? node.getToken() : "");
    }

}
