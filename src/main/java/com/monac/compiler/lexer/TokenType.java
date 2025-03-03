package com.monac.compiler.lexer;

public enum TokenType {

    // Keywords
    GOTO, CONTINUE, RETURN, BREAK,
    IF, ELSE, SWITCH, WHILE, DO, FOR,

    // Identifiers and literals
    IDENTIFIER,
    STRING,
    INTEGER_CONSTANT,

    // Punctuation and grouping
    LPAREN, RPAREN,
    LBRACKET, RBRACKET,
    LBRACE, RBRACE,
    SEMICOLON,
    DOT, // .
    ARROW, // ->

    // Operators
    INCREMENT, // ++
    DECREMENT, // --
    SHL, // <<
    SHR, // >>
    PLUS, // +
    MINUS, // -
    MUL, // *
    DIV, // /
    MOD, // %

    // Miscellaneous
    WHITESPACE,
    COMMENT,
    EOF
}
