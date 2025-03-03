package com.monac.compiler.parser.rules.operator;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

public final class AssignmentOperator {

    // <assignment-operator> ::= =
    //| *=
    //| /=
    //| %=
    //| +=
    //| -=
    //| <<=
    //| >>=
    //| &=
    //| ^=
    //| |=

    public static Node parse(Parser parser) {
        if (parser.match(TokenType.ASSIGN, TokenType.MUL_ASSIGN, TokenType.DIV_ASSIGN,
                TokenType.MOD_ASSIGN, TokenType.INC_ASSIGN, TokenType.DEC_ASSIGN,
                TokenType.SHL_ASSIGN, TokenType.SHR_ASSIGN, TokenType.AND_ASSIGN,
                TokenType.XOR_ASSIGN, TokenType.OR_ASSIGN
        )) {
            Token token = parser.previous();
            Node result = new Node(NodeType.ASSIGNMENT_OPERATOR, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());
            return result;
        }
        return null;
    }

}
