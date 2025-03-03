package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.operator.AssignmentOperator;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class AssignmentExpression {

    // <assignment-expression> ::= <conditional-expression>
    //| <unary-expression> <assignment-operator> <assignment-expression>

    public static Node parse(Parser parser) {

        Node ce = ConditionalExpression.parse(parser);
        if (ce != null) {
            return ce;
        } else {
            Node ue = UnaryExpression.parse(parser);
            if (ue != null) {
                Node ao = AssignmentOperator.parse(parser);
                if (ao != null) {
                    Node ae = AssignmentExpression.parse(parser);
                    if (ae != null) {
                        Node result = new Node(NodeType.ASSIGNMENT_EXPRESSION, ue.getLine(), ue.getColumn());
                        result.setChildren(List.of(ue, ao, ae));
                        return result;
                    } else {
                        Token actual = parser.peek();
                        parser.addError(new ParserException(
                                "Syntax Error: Expected an expression after assignment operator.",
                                actual.getLine(), actual.getColumn(),
                                actual.getLexeme(),
                                "Expected an expression (e.g., variable, function call, or literal).",
                                "Ensure that an assignment expression follows the assignment operator."
                        ));
                        parser.synchronize();
                        return null;
                    }
                } else {
                    Token actual = parser.peek();
                    parser.addError(new ParserException(
                            "Syntax Error: Expected an assignment operator (=, +=, -=, etc.).",
                            actual.getLine(), actual.getColumn(),
                            actual.getLexeme(),
                            "Expected an assignment operator.",
                            "Check that an assignment operator follows the left-hand operand."
                    ));
                    parser.synchronize();
                    return null;
                }
            } else {
                return null;
            }
        }

    }


}
