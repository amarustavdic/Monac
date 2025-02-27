package com.monac.compiler.parser;

public enum NodeType {

    // Will be naming terminals for now based on BNF specification for c

    IDENTIFIER,                 // Representing any user defined identifier

    PRIMARY_EXPRESSION,
    POSTFIX_EXPRESSION,
    UNARY_EXPRESSION,

    INTEGER_CONSTANT,           // Representing int actual number
    CONSTANT,

}
