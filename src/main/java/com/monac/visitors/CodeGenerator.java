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
            case AND_EXPRESSION -> generateAndExpression(node);
            case EQUALITY_EXPRESSION -> generateEqualityExpression(node);
            case RELATIONAL_EXPRESSION -> generateRelationalExpression(node);
            case SHIFT_EXPRESSION -> generateShiftExpression(node);
            case ADDITIVE_EXPRESSION -> generateAdditiveExpression(node);
            case MULTIPLICATIVE_EXPRESSION -> generateMultiplicativeExpression(node);
            case CONSTANT -> generateConstant(node);
        }

    }

    private void generateAndExpression(Node node) {
        // TODO: TBD how
    }

    private void generateEqualityExpression(Node node) {
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


    private void generateRelationalExpression(Node node) {
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

        // Code for the false case (push 0 if comparison fails)
        code.append("PUSH 0").append('\n');
        code.append("JMP ").append(endLabel).append('\n'); // Jump to end to avoid overwriting the result

        // true case: push 1 if comparison is true
        code.append(trueLabel).append(": ").append('\n');
        code.append("PUSH 1").append('\n');

        // end
        code.append(endLabel).append(": ").append('\n');
    }

    private void generateShiftExpression(Node node) {
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

        code.append("PUSH A").append('\n');
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
        code.append("PUSH ").append(value).append('\n');
    }

    public String getCode() {
        return code.toString();
    }

}
