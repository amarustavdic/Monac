package com.monac.compiler;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.lexer.Token;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.tree.nodes.ConstantNode;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String input = "234";

        Parser parser = new Parser(new Lexer(input));

        var ast = parser.parse();

        if (ast instanceof ConstantNode) {
            System.out.println("yay");
        }


    }
}

