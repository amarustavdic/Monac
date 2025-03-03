package com.monac.compiler.parser.grammar.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class MultiplicativeExpression {

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

    public static Node parse(Parser parser) {

        Node left = CastExpression.parse(parser);
        if (left == null) return null;
        return parsePrime(parser, left);
    }

    private static Node parsePrime(Parser parser, Node left) {

        while (parser.match(TokenType.MUL, TokenType.DIV, TokenType.MOD)) {
            Token operator = parser.previous();

            Node right = CastExpression.parse(parser);
            if (right == null) return left;

            Node node = new Node(NodeType.MULTIPLICATIVE_EXPRESSION, operator.getLine(), operator.getColumn());
            node.setLiteral(operator.getLexeme());

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
