package com.monac.compiler.parser.symbols;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.NodeType;
import com.monac.compiler.parser.tree.nodes.ConstantNode;

public class Constant implements Terminal {

    @Override
    public Node parse(Parser parser) {

        if (parser.match(TokenType.INTEGER_CONSTANT)) {
            Token token = parser.previous();
            return new ConstantNode(
                    NodeType.CONSTANT,
                    token.getLine(),
                    token.getColumn(),
                    (int) token.getLiteral()
            );
        }

        // if it is not matching, then would be better to throw some sort
        // of exception and then caller of this parse method, is going to
        // synchronize

        // But for now it is simply returning null
        return null;
    }

}
