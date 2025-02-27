package com.monac.compiler.lexer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Lexer {

    private final List<Token> tokens = new ArrayList<>();
    private final String input;
    private int cursor = 0;
    private int line = 1;
    private int column = 1;

    private static final Map<TokenType, Pattern> tokenPatterns = new LinkedHashMap<>();

    static {
        // Keywords
        tokenPatterns.put(TokenType.KEYWORD_INT, Pattern.compile("int\\b"));
        tokenPatterns.put(TokenType.KEYWORD_VOID, Pattern.compile("void\\b"));
        tokenPatterns.put(TokenType.RETURN, Pattern.compile("return\\b"));
        tokenPatterns.put(TokenType.IF, Pattern.compile("if\\b"));
        tokenPatterns.put(TokenType.KEYWORD_ELSE, Pattern.compile("else\\b"));
        tokenPatterns.put(TokenType.WHILE, Pattern.compile("while\\b"));
        tokenPatterns.put(TokenType.FOR, Pattern.compile("for\\b"));
        tokenPatterns.put(TokenType.BREAK, Pattern.compile("break\\b"));
        tokenPatterns.put(TokenType.CONTINUE, Pattern.compile("continue\\b"));
        tokenPatterns.put(TokenType.GOTO, Pattern.compile("goto\\b"));

        // Identifiers (variable & function names)
        tokenPatterns.put(TokenType.IDENTIFIER, Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*"));

        // Literals
        tokenPatterns.put(TokenType.INTEGER_LITERAL, Pattern.compile("\\d+"));
        tokenPatterns.put(TokenType.CHAR_LITERAL, Pattern.compile("'([^'\\\\]|\\\\.)'"));
        tokenPatterns.put(TokenType.STRING_LITERAL, Pattern.compile("\"(\\\\.|[^\"])*\""));

        // Operators
        tokenPatterns.put(TokenType.INCREMENT, Pattern.compile("\\+\\+"));
        tokenPatterns.put(TokenType.DECREMENT, Pattern.compile("--"));
        tokenPatterns.put(TokenType.PLUS_ASSIGN, Pattern.compile("\\+="));
        tokenPatterns.put(TokenType.MINUS_ASSIGN, Pattern.compile("-="));
        tokenPatterns.put(TokenType.EQUALS_EQUALS, Pattern.compile("=="));
        tokenPatterns.put(TokenType.NOT_EQUALS, Pattern.compile("!="));
        tokenPatterns.put(TokenType.LESS_EQUALS, Pattern.compile("<="));
        tokenPatterns.put(TokenType.GREATER_EQUALS, Pattern.compile(">="));
        tokenPatterns.put(TokenType.LOGICAL_AND, Pattern.compile("&&"));
        tokenPatterns.put(TokenType.LOGICAL_OR, Pattern.compile("\\|\\|"));

        // Single character operators
        tokenPatterns.put(TokenType.PLUS, Pattern.compile("\\+"));
        tokenPatterns.put(TokenType.MINUS, Pattern.compile("-"));
        tokenPatterns.put(TokenType.STAR, Pattern.compile("\\*"));
        tokenPatterns.put(TokenType.DIVIDE, Pattern.compile("/"));
        tokenPatterns.put(TokenType.MODULO, Pattern.compile("%"));
        tokenPatterns.put(TokenType.EQUALS, Pattern.compile("="));
        tokenPatterns.put(TokenType.LESS_THAN, Pattern.compile("<"));
        tokenPatterns.put(TokenType.GREATER_THAN, Pattern.compile(">"));
        tokenPatterns.put(TokenType.BITWISE_AND, Pattern.compile("&"));
        tokenPatterns.put(TokenType.BITWISE_OR, Pattern.compile("\\|"));
        tokenPatterns.put(TokenType.BITWISE_XOR, Pattern.compile("\\^"));
        tokenPatterns.put(TokenType.BITWISE_NOT, Pattern.compile("~"));
        tokenPatterns.put(TokenType.LOGICAL_NOT, Pattern.compile("!"));

        // Delimiters
        tokenPatterns.put(TokenType.SEMICOLON, Pattern.compile(";"));
        tokenPatterns.put(TokenType.COMMA, Pattern.compile(","));
        tokenPatterns.put(TokenType.LEFT_PAREN, Pattern.compile("\\("));
        tokenPatterns.put(TokenType.RIGHT_PAREN, Pattern.compile("\\)"));
        tokenPatterns.put(TokenType.LEFT_BRACE, Pattern.compile("\\{"));
        tokenPatterns.put(TokenType.RIGHT_BRACE, Pattern.compile("\\}"));

        // Whitespace & Comments
        tokenPatterns.put(TokenType.WHITESPACE, Pattern.compile("\\s+"));
        tokenPatterns.put(TokenType.COMMENT, Pattern.compile("//.*|/\\*.*?\\*/", Pattern.DOTALL));
    }

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        while (cursor < input.length()) {
            boolean matched = false;

            for (var entry : tokenPatterns.entrySet()) {
                var type = entry.getKey();
                var pattern = entry.getValue();
                var matcher = pattern.matcher(input.substring(cursor));

                if (matcher.lookingAt()) {
                    String value = matcher.group();
                    int length = value.length();

                    // Update line & column before adding token
                    if (type == TokenType.WHITESPACE) {
                        updatePosition(value);
                    } else if (type == TokenType.COMMENT) {
                        updatePosition(value);
                    } else {
                        tokens.add(new Token(type, value, line, column));
                        updatePosition(value);
                    }

                    cursor += length;
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new RuntimeException("Unknown token at position: [" + line + "," + column + "]");
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