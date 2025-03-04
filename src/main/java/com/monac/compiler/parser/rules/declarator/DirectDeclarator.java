package com.monac.compiler.parser.rules.declarator;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.list.ParameterTypeList;
import com.monac.compiler.parser.rules.expression.ConstantExpression;
import com.monac.compiler.parser.rules.other.Identifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class DirectDeclarator {

    /*
    direct-declarator ->
	      identifier direct-declarator-prime
	    | "(" declarator ")"

    direct-declarator-prime ->
	      "[" {constant-expression}? "]" direct-declarator-prime
	    | "(" parameter-type-list ")" direct-declarator-prime
	    | "(" {identifier}* ")" direct-declarator-prime
	    | ɛ

    declarator -> {pointer}? direct-declarator
     */

    public static Node parse(Parser parser) {

        Node left;
        if (parser.match(TokenType.LPAREN)) {
            left = Declarator.parse(parser);
            if (!parser.match(TokenType.RPAREN)) {
                // todo error sync; expecting right parenthesis
                parser.addError(null);
                parser.synchronize();
                return null;
            }
            return left;
        } else {
            left = Identifier.parse(parser);
        }

        if (left == null) return null;
        return parsePrime(parser, left);
    }

    private static Node parsePrime(Parser parser, Node left) {
        while (parser.match(TokenType.LPAREN)) {
            List<Node> children = new ArrayList<>();
            children.add(left);

            // Check for an empty parameter list
            if (parser.match(TokenType.RPAREN)) {
                // Empty parameter list case (e.g., int main())
            } else {
                Node identifier;
                while ((identifier = Identifier.parse(parser)) != null) {
                    children.add(identifier);
                }

                if (!parser.match(TokenType.RPAREN)) {
                    parser.addError(null);
                    parser.synchronize(); // Error recovery
                    return null;
                }
            }

            Node result = new Node(NodeType.DIRECT_DECLARATOR, 0, 0);
            result.setChildren(children);

            left = result;
        }
        return left;
    }


}
