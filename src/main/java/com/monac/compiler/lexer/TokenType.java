package com.monac.compiler.lexer;

public enum TokenType {



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
