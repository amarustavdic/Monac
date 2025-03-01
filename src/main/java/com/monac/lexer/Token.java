package com.monac.lexer;

public class Token {

    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;

    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token{type: " + type + ", lexeme: " + lexeme + ", [" + line + ":" + column + "]}";
    }

    public String toJson() {
        return "\033[1;37m{" +
                " \033[1;32m\"type\"\033[0m: \033[1;33m\"" + type + "\"\033[0m," +
                " \033[1;32m\"lexeme\"\033[0m: \033[1;36m\"" + lexeme + "\"\033[0m," +
                " \033[1;32m\"line\"\033[0m: \033[1;35m" + line + "\033[0m," +
                " \033[1;32m\"column\"\033[0m: \033[1;35m" + column + " \033[0m" +
                "\033[1;37m}\033[0m";
    }

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

}
