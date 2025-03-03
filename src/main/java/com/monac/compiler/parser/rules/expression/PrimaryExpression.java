package com.monac.compiler.parser.rules.expression;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.constant.Constant;
import com.monac.compiler.parser.rules.other.Identifier;
import com.monac.compiler.parser.rules.other.String;
import com.monac.compiler.parser.tree.Node;

public final class PrimaryExpression {

    public static Node parse(Parser parser) {

        if (parser.match(TokenType.LEFT_PARENTHESIS)) {

            // TODO: This one might ... idk what yet
            Node expression = Expression.parse(parser);
            // Figure out how to handle expression, does it return null possibly?

            if (expression != null) {
                if (parser.match(TokenType.RIGHT_PARENTHESIS)) {
                    return expression;
                } else {
                    Token token = parser.peek();
                    parser.addError(new ParserException(
                            "Expected ')' after expression.",
                            token.getLine(), token.getColumn(),
                            token.getLexeme(), ")"
                    ));
                    parser.synchronize();
                }
            } else {
                parser.addError(null);
                parser.synchronize();
            }
        }

        Node identifier = Identifier.parse(parser);
        if (identifier != null) return identifier;

        Node constant = Constant.parse(parser);
        if (constant != null) return constant;

        return String.parse(parser);
    }
}
