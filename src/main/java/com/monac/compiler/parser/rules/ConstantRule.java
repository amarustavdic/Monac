package com.monac.compiler.parser.rules;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.NodeType;

// TODO: Refactor class Constant since by c-lang BNF this should be nonterminal

public class ConstantRule implements Rule {


    @Override
    public Node parse(Parser parser) {

        if (parser.match(TokenType.INTEGER_CONSTANT)) {
            Token token = parser.previous();
            Node node = new Node(NodeType.CONSTANT, token.getLine(), token.getColumn());
            node.setLiteral(Integer.parseInt(token.getLexeme()));
            return node;
        }

        // if it is not matching, then would be better to throw some sort
        // of exception and then caller of this parse method, is going to
        // synchronize

        // But for now it is simply returning null
        return null;
    }

}
