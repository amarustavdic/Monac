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


        // Keywords
        this.patterns.put(TokenType.GOTO, Pattern.compile("\\bgoto\\b"));
        this.patterns.put(TokenType.CONTINUE, Pattern.compile("\\bcontinue\\b"));
        this.patterns.put(TokenType.RETURN, Pattern.compile("\\breturn\\b"));
        this.patterns.put(TokenType.BREAK, Pattern.compile("\\bbreak\\b"));
        this.patterns.put(TokenType.IF, Pattern.compile("\\bif\\b"));
        this.patterns.put(TokenType.ELSE, Pattern.compile("\\belse\\b"));
        this.patterns.put(TokenType.SWITCH, Pattern.compile("\\bswitch\\b"));
        this.patterns.put(TokenType.WHILE, Pattern.compile("\\bwhile\\b"));
        this.patterns.put(TokenType.DO, Pattern.compile("\\bdo\\b"));
        this.patterns.put(TokenType.FOR, Pattern.compile("\\bfor\\b"));

        // Identifiers and literals
        this.patterns.put(TokenType.IDENTIFIER, Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*"));
        this.patterns.put(TokenType.STRING, Pattern.compile("\"(\\\\.|[^\"])*\""));
        this.patterns.put(TokenType.INTEGER_CONSTANT, Pattern.compile("\\d+"));

        // Punctuation and grouping
        this.patterns.put(TokenType.LEFT_PARENTHESIS, Pattern.compile("\\("));
        this.patterns.put(TokenType.RPAREN, Pattern.compile("\\)"));
        this.patterns.put(TokenType.LBRACKET, Pattern.compile("\\["));
        this.patterns.put(TokenType.RBRACKET, Pattern.compile("]"));
        this.patterns.put(TokenType.LBRACE, Pattern.compile("\\{"));
        this.patterns.put(TokenType.RBRACE, Pattern.compile("}"));
        this.patterns.put(TokenType.SEMICOLON, Pattern.compile(";"));

        // Operators
        this.patterns.put(TokenType.DOT, Pattern.compile("\\."));
        this.patterns.put(TokenType.ARROW, Pattern.compile("->"));
        this.patterns.put(TokenType.INCREMENT, Pattern.compile("\\+\\+"));
        this.patterns.put(TokenType.DECREMENT, Pattern.compile("--"));
        this.patterns.put(TokenType.SHL, Pattern.compile("<<"));
        this.patterns.put(TokenType.SHR, Pattern.compile(">>"));
        this.patterns.put(TokenType.PLUS, Pattern.compile("\\+"));
        this.patterns.put(TokenType.MINUS, Pattern.compile("-"));
        this.patterns.put(TokenType.MUL, Pattern.compile("\\*"));
        this.patterns.put(TokenType.DIV, Pattern.compile("/"));
        this.patterns.put(TokenType.MOD, Pattern.compile("%"));

        // Whitespace and comments
        this.patterns.put(TokenType.WHITESPACE, Pattern.compile("\\s+"));
        this.patterns.put(TokenType.COMMENT, Pattern.compile("#.*"));

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
