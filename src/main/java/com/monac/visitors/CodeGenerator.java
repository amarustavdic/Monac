package com.monac.visitors;

import com.monac.parser.Node;

public class CodeGenerator implements Visitor {

    private final StringBuilder code = new StringBuilder();

    public CodeGenerator() {
        code.append("MOV SP, 0x0FFF").append('\n'); // initialize stack
    }

    @Override
    public void visit(Node node) {

        switch (node.getType()) {
            case ADDITIVE_EXPRESSION -> generateAdditiveExpression(node);
            case MULTIPLICATIVE_EXPRESSION -> generateMultiplicativeExpression(node);
            case CONSTANT -> generateConstant(node);
        }

    }

    private void generateAdditiveExpression(Node node) {
        Node left = node.getChildren().get(0);
        Node right = node.getChildren().get(1);

        visit(left);
        visit(right);

        // TODO: Figure out better way to get operators
        String operator = node.getValue();

        code.append("POP B").append('\n');
        code.append("POP A").append('\n');

        if (operator.equals("+")) {
            code.append("ADD A, B").append('\n');
        } else if (operator.equals("-")) {
            code.append("SUB A, B").append('\n');
        }

        code.append("PUSH A").append('\n'); // Store result back on stack
    }

    private void generateMultiplicativeExpression(Node node) {
        Node left = node.getChildren().get(0);
        Node right = node.getChildren().get(1);

        visit(left);
        visit(right);

        String operator = node.getValue();

        code.append("POP B").append('\n'); // Get right operand
        code.append("POP A").append('\n'); // Get left operand

        if (operator.equals("*")) {
            code.append("MUL B").append('\n');

        } else if (operator.equals("/")) {
            code.append("DIV B").append('\n');

        } else if (operator.equals("%")) {
            // Since there is no direct instruction to support modulo
            code.append("DIV B").append('\n');  // A = A / B, quotient in A

            code.append("MOV C, A").append('\n');  // Copy quotient to C
            code.append("MUL B").append('\n');    // A = A * B, product in A

            code.append("SUB A, C").append('\n');    // A = A - C (A - (A / B) * B)
            // The result in A is now A % B (the remainder)
        }

        code.append("PUSH A").append('\n');
    }

    private void generateConstant(Node node) {
        String value = node.getToken().getLexeme();
        code.append("PUSH ").append(value).append("\n");
    }

    public String getCode() {
        return code.toString();
    }

}
