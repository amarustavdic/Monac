package com.monac.compiler.lexer;

public enum TokenType {

    // Arithmetic
    MUL, // *
    DIV, // /
    MOD, // %

    INTEGER_CONSTANT,

    WHITESPACE,
    COMMENT,

    EOF

}
