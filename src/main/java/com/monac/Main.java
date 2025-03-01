package com.monac;

import com.monac.lexer.Lexer;
import com.monac.lexer.Token;
import com.monac.parser.Node;
import com.monac.parser.Parser;
import com.monac.visitors.CodeGenerator;
import com.monac.visitors.Printer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        String input = Files.readString(Path.of("./src/main/resources/src/main.c"));

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token.getType());
        }

        Parser parser = new Parser(tokens);
        Node ast = parser.parse();

        if (parser.hasErrors()) {
            System.err.println();

            // For now showing only first encountered error cuz, have not
            // implemented parser synchronization yet (would be nice)
            System.err.println(parser.getErrors().getFirst());

        } else {
            System.out.println();
            ast.accept(new Printer());

            CodeGenerator codeGenerator = new CodeGenerator();
            ast.accept(codeGenerator);

            String code = codeGenerator.getCode();

            Path outputPath = Path.of("./src/main/resources/out/main.s");
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                writer.write(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
