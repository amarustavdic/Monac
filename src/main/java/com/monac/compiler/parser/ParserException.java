package com.monac.compiler.parser;

public class ParserException extends Exception {

    private final int line;
    private final int column;
    private final String actual;
    private final String expected;
    private final String suggestion;

    public ParserException(String message, int line, int column, String actual, String expected, String suggestion) {
        super("Syntax error: " + message);
        this.line = line;
        this.column = column;
        this.actual = actual;
        this.expected = expected;
        this.suggestion = suggestion;
    }

    public String toJson() {
        return "{"
                + "\"message\": \"" + escapeJson(super.getMessage()) + "\", "
                + "\"line\": " + line + ", "
                + "\"column\": " + column + ", "
                + "\"actual\": \"" + escapeJson(actual) + "\", "
                + "\"expected\": \"" + escapeJson(expected) + "\", "
                + "\"suggestion\": \"" + escapeJson(suggestion) + "\""
                + "}";
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public String toString() {
        return toJson();
    }
}
