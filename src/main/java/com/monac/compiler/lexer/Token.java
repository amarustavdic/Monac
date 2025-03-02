package com.monac.compiler.lexer;

public class Token {

    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;
    private Object literal;                 // Actual value converted

    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    // Getters

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Object getLiteral() {
        return literal;
    }

    // Setters

    public void setLiteral(Object literal) {
        this.literal = literal;
    }

}
