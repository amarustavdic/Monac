package com.monac.compiler;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.lexer.Token;
import com.monac.compiler.parser.Node;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.visitors.Printer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        String input = Files.readString(Path.of("./src/main/resources/src/main.c"));

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token.getType());
        }

        Parser parser = new Parser(tokens);
        Node ast = parser.parse();

        System.out.println();

        ast.accept(new Printer());

    }

}
