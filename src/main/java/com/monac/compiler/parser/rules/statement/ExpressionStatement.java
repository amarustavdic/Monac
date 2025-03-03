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
            return expression;
        } else {
            Token actual = parser.peek();
            parser.addError(new ParserException(
                    "Missing semicolon ';' at the end of the statement.",
                    actual.getLine(),
                    actual.getColumn(),
                    actual.getLexeme(),
                    "Expected ';' after an expression or statement.",
                    "Ensure your statement ends with a semicolon."
            ));
            parser.synchronize();
            return null;
        }
    }

}
