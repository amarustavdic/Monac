package com.monac.compiler.parser.rules.specifier;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

public final class StructOrUnion {

    public static Node parse(Parser parser) {
        if (parser.match(TokenType.STRUCT, TokenType.UNION)) {
            Token token = parser.previous();
            Node result = new Node(NodeType.STRUCT_OR_UNION, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());
            return result;
        }
        return null;
    }

}
