package com.monac.compiler.lexer;

public enum TokenType {

    // Keywords
    CONST, VOLATILE,
    STRUCT, UNION, CASE, DEFAULT,
    GOTO, CONTINUE, RETURN, BREAK,
    IF, ELSE, SWITCH, WHILE, DO, FOR,

    // Relational and equality operators
    LT, GT, LE, GE, EQ, NE,

    // Size and type
    SIZEOF,

    AND, // &
    XOR, // ^
    OR,  // |

    LAND, // &&


    // Identifiers and literals
    IDENTIFIER, STRING, INTEGER_CONSTANT,

    // Punctuation and grouping symbols
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS,
    LBRACKET, RBRACKET,
    LBRACE, RBRACE,
    SEMICOLON,
    COLON,
    DOT, // . (dot operator)
    ARROW, // -> (arrow operator)

    // Arithmetic and shift operators
    PLUS, MINUS, MUL, DIV, MOD,
    INCREMENT, DECREMENT, // ++, --

    SHL, SHR, // <<, >>

    // Miscellaneous
    WHITESPACE, COMMENT, EOF
}
