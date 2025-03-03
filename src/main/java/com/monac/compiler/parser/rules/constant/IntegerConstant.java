package com.monac.compiler.parser.rules.constant;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

public final class IntegerConstant {

    public static Node parse(Parser parser) {
        if (parser.match(TokenType.INTEGER_CONSTANT)) {
            Token token = parser.previous();
            Node node = new Node(NodeType.INTEGER_CONSTANT, token.getLine(), token.getColumn());
            node.setLiteral(Integer.parseInt(token.getLexeme()));
            return node;
        }
        return null;
    }

}
