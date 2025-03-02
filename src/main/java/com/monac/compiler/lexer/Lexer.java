package com.monac.compiler.lexer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Lexer {

    private final Map<TokenType, Pattern> patterns = new LinkedHashMap<>();
    private final List<Token> tokens = new ArrayList<>();
    private final String input;
    private int cursor = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String input) {
        this.input = input;

        // Literals
        this.patterns.put(TokenType.INTEGER_CONSTANT, Pattern.compile("\\d+"));

        // Arithmetic
        this.patterns.put(TokenType.MUL, Pattern.compile("\\*"));
        this.patterns.put(TokenType.DIV, Pattern.compile("/"));
        this.patterns.put(TokenType.MOD, Pattern.compile("%"));

        // Other
        this.patterns.put(TokenType.WHITESPACE, Pattern.compile("\\s+"));
        this.patterns.put(TokenType.COMMENT, Pattern.compile("^\\s*#.*"));
    }

    public List<Token> tokenize() {
        while (cursor < input.length()) {
            boolean matched = false;

            for (var entry : patterns.entrySet()) {
                var type = entry.getKey();
                var pattern = entry.getValue();
                var matcher = pattern.matcher(input.substring(cursor));

                if (matcher.lookingAt()) {
                    String value = matcher.group();
                    int length = value.length();

                    switch (type) {
                        case WHITESPACE, COMMENT -> updatePosition(value);
                        default -> {
                            tokens.add(new Token(type, value, line, column));
                            updatePosition(value);
                        }
                    }

                    cursor += length;
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new RuntimeException("Unknown token at position: [" + line + ":" + column + "]");
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private void updatePosition(String value) {
        for (char c : value.toCharArray()) {
            if (c == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
    }

}
