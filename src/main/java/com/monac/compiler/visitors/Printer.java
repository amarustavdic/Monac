package com.monac.compiler.visitors;

import com.monac.compiler.parser.Node;
import java.util.List;

public class Printer implements Visitor {

    private int indentation = 0;

    @Override
    public void visit(Node node) {
        visitNode(node, true);
    }

    private void visitNode(Node node, boolean isLast) {
        if (node == null) {
            return;
        }

        printIndentation(isLast);
        System.out.println(node);

        indentation++;

        List<Node> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            int size = children.size();
            for (int i = 0; i < size; i++) {
                visitNode(children.get(i), i == size - 1);
            }
        }

        indentation--;
    }

    private void printIndentation(boolean isLast) {
        for (int i = 0; i < indentation - 1; i++) {
            System.out.print("    ");
        }
        if (indentation > 0) {
            System.out.print(isLast ? "└── " : "├── ");
        }
    }

}
