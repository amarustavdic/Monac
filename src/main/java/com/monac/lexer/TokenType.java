package com.monac.lexer;

public enum TokenType {

    INTEGER_CONSTANT,
    CHARACTER_CONSTANT,

    IDENTIFIER,
    STRING,

    MULTIPLY,           // *
    DIVIDE,             // /
    MODULO,             // %
    LEFT_SHIFT,         // <<
    RIGHT_SHIFT,        // >>

    PLUS,               // +
    MINUS,              // -















    // -----------------------------------------------


    // Keywords
    KEYWORD_VOID,       // void
    KEYWORD_CHAR,       // char
    KEYWORD_INT,        // int
    KEYWORD_RETURN,     // return
    KEYWORD_IF,         // if
    KEYWORD_ELSE,       // else
    KEYWORD_WHILE,      // while
    KEYWORD_FOR,        // for
    KEYWORD_BREAK,      // break
    KEYWORD_CONTINUE,   // continue
    KEYWORD_GOTO,       // goto


    //  Operators
    EQUALS,             // =
    PLUS_ASSIGN,        // +=
    MINUS_ASSIGN,       // -=
    MUL_ASSIGN,         // *=
    DIV_ASSIGN,         // /=
    MOD_ASSIGN,         // %=
    INCREMENT,          // ++
    DECREMENT,          // --
    EQUALS_EQUALS,      // ==
    NOT_EQUALS,         // !=
    LESS_THAN,          // <
    GREATER_THAN,       // >
    LESS_EQUALS,        // <=
    GREATER_EQUALS,     // >=
    LOGICAL_AND,        // &&
    LOGICAL_OR,         // ||
    BITWISE_AND,        // &
    BITWISE_OR,         // |
    BITWISE_XOR,        // ^
    BITWISE_NOT,        // ~
    LOGICAL_NOT,        // !

    DEREFERENCE,        // *
    ADDRESS_OF,         // &

    // Delimiters
    SEMICOLON,          // ;
    COMMA,              // ,
    DOT,                // .
    COLON,              // :
    LEFT_PAREN,         // (
    RIGHT_PAREN,        // )
    LEFT_BRACE,         // {
    RIGHT_BRACE,        // }
    LEFT_BRACKET,       // [
    RIGHT_BRACKET,      // ]

    // Special tokens
    WHITESPACE,         // Spaces, tabs, newlines
    COMMENT,            // // Single-line or /* multi-line */
    EOF,                // End of file
    UNKNOWN             // Unrecognized token
}
