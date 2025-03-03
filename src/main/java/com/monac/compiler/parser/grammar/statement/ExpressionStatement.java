package com.monac.compiler.parser.grammar.statement;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.grammar.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class ExpressionStatement {

    // <expression-statement> ::= {<expression>}? ;

    public static Node parse(Parser parser) throws Exception {
        Node expression = Expression.parse(parser);
        if (parser.match(TokenType.SEMICOLON)) {
            Node result = new Node(NodeType.EXPRESSION_STATEMENT, 0, 0);
            if (expression != null) {
                result.setChildren(List.of(expression));
            }
            return result;
        } else {
            throw new Exception();
        }
    }

}
