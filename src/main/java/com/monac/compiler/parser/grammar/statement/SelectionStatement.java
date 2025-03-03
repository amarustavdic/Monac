package com.monac.compiler.parser.grammar.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.grammar.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;
import java.util.ArrayList;
import java.util.List;

public final class SelectionStatement {

    public static Node parse(Parser parser) {
        if (parser.match(TokenType.IF)) {
            Token ifToken = parser.previous();

            parser.consume(TokenType.LPAREN, "Expected '('");
            Node expr = Expression.parse(parser);

            if (expr == null) {
                // Handle error (Expression should not be null)
                return null;
            }

            parser.consume(TokenType.RPAREN, "Expected ')'");
            Node stmt = Statement.parse(parser);

            if (stmt == null) {
                // Handle error (Statement should not be null)
                return null;
            }

            List<Node> children = new ArrayList<>();
            children.add(expr);
            children.add(stmt);

            if (parser.match(TokenType.ELSE)) {
                Node elseStmt = Statement.parse(parser);
                if (elseStmt == null) {
                    // Handle error (Else statement should not be null)
                    return null;
                }
                children.add(elseStmt);
            }

            Node result =  new Node(NodeType.SELECTION_STATEMENT, ifToken.getLine(), ifToken.getColumn());
            result.setChildren(children);
            return result;

        } else if (parser.match(TokenType.SWITCH)) {
            Token switchToken = parser.previous();

            parser.consume(TokenType.LPAREN, "Expected '('");
            Node expr = Expression.parse(parser);

            if (expr == null) {
                // Handle error
                return null;
            }

            parser.consume(TokenType.RPAREN, "Expected ')'");
            Node stmt = Statement.parse(parser);

            if (stmt == null) {
                // Handle error
                return null;
            }

            List<Node> children = new ArrayList<>();
            children.add(expr);
            children.add(stmt);

            Node result =  new Node(NodeType.SELECTION_STATEMENT, switchToken.getLine(), switchToken.getColumn());
            result.setChildren(children);
            return result;
        }
        return null;
    }
}
