package com.monac;

import com.monac.lexer.Lexer;
import com.monac.lexer.Token;
import com.monac.parser.Node;
import com.monac.parser.Parser;
import com.monac.visitors.Printer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        String input = Files.readString(Path.of("./src/main/resources/src/main.m"));

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token.getType());
        }

        Parser parser = new Parser(tokens);
        Node ast = parser.parse();

        ast.accept(new Printer());

    }

}
