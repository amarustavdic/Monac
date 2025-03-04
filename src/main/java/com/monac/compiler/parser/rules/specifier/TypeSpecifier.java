package com.monac.compiler.parser.rules.specifier;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.TypedefName;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

public final class TypeSpecifier {

    // <type-specifier> ::= void
    //| char
    //| short
    //| int
    //| long
    //| float
    //| double
    //| signed
    //| unsigned
    //| <struct-or-union-specifier>
    //| <enum-specifier>
    //| <typedef-name>

    public static Node parse(Parser parser) {

        // For now just handling void, int and char as types

        if (parser.match(TokenType.VOID, TokenType.CHAR, TokenType.INT)) {
            Token token = parser.previous();
            Node result = new Node(NodeType.TYPE_SPECIFIER, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());
            return result;
        }
        return null;
    }

}
