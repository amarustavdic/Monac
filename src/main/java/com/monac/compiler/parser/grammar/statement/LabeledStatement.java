package com.monac.compiler.parser.grammar.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.grammar.expression.ConstantExpression;
import com.monac.compiler.parser.grammar.other.Identifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class LabeledStatement {

    // <labeled-statement> ::= <identifier> : <statement>
    //| case <constant-expression> : <statement>
    //| default : <statement>

    public static Node parse(Parser parser) throws Exception {

        Node identifier = Identifier.parse(parser);
        if (identifier != null) {
            if (parser.match(TokenType.COLON)) {
                Node statement = Statement.parse(parser);
                if (statement != null) {
                    Node result = new Node(NodeType.LABELED_STATEMENT, identifier.getLine(), identifier.getColumn());
                    result.setChildren(List.of(identifier,statement));
                    return result;
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        }

        if (parser.match(TokenType.CASE)) {
            Token token = parser.previous();
            Node constantExpression = ConstantExpression.parse(parser);
            if (constantExpression != null) {
                if (parser.match(TokenType.COLON)) {
                    Node statement = Statement.parse(parser);
                    if (statement != null) {
                        Node result = new Node(NodeType.LABELED_STATEMENT, token.getLine(), token.getLine());
                        result.setLiteral(token.getLexeme());
                        result.setChildren(List.of(constantExpression,statement));
                        return result;
                    } else {
                        throw new Exception();
                    }
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        }

        if (parser.match(TokenType.DEFAULT)) {
            Token token = parser.previous();
            if (parser.match(TokenType.COLON)) {
                Node statement = Statement.parse(parser);
                if (statement != null) {
                    Node result = new Node(NodeType.LABELED_STATEMENT, token.getLine(), token.getLine());
                    result.setLiteral(token.getLexeme());
                    result.setChildren(List.of(statement));
                    return result;
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        }

        return null;
    }

}
