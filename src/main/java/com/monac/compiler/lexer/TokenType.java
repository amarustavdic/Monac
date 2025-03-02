package com.monac.compiler.lexer;

public enum TokenType {

    // Arithmetic
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
