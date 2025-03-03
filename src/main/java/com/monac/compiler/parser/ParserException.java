package com.monac.compiler.parser;

public class ParserException extends Exception {

    private final int line;
    private final int column;
    private final String actual;
    private final String expected;

    public ParserException(String message, int line, int column, String actual, String expected) {
        super(message);
        this.line = line;
        this.column = column;
        this.actual = actual;
        this.expected = expected;
    }

}
