package com.monac.compiler.lexer;

public enum TokenType {

    IDENTIFIER, //
    STRING,     // " "

    LPAREN,     // (
    RPAREN,     // (

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
