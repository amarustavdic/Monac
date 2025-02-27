package com.monac.compiler.parser;

public class ParserException extends Exception {

    private final int line;
    private final int column;
    private final String expected;
    private final String found;

    public ParserException(String expected, String found, int line, int column) {
        this.line = line;
        this.column = column;
        this.expected = expected;
        this.found = found;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getExpected() {
        return expected;
    }

    public String getFound() {
        return found;
    }

    @Override
    public String getMessage() {
        return "Expected " + expected + " but found " + found + " at [" + line + ":" + column + "]";
    }

}
