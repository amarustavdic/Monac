package com.monac.parser;

import com.monac.lexer.Token;
import com.monac.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<String> errors = new ArrayList<>();

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }


    // ------------------------------------- FUNCTIONS FOR NON-TERMINALS


    // <conditional-expression> ::= <logical-or-expression>
    //| <logical-or-expression> ? <expression> : <conditional-expression>
    private Node conditionalExpression() {
        Node left = logicalOrExpression();
        if (left == null) {
            error(peek(0), "Expected <logical-or-expression> before '?'.");
            return null;
        }

        if (match(TokenType.QUESTION_MARK)) {
            Node middle = expression();
            if (middle == null) {
                error(peek(0), "Expected <expression> after '?'.");
                return left;
            }

            if (!match(TokenType.COLON)) {
                error(peek(0), "Expected ':' after <expression> in conditional expression.");
                return left;
            }

            Node right = conditionalExpression();
            if (right == null) {
                error(peek(0), "Expected <conditional-expression> after ':'.");
                return left;
            }

            return new Node(NodeType.CONDITIONAL_EXPRESSION, List.of(left, middle, right));
        }

        return left; // If no `?`, return the logical OR expression as-is
    }

    // <logical-or-expression> ::= <logical-and-expression>
    //| <logical-or-expression> || <logical-and-expression>
    private Node logicalOrExpression() {
        Node left = logicalAndExpression();
        if (left == null) return null;

        while (match(TokenType.LOGICAL_OR)) {
            Token operator = previous();
            Node right = logicalAndExpression();

            if (right == null) {
                error(peek(0), "Expected <logical-and-expression> after '||'.");
                // synchronize(); TODO: tbd sync
                return left;
            }

            left = new Node(NodeType.LOGICAL_OR_EXPRESSION, List.of(left, right),
                    operator != null ? operator.getLexeme() : "||");
        }

        return left;
    }

    //<logical-and-expression> ::= <inclusive-or-expression>
    //| <logical-and-expression> && <inclusive-or-expression>
    private Node logicalAndExpression() {
        Node left = inclusiveOrExpression();
        if (left == null) return null;

        while (match(TokenType.LOGICAL_AND)) {
            Token operator = previous();
            Node right = inclusiveOrExpression();

            if (right == null) {
                error(peek(0), "Expected <inclusive-or-expression> after '&&'.");
                // synchronize(); TODO: tbd sync recover from errors
                return left;
            }

            left = new Node(NodeType.LOGICAL_AND_EXPRESSION, List.of(left, right),
                    operator != null ? operator.getLexeme() : "&&");
        }

        return left;
    }

    // <inclusive-or-expression> ::= <exclusive-or-expression>
    // | <inclusive-or-expression> | <exclusive-or-expression>
    private Node inclusiveOrExpression() {
        Node left = exclusiveOrExpression();
        if (left == null) return null;

        while (match(TokenType.BITWISE_OR)) {
            Token operator = previous();
            Node right = exclusiveOrExpression();

            if (right == null) {
                error(peek(0), "Expected <exclusive-or-expression> after '|'.");
                // synchronize(); TODO: tbd sync, waiting better times
                return left;
            }

            left = new Node(NodeType.INCLUSIVE_OR_EXPRESSION, List.of(left, right),
                    operator != null ? operator.getLexeme() : "|");
        }

        return left;
    }

    // <exclusive-or-expression> ::= <and-expression>
    //| <exclusive-or-expression> ^ <and-expression>
    private Node exclusiveOrExpression() {
        Node left = andExpression();
        if (left == null) return null;

        while (match(TokenType.BITWISE_XOR)) {
            Token operator = previous();
            Node right = andExpression();

            if (right == null) {
                error(peek(0), "Expected <and-expression> after '^'.");
                // synchronize(); TODO sync
                return left;
            }

            left = new Node(NodeType.EXCLUSIVE_OR_EXPRESSION, List.of(left, right),
                    operator != null ? operator.getLexeme() : "^");
        }

        return left;
    }

    // <and-expression> ::= <equality-expression>
    //| <and-expression> & <equality-expression>
    private Node andExpression() {
        Node left = equalityExpression();
        if (left == null) return null;

        while (match(TokenType.BITWISE_AND)) {
            Token operator = previous();
            Node right = equalityExpression();

            if (right == null) {
                error(peek(0), "Expected <equality-expression> after '&'.");
                // synchronize(); TODO: sync
                return left;
            }

            left = new Node(NodeType.AND_EXPRESSION, List.of(left, right),
                    operator != null ? operator.getLexeme() : "&");
        }

        return left;
    }

    // <equality-expression> ::= <relational-expression>
    //| <equality-expression> == <relational-expression>
    //| <equality-expression> != <relational-expression>
    private Node equalityExpression() {
        Node left = relationalExpression();
        if (left == null) return null;

        while (match(TokenType.EQUALS_EQUALS, TokenType.NOT_EQUALS)) {
            Token operator = previous();
            Node right = relationalExpression();

            if (right == null) {
                error(peek(0), "Expected <relational-expression> after '==' or '!='.");
                // synchronize(); TODO: sync
                return left;
            }

            left = new Node(NodeType.EQUALITY_EXPRESSION, List.of(left, right),
                    operator != null ? operator.getLexeme() : (operator != null && operator.getType() == TokenType.EQUALS_EQUALS ? "==" : "!="));
        }

        return left;
    }

    // <relational-expression> ::= <shift-expression>
    //| <relational-expression> < <shift-expression>
    //| <relational-expression> > <shift-expression>
    //| <relational-expression> <= <shift-expression>
    //| <relational-expression> >= <shift-expression>
    private Node relationalExpression() {
        Node left = shiftExpression();
        if (left == null) return null;

        while (match(TokenType.LESS_THAN, TokenType.GREATER_THAN, TokenType.LESS_EQUALS, TokenType.GREATER_EQUALS)) {
            Token operator = previous();
            Node right = shiftExpression();

            if (right == null) {
                error(peek(0), "Expected <shift-expression> after relational operator.");
                // synchronize(); TODO: sync
                return left;
            }

            // TODO: Potential problem with defaulting to '<'
            left = new Node(NodeType.RELATIONAL_EXPRESSION, List.of(left, right),
                    operator != null ? operator.getLexeme() : "<");
        }

        return left;
    }

    // <shift-expression> ::= <additive-expression>
    //| <shift-expression> << <additive-expression>
    //| <shift-expression> >> <additive-expression>
    private Node shiftExpression() {
        Node left = additiveExpression();
        if (left == null) return null;

        while (match(TokenType.LEFT_SHIFT, TokenType.RIGHT_SHIFT)) {
            Token operator = previous();
            Node right = additiveExpression();

            if (right == null) {
                error(peek(0), "Expected <additive-expression> after shift operator.");
                // synchronize(); TODO: sync
                return left;
            }

            left = new Node(NodeType.SHIFT_EXPRESSION, List.of(left, right), operator.getLexeme());
        }

        return left;
    }

    // <additive-expression> ::= <multiplicative-expression>
    //| <additive-expression> + <multiplicative-expression>
    //| <additive-expression> - <multiplicative-expression>
    private Node additiveExpression() {
        Node left = multiplicativeExpression();
        if (left == null) return null;

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Node right = multiplicativeExpression();

            if (right == null) {
                error(peek(0), "Expected <multiplicative-expression> after " + operator.getLexeme());
                // synchronize(); TODO: sync later
                return left;
            }

            left = new Node(NodeType.ADDITIVE_EXPRESSION, List.of(left, right), operator.getLexeme());
        }

        return left;
    }

    // <multiplicative-expression> ::= <cast-expression>
    //| <multiplicative-expression> * <cast-expression>
    //| <multiplicative-expression> / <cast-expression>
    //| <multiplicative-expression> % <cast-expression>
    private Node multiplicativeExpression() {
        Node left = castExpression();
        if (left == null) return null;

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            Token operator = previous();
            Node right = castExpression();

            if (right == null) {
                error(peek(0), "Expected <cast-expression> after " + operator.getLexeme());
                // synchronize(); TODO: sync later
                return left;
            }

            left = new Node(NodeType.MULTIPLICATIVE_EXPRESSION, List.of(left, right), operator.getLexeme());
        }

        return left;
    }


    private Node castExpression() {
        return unaryExpression();
    }

    // <unary-expression> ::= <postfix-expression>
    //| ++ <unary-expression>
    //| -- <unary-expression>
    //| <unary-operator> <cast-expression>
    //| sizeof <unary-expression>
    //| sizeof <type-name>
    private Node unaryExpression() {

        if (match(TokenType.INCREMENT, TokenType.DECREMENT)) {
            Token operator = previous();
            Node right = unaryExpression();
            if (right != null) {
                return new Node(NodeType.UNARY_EXPRESSION, List.of(right), operator.getLexeme());
            } else {
                error(operator, "Expected <unary-expression> after '++' or '--'.");
                return null;
            }
        } else {
            return postfixExpression();
        }
    }

    private Node postfixExpression() {
        return primaryExpression();
    }

    // <primary-expression> ::= <identifier>
    //| <constant>
    //| <string>
    //| ( <expression> )
    private Node primaryExpression() {
        if (match(TokenType.IDENTIFIER, TokenType.STRING)) {
            return new Node(NodeType.PRIMARY_EXPRESSION, previous());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Node expression = expression();
            if (expression == null) {
                error(peek(0), "Expected expression inside parentheses.");
                return null;
            }

            if (!match(TokenType.RIGHT_PAREN)) {
                error(peek(0), "Expected closing parenthesis ')'.");
                return null;
            }

            return new Node(NodeType.PRIMARY_EXPRESSION, List.of(expression));
        }

        return constant();
    }


    // For now handling an only couple of constants from c bnf grammar
    // <constant> ::= <integer-constant>
    //| <character-constant>
    //| <floating-constant>
    //| <enumeration-constant>
    private Node constant() {
        Token token = peek(0);
        if (match(TokenType.INTEGER_CONSTANT, TokenType.CHARACTER_CONSTANT)) {
            return new Node(NodeType.CONSTANT, token);
        } else {
            error(token, "Expected <integer-constant> or <character-constant>.");
            return null;
        }
    }

    // <expression> ::= <assignment-expression>
    //| <expression> , <assignment-expression>
    private Node expression() {
        Node left = assignmentExpression();
        if (left == null) return null;

        while (match(TokenType.COMMA)) {
            Node right = assignmentExpression();
            if (right == null) {
                error(peek(0), "Expected <assignment-expression> after ','.");
                // synchronize(); TODO: tbd sync
                return left;
            }
            left = new Node(NodeType.EXPRESSION, List.of(left, right));
        }
        return left;
    }

    // <assignment-expression> ::= <conditional-expression>
    //| <unary-expression> <assignment-operator> <assignment-expression>
    private Node assignmentExpression() {
        Node left = conditionalExpression();
        if (left != null) {
            return left;
        }

        left = unaryExpression();
        if (left == null) {
            error(peek(0), "Expected <unary-expression> in assignment expression.");
            return null;
        }

        Node middle = assignmentOperator();
        if (middle == null) {
            error(peek(0), "Expected <assignment-operator> after <unary-expression>.");
            return null;
        }

        Node right = assignmentExpression();
        if (right == null) {
            error(peek(0), "Expected <assignment-expression> after <assignment-operator>.");
            return null;
        }

        return new Node(NodeType.ASSIGNMENT_EXPRESSION, List.of(left, middle, right));
    }

    // <assignment-operator> ::= = | *= | /= | %= | += | -= | <<= | >>= | &= | ^= | |=
    private Node assignmentOperator() {
        if (match(TokenType.EQUALS)) {
            return new Node(NodeType.ASSIGNMENT_OPERATOR, previous());
        }
        error(peek(0), "Expected '=' assignment operator.");
        // synchronize(); TODO: sync tbd
        return null;
    }

    // TODO: & and * is probably for the pointers and shit, tbd how
    // <unary-operator> ::= & | * | + | - | ~ | !
    private Node unaryOperator() {
        if (match(TokenType.PLUS, TokenType.MINUS, TokenType.BITWISE_NOT, TokenType.LOGICAL_NOT)) {
            return new Node(NodeType.UNARY_OPERATOR, previous());
        } else {
            error(peek(0), "Expected unary operator: '+', '-', '~' or '!'.");
            return null;
        }
    }


    // ----------------- ERROR HANDLING METHODS

    private void error(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            report(token.getLine(), token.getColumn(), " at end", message);
        } else {
            report(token.getLine(), token.getColumn(), " at '" + token.getLexeme() + "'", message);
        }
    }

    private void report(int line, int column, String where, String message) {
        String error = "[" + line + ":" + column + "] Error" + where + ": " + message;
        errors.add(error);
    }

    // ----------------- HELPER METHODS BELLOW

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        } else {
            error(peek(0), message);
            return null;
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek(0).getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) cursor++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek(0).getType() == TokenType.EOF;
    }

    private Token peek(int offset) {
        // This might be a problem, of index out of bounds later, tbd
        return tokens.get(cursor + offset);
    }

    private Token previous() {
        return tokens.get(cursor - 1);
    }

    public Node parse() {
        Node ast = conditionalExpression();
        if (!peek(0).getType().equals(TokenType.EOF)) {
            error(previous(), "Expected end of input.");
        }
        return ast;
    }

}
