package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.rules.other.Identifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class JumpStatement {

    // <jump-statement> ::= goto <identifier> ;
    //  | continue ;
    //  | break ;
    //  | return {<expression>}? ;

    public static Node parse(Parser parser) {

        if (parser.match(TokenType.RETURN)) {
            Token token = parser.previous(); // return token

            Node expression = Expression.parse(parser); // optional

            if (!parser.match(TokenType.SEMICOLON)) {
                parser.addError(null); // todo
                parser.synchronize();
                return null;
            }

            Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());

            if (expression != null) {
                result.setChildren(List.of(expression));
            }

            return result;
        }


        return null;
    }

}
