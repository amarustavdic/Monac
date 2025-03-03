package com.monac.compiler;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.ParserException;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.util.LiteralPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        parseFromFile("example.c");


    }


    public static void parseFromFile(String filename) throws IOException {
        String content = Files.readString(Path.of("src/main/resources/" + filename));

        Parser parser = new Parser(new Lexer(content));
        Node tree = parser.parse();

        tree.accept(new LiteralPrinter());
    }


    public static void repl() {
        Scanner sc = new Scanner(System.in);
        System.out.print(">>> ");
        String input;
        while ((input = sc.nextLine()) != null) {
            Parser parser = new Parser(new Lexer(input));
            var ast = parser.parse();
            for (ParserException error : parser.getErrors()) {
                System.out.println(error);
            }
            if (ast != null) {
                ast.accept(new LiteralPrinter());
            }
            System.out.print(">>> ");
        }
    }

}

