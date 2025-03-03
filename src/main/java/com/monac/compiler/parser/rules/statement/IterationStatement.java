package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.expression.Expression;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class IterationStatement {

    // <iteration-statement> ::= while ( <expression> ) <statement>
    //| do <statement> while ( <expression> ) ;
    //| for ( {<expression>}? ; {<expression>}? ; {<expression>}? ) <statement>

    public static Node parse(Parser parser) throws Exception {

        if (parser.match(TokenType.WHILE)) {
            Token token = parser.previous();
            if (parser.match(TokenType.LEFT_PARENTHESIS)) {
                Node expression = Expression.parse(parser);
                if (expression != null) {
                    if (parser.match(TokenType.RIGHT_PARENTHESIS)) {
                        Node statement = Statement.parse(parser);
                        if (statement != null) {
                            Node result = new Node(NodeType.ITERATOR_STATEMENT, token.getLine(), token.getColumn());
                            result.setLiteral(token.getLexeme());
                            result.setChildren(List.of(expression, statement));
                            return result;
                        } else {
                            throw new Exception();
                        }
                    } else {
                        throw new Exception("Expecting ')' after expression.");
                    }
                } else {
                    throw new Exception("Expression expected");
                }
            } else {
                throw new Exception("Expecting '(' after 'while' keyword.");
            }
        }

        if (parser.match(TokenType.DO)) {
            Token token = parser.previous();
            Node statement = Statement.parse(parser);
            if (statement != null) {
                if (parser.match(TokenType.WHILE)) {
                    if (parser.match(TokenType.LEFT_PARENTHESIS)) {
                        Node expression = Expression.parse(parser);
                        if (expression != null) {
                            if (parser.match(TokenType.RIGHT_PARENTHESIS)) {
                                if (parser.match(TokenType.SEMICOLON)) {
                                    Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getColumn());
                                    result.setLiteral(token.getLexeme());
                                    result.setChildren(List.of(statement, expression));
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

        if (parser.match(TokenType.FOR)) {
            Token token = parser.previous();
            if (parser.match(TokenType.LEFT_PARENTHESIS)) {
                Node expression1 = Expression.parse(parser);
                if (parser.match(TokenType.SEMICOLON)) {
                    Node expression2 = Expression.parse(parser);
                    if (parser.match(TokenType.SEMICOLON)) {
                        Node expression3 = Expression.parse(parser);
                        if (parser.match(TokenType.RIGHT_PARENTHESIS)) {
                            Node statement = Statement.parse(parser);
                            if (statement == null) {
                                throw new Exception();
                            }
                            Node result = new Node(NodeType.ITERATOR_STATEMENT, token.getLine(), token.getColumn());
                            result.setLiteral(token.getLexeme());
                            List<Node> children = new ArrayList<>();
                            if (expression1 != null) children.add(expression1);
                            if (expression2 != null) children.add(expression2);
                            if (expression3 != null) children.add(expression3);
                            children.add(statement);
                            result.setChildren(children);
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
            } else {
                throw new Exception();
            }
        }

        return null;
    }

}
