package com.monac.visitors;

import com.monac.parser.Node;

public class CodeGenerator implements Visitor {

    private final StringBuilder code = new StringBuilder();
    private int labelCounter = 0; // Counter for generating unique labels


    public CodeGenerator() {
        code.append("MOV SP, 0x0FFF")
                .append("   ; initialize stack")
                .append('\n');
    }

    @Override
    public void visit(Node node) {

        switch (node.getType()) {
            case AND_EXPRESSION -> andExpression(node);
            case EQUALITY_EXPRESSION -> equalityExpression(node);
            case RELATIONAL_EXPRESSION -> relationalExpression(node);
            case SHIFT_EXPRESSION -> shiftExpression(node);
            case ADDITIVE_EXPRESSION -> additiveExpression(node);
            case MULTIPLICATIVE_EXPRESSION -> multiplicativeExpression(node);
            case CONSTANT -> constant(node);
            case SELECTION_STATEMENT -> selectionStatement(node);
        }

    }


    private void selectionStatement(Node node) {

        for (Node child : node.getChildren()) {
            switch (child.getType()) {
                case RELATIONAL_EXPRESSION -> relationalExpression(child);
                case JUMP_STATEMENT -> jumpStatement(child);
            }
        }

    }

    private void jumpStatement(Node node) {

        switch (node.getValue()) {
            case "goto" -> handleGoto(node);
        }

    }

    private void handleGoto(Node node) {
        // TODO: Here should probably be checking if label is defined prior
        code.append("JMP ").append(node.getToken().getLexeme());
    }

    private void andExpression(Node node) {
        // TODO: TBD how
    }

    private void equalityExpression(Node node) {
        Node left = node.getChildren().get(0);
        Node right = node.getChildren().get(1);

        visit(left);
        visit(right);

        code.append("POP A").append('\n'); // Right operand goes to A
        code.append("POP B").append('\n'); // Left operand goes to B

        String operator = node.getValue(); // The operator "==" or "!="
        String trueLabel = "true" + labelCounter++;
        String endLabel = "false" + labelCounter++;

        switch (operator) {
            case "==":
                code.append("CMP B, A").append('\n'); // Compare left (B) with right (A)
                code.append("JZ ").append(trueLabel).append("   ; == comparison").append('\n');
                break;

            case "!=":
                code.append("CMP B, A").append('\n'); // Compare left (B) with right (A)
                code.append("JNZ ").append(trueLabel).append("   ; != comparison").append('\n');
                break;

            default:
                System.out.println("Unsupported equality operator: " + operator);
        }

        code.append("PUSH 0").append('\n');
        code.append("JMP ").append(endLabel).append('\n');

        code.append(trueLabel).append(":").append('\n');
        code.append("PUSH 1").append('\n');

        code.append(endLabel).append(":").append('\n');
    }


    private void relationalExpression(Node node) {
        Node left = node.getChildren().get(0);
        Node right = node.getChildren().get(1);

        visit(left);
        visit(right);

        code.append("POP B").append('\n'); // right
        code.append("POP A").append('\n'); // left

        String operator = node.getValue(); // "<", ">", "<=", ">="
        String trueLabel = "true" + labelCounter++;
        String endLabel = "false" + labelCounter++;


        switch (operator) {
            case "<":
                code.append("CMP A, B").append('\n'); // Compare A with B (A < B)
                code.append("JB ")
                        .append(trueLabel)
                        .append("   ; < comparison")
                        .append('\n'); // Jump to TRUE if A < B
                break;

            case ">":
                code.append("CMP A, B").append('\n'); // Compare A with B (A > B)
                code.append("JA ")
                        .append(trueLabel)
                        .append("   ; > comparison")
                        .append('\n'); // Jump to TRUE if A > B
                break;

            case "<=":
                code.append("CMP A, B").append('\n'); // Compare A with B (A <= B)
                code.append("JBE ")
                        .append(trueLabel)
                        .append("   ; <= comparison")
                        .append('\n'); // Jump to TRUE if A <= B
                break;

            case ">=":
                code.append("CMP A, B").append('\n'); // Compare A with B (A >= B)
                code.append("JAE ")
                        .append(trueLabel)
                        .append("   ; >= comparison")
                        .append('\n'); // Jump to TRUE if A >= B
                break;

            default:
                System.out.println("Unsupported relational operator: " + operator);
        }

        code.append("JMP ").append(endLabel).append('\n'); // Jump to end to avoid overwriting the result

        code.append(trueLabel).append(": ").append('\n');

        // end
        code.append(endLabel).append(": ").append('\n');
    }

    private void shiftExpression(Node node) {
        Node left = node.getChildren().get(0);
        Node right = node.getChildren().get(1);

        visit(left);
        visit(right);

        String operator = node.getValue();

        code.append("POP B").append('\n'); // right operand
        code.append("POP A").append('\n'); // left operand

        if (operator.equals("<<")) {
            code.append("SHL A, B").append('\n');

        } else if (operator.equals(">>")) {
            code.append("SHR A, B").append('\n');
        }

        code.append("PUSH A").append('\n');
    }

    private void additiveExpression(Node node) {
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

        code.append("PUSH A").append('\n');
    }

    private void multiplicativeExpression(Node node) {
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

    private void constant(Node node) {
        String value = node.getToken().getLexeme();
        code.append("PUSH ").append(value).append('\n');
    }

    public String getCode() {
        return code.toString();
    }

}
