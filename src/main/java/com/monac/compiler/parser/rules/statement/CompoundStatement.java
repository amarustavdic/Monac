package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

public final class CompoundStatement {

    // <compound-statement> ::= { {<declaration>}* {<statement>}* }

    public static Node parse(Parser parser) {
        if (parser.match(TokenType.LBRACE)) {
            Token lBrace = parser.previous();

            // first 0 or more declarations, and 0 or more statements

            Token rBrace = parser.consume(TokenType.RBRACE, "");
            if (rBrace == null) {
                // in reality should handle error here
                return null;
            }

            Node result = new Node(NodeType.COMPOUND_STATEMENT, lBrace.getLine(), lBrace.getColumn());
            // result.setChildren(List.of());
        }
        return null;
    }

}
