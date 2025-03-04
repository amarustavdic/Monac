package com.monac.compiler.parser.rules.statement;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.declaration.Declaration;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class CompoundStatement {

    // <compound-statement> ::= { {<declaration>}* {<statement>}* }

    public static Node parse(Parser parser) {

        if (parser.match(TokenType.LBRACE)) {
            Token lBrace = parser.previous();
            List<Node> children = new ArrayList<>();

            // Parse multiple declarations
            Node declaration;
            while ((declaration = Declaration.parse(parser)) != null) {
                children.add(declaration);
            }

            // Parse multiple statements
            Node statement;
            while ((statement = Statement.parse(parser)) != null) {
                children.add(statement);
            }

            if (parser.match(TokenType.RBRACE)) {
                Node result = new Node(NodeType.COMPOUND_STATEMENT, lBrace.getLine(), lBrace.getColumn());
                result.setChildren(children);
                return result;
            } else {
                Token actual = parser.peek();
                parser.addError(new ParserException(
                        "Syntax Error: Missing closing '}' for compound statement.",
                        actual.getLine(),
                        actual.getColumn(),
                        actual.getLexeme(),
                        "Expected '}' to close the compound block.",
                        "Ensure every '{' has a matching '}'."
                ));
            }
        }
        return null;
    }

}
