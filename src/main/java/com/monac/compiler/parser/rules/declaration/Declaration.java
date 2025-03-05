package com.monac.compiler.parser.rules.declaration;

import com.monac.compiler.lexer.Token;
import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.rules.declarator.InitDeclarator;
import com.monac.compiler.parser.rules.specifier.DeclarationSpecifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses declaration.
 *
 * <pre>{@code
 * <declaration> ::= {<declaration-specifier>}+ {<init-declarator>}* ;
 * }</pre>
 */
public final class Declaration {


    public static Node parse(Parser parser) {

        // One or more
        List<Node> dss = new ArrayList<>();
        Node ds = DeclarationSpecifier.parse(parser);
        if (ds != null) dss.add(ds);
        while ((ds = DeclarationSpecifier.parse(parser)) != null) dss.add(ds);

        // If none then this is not it
        if (dss.isEmpty()) return null;

        // Zero or more
        List<Node> ids = new ArrayList<>();
        Node id;
        while ((id = InitDeclarator.parse(parser)) != null) ids.add(id);

        // Expect semicolon at the end
        if (!parser.match(TokenType.SEMICOLON)) {
            Token actual = parser.peek();
            parser.addError(new ParserException(
                    "Expected ';' at the end of the declaration.",
                    actual.getLine(),
                    actual.getColumn(),
                    actual.getLexeme(),
                    "';'",
                    "Ensure that the declaration ends with a semicolon, e.g., 'int x;'."
            ));
            parser.synchronize();
            return null;
        }

        // Combine all
        dss.addAll(ids);

        // Make ast node
        Node result = new Node(NodeType.DECLARATION, dss.get(0).getLine(), dss.get(0).getColumn());
        result.setChildren(dss);
        return result;
    }

}
