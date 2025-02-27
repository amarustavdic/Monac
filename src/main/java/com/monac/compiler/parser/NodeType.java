package com.monac.compiler.parser;

public enum NodeType {

    // Will be naming terminals for now based on BNF specification for c

    IDENTIFIER,                 // Representing any user defined identifier

    PRIMARY_EXPRESSION,
    POSTFIX_EXPRESSION,
    UNARY_EXPRESSION,
    ASSIGNMENT_EXPRESSION,
    CAST_EXPRESSION,
    MULTIPLICATIVE_EXPRESSION,
    ADDITIVE_EXPRESSION,
    SHIFT_EXPRESSION,
    RELATIONAL_EXPRESSION,
    EQUALITY_EXPRESSION,
    AND_EXPRESSION,
    EXCLUSIVE_OR_EXPRESSION,
    INCLUSIVE_OR_EXPRESSION,
    LOGICAL_AND_EXPRESSION,
    LOGICAL_OR_EXPRESSION,
    CONDITIONAL_EXPRESSION,
    EXPRESSION,

    INTEGER_CONSTANT,           // Representing int actual number
    CONSTANT,

    // Assignment operators
    EQUALS,



}
