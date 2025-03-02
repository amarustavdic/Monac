package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.Rule;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;
import com.monac.compiler.parser.tree.nodes.expression.ShiftExpressionNode;

import java.util.ArrayList;
import java.util.List;

public class ShiftExpressionRule implements Rule {

    private final Rule terminal;

    public ShiftExpressionRule(Rule terminal) {
        this.terminal = terminal;
    }

    //<shift-expression> ::= <additive-expression>
    //| <shift-expression> << <additive-expression>
    //| <shift-expression> >> <additive-expression>

    // --------------------------------------------

    //<shift-expression> ::= <additive-expression> <shift-expression'>
    //
    //<shift-expression'> ::= << <additive-expression>
    //                      | >> <additive-expression>
    //                      | ε


    @Override
    public Node parse(Parser parser) {
        Node left = terminal.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    private Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.SHL, TokenType.SHR)) {
            Token operator = parser.previous();

            Node right = terminal.parse(parser);
            if (right == null) return left;

            ShiftExpressionNode node = new ShiftExpressionNode(
                    NodeType.SHIFT_EXPRESSION,
                    operator.getLine(),
                    operator.getColumn()
            );

            List<Node> children = new ArrayList<>();
            children.add(left);
            children.add(right);
            node.setChildren(children);

            left = node;
        }

        return left;
    }

}
