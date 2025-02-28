package com.monac.compiler.visitors;

import com.monac.compiler.parser.Node;

public class CodeGenerator implements Visitor {

    private final StringBuilder code;

    public CodeGenerator() {
        code = new StringBuilder();
    }

    public String getCode() {
        return code.toString();
    }

    @Override
    public void visit(Node node) {
        if (node == null) return;



    }

}
