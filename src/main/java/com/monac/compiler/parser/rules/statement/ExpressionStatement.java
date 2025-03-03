package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class ExpressionStatement {

    // <expression-statement> ::= {<expression>}? ;

    public static Node parse(Parser parser) {
        Node expression = Expression.parse(parser);
        if (parser.match(TokenType.SEMICOLON)) {

//            Node result = new Node(NodeType.EXPRESSION_STATEMENT, 0, 0);
//            result.setLiteral("quick fix?");
//            if (expression != null) {
//                result.setChildren(List.of(expression));
//            }

            return expression;
        } else {
            Token actual = parser.peek();
            parser.addError(new ParserException(
                    "",
                    actual.getLine(),
                    actual.getColumn(),
                    actual.getLexeme(),
                    "",
                    ""
            ));
            parser.synchronize();
            return null;
        }
    }

}
