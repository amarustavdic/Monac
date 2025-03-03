package com.monac.compiler.lexer;

public enum TokenType {

    CONST,
    VOLATILE,

    STRUCT,
    UNION,
    COLON,

    SIZEOF,

    CASE,
    DEFAULT,

    // Keywords
    GOTO, CONTINUE, RETURN, BREAK,
    IF, ELSE, SWITCH, WHILE, DO, FOR,

    // Identifiers and literals
    IDENTIFIER,
    STRING,
    INTEGER_CONSTANT,

    // Punctuation and grouping
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS,
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
