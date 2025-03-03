package com.monac.compiler.lexer;

public enum TokenType {

    GOTO,
    CONTINUE,
    RETURN,
    BREAK,


    IDENTIFIER, //
    STRING,     //

    LPAREN, RPAREN,
    LBRACKET, RBRACKET,
    LBRACE, RBRACE,

    DOT, // .
    ARROW, // ->

    INCREMENT, // ++
    DECREMENT, // --

    IF,
    ELSE,
    SWITCH,
    WHILE,
    DO,
    FOR,


    SEMICOLON,

    // For expressions
    SHL,        // <<
    SHR,        // >>
    PLUS,       // +
    MINUS,      // -
    MUL,        // *
    DIV,        // /
    MOD,        // %

    INTEGER_CONSTANT,

    WHITESPACE,
    COMMENT,

    EOF

}
