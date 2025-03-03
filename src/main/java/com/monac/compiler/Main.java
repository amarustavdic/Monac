package com.monac.compiler;

import com.monac.compiler.lexer.Lexer;
import com.monac.compiler.parser.Parser;
import com.monac.compiler.util.LiteralPrinter;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print(">>> ");

        String input;
        while ((input = sc.nextLine()) != null) {
            Parser parser = new Parser(new Lexer(input));
            var ast = parser.parse();

            if (ast != null) {
                ast.accept(new LiteralPrinter());
            }

            System.out.print(">>> ");
        }
    }
}

