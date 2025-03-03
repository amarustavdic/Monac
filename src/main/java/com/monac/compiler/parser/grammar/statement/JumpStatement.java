package com.monac.compiler.parser.grammar.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.grammar.expression.Expression;
import com.monac.compiler.parser.grammar.other.Identifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

public final class JumpStatement {

    // <jump-statement> ::= goto <identifier> ;
    //  | continue ;
    //  | break ;
    //  | return {<expression>}? ;

    public static Node parse(Parser parser) throws Exception {

        if (parser.match(TokenType.GOTO)) {
            Token token = parser.previous();
            Node identifier = Identifier.parse(parser);
            if (identifier != null) {
                if (parser.match(TokenType.SEMICOLON)) {
                    Node result = new Node(NodeType.JUMP_STATEMENT, identifier.getLine(), identifier.getColumn());
                    result.setLiteral(token.getLexeme());
                    result.setChildren(List.of(identifier));
                    return result;
                } else {
                    throw new Exception("Expected ';' after 'goto <identifier>'.");
                }
            } else {
                throw new Exception("Expected identifier after 'goto' statement.");
            }
        }

        if (parser.match(TokenType.CONTINUE)) {
            Token token = parser.previous();
            if (parser.match(TokenType.SEMICOLON)) {
                Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getLine());
                result.setLiteral(token.getLexeme());
                return result;
            } else {
                throw new Exception("Expected ';' after 'continue'.");
            }
        }

        if (parser.match(TokenType.BREAK)) {
            Token token = parser.previous();
            if (parser.match(TokenType.SEMICOLON)) {
                Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getLine());
                result.setLiteral(token.getLexeme());
                return result;
            } else {
                throw new Exception("Expected ';' after 'break'.");
            }
        }

        if (parser.match(TokenType.RETURN)) {
            Token token = parser.previous();
            Node expression = Expression.parse(parser);
            Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getLine());
            result.setLiteral(token.getLexeme());
            if (expression != null) {
                result.setChildren(List.of(expression));
            }
            return result;
        }

        return null;
    }

}
