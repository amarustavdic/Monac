package com.monac.compiler.lexer;

public enum TokenType {

    // Keywords
    CONST, VOLATILE,
    STRUCT, UNION, CASE, DEFAULT,
    GOTO, CONTINUE, RETURN, BREAK,
    IF, ELSE, SWITCH, WHILE, DO, FOR,

    // Types
    VOID,
    CHAR,
    INT,


    // Relational and equality operators
    LT, GT, LE, GE, EQ, NE,

    // Size and type
    SIZEOF,

    AND,    // &
    XOR,    // ^
    OR,     // |

    LAND,   // &&
    LOR,    // ||

    QUESTION,

    COMMA,

    ASSIGN,     // =
    MUL_ASSIGN, // *=
    DIV_ASSIGN, // /=
    MOD_ASSIGN, // %=
    INC_ASSIGN, // +=
    DEC_ASSIGN, // -=
    SHL_ASSIGN, // <<=
    SHR_ASSIGN, // >>=
    AND_ASSIGN, // &
    XOR_ASSIGN, // ^
    OR_ASSIGN,  // |

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
