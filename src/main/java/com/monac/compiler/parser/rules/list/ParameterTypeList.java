package com.monac.compiler.parser.rules.list;

import com.monac.compiler.lexer.TokenType;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

public final class ParameterTypeList {

    // <parameter-type-list> ::= <parameter-list>
    //| <parameter-list> , ...

    public static Node parse(Parser parser) {

        List<Node> children = new ArrayList<>();

        Node parameterList = ParameterList.parse(parser);
        if (parameterList == null) return null; // Just try something else, this is not it
        children.add(parameterList);

        // Parse the rest parameter lists if exist
        while (parser.match(TokenType.COMMA)) {
            parameterList = ParameterList.parse(parser);

            // Expect it after ','
            if (parameterList == null) {
                parser.addError(null); // todo
                parser.synchronize();
                return null;
            }
            children.add(parameterList);
        }

        Node result = new Node(NodeType.PARAMETER_TYPE_LIST, children.getFirst().getLine(), children.getLast().getLine());
        result.setChildren(children);

        return result;
    }

}
