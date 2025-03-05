package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
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

        // Process goto
        if (parser.match(TokenType.GOTO)) {
            Token token = parser.previous();
            Node identifier = Identifier.parse(parser);

            // Expect identifier after goto statement
            if (identifier == null) {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Expected <identifier> after goto, e.g.: 'goto <identifier>;'.",
                        actual.getLine(),
                        actual.getColumn(),
                        actual.getLexeme(),
                        "<identifier>",
                        "Make sure that goto keyword is followed by an <identifier>."
                ));
                return null;
            }

            // Expect semicolon
            if (!foundSemicolon(parser, token)) return null;

            Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());
            result.setChildren(List.of(identifier));

            return result;
        }

        // Process break or continue
        if (parser.match(TokenType.BREAK, TokenType.CONTINUE)) {
            Token token = parser.previous();

            // Expect semicolon
            if (!foundSemicolon(parser, token)) return null;

            Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());

            return result;
        }

        // Process return
        if (parser.match(TokenType.RETURN)) {
            Token token = parser.previous();
            Node expression = null; // optional

            // Check if an expression follows `return`
            if (!parser.check(TokenType.SEMICOLON)) {
                expression = Expression.parse(parser);
            }

            // Expect semicolon
            if (!foundSemicolon(parser, token)) return null;


            Node result = new Node(NodeType.JUMP_STATEMENT, token.getLine(), token.getColumn());
            result.setLiteral(token.getLexeme());

            if (expression != null) result.setChildren(List.of(expression));
            return result;
        }

        // No valid jump statement found
        return null;
    }

    private static boolean foundSemicolon(Parser parser, Token token) {
        if (!parser.match(TokenType.SEMICOLON)) {
            Token actual = parser.peek();
            parser.addError(new ParserException(
                    "Expected ';' at the end of " + token.getLexeme() + " statement.",
                    actual.getLine(),
                    actual.getColumn(),
                    actual.getLexeme(),
                    ";",
                    "Make sure that you close every statement with ';'."
            ));
            parser.synchronize();
            return false;
        }
        return true;
    }

}
