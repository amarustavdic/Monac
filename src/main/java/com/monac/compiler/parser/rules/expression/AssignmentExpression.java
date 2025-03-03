package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.operator.AssignmentOperator;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class AssignmentExpression {

    // <assignment-expression> ::= <conditional-expression>
    //| <unary-expression> <assignment-operator> <assignment-expression>

    public static Node parse(Parser parser) {

//        Node left = UnaryExpression.parse(parser);
//        if (left != null) {
//            Node middle = AssignmentOperator.parse(parser);
//            if (middle != null) {
//                Node right = AssignmentExpression.parse(parser);
//                if (right != null) {
//                    Node result = new Node(NodeType.ASSIGNMENT_EXPRESSION, left.getLine(), left.getColumn());
//                    result.setChildren(List.of(left, middle, right));
//                    return result;
//                } else {
//                    Token actual = parser.peek();
//                    // TODO: Make nice error message
//                    parser.addError(null);
//                    parser.synchronize();
//                }
//            } else {
//                Token actual = parser.peek();
//                // TODO: Make nice error message
//                parser.addError(null);
//                parser.synchronize();
//            }
//        } else {
//            return ConditionalExpression.parse(parser);
//        }
//        return null; // is this okay?
        return ConditionalExpression.parse(parser);
    }

}
