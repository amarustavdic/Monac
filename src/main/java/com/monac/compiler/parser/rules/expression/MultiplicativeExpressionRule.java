package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.Rule;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;
import com.monac.compiler.parser.tree.nodes.expression.MultiplicativeExpressionNode;

import java.util.ArrayList;
import java.util.List;

public class MultiplicativeExpressionRule implements Rule {

    // Original BNF but problem is left recursion

    // <multiplicative-expression> ::= <cast-expression>
    //| <multiplicative-expression> * <cast-expression>
    //| <multiplicative-expression> / <cast-expression>
    //| <multiplicative-expression> % <cast-expression>


    // <multiplicative-expression> ::= <cast-expression> <multiplicative-expression'>
    //
    // <multiplicative-expression'> ::= * <cast-expression>
    //                                | / <cast-expression>
    //                                | % <cast-expression>
    //                                | ε

    // This above should be it, I mean without left recursion

    private final Rule terminal;

    public MultiplicativeExpressionRule(Rule terminal) {
        this.terminal = terminal;
    }

    @Override
    public Node parse(Parser parser) {

        // Parse left operand first
        Node left = terminal.parse(parser);
        if (left == null) {
            return null; // Error: expected a valid operand
        }

        return parsePrime(parser, left);
    }

    private Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.MUL)) {
            Token operator = parser.previous();

            Node right = terminal.parse(parser);
            if (right == null) {
                // TODO: Error handling or synchronization
                return left;
            }

            MultiplicativeExpressionNode node = new MultiplicativeExpressionNode(
                    NodeType.MULTIPLICATIVE_EXPRESSION,
                    operator.getLine(),
                    operator.getColumn()
            );

            List<Node> children = new ArrayList<>();
            children.add(left);
            children.add(right);
            node.setChildren(children);

            // The new node becomes the left operand for further operations
            left = node;
        }

        return left;
    }

}
