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


    // <declarator> ::= {<pointer>}? <direct-declarator>
    private Node declarator() {
        return null;
    }

    // <pointer> ::= * {<type-qualifier>}* {<pointer>}?
    private Node pointer() {
        return null;
    }

    // <type-qualifier> ::= const
    //| volatile
    private Node typeQualifier() {
        // TODO: To be decided if I even need those, const and volatile
        return null;
    }

    // <direct-declarator> ::= <identifier>
    //| ( <declarator> )
    //| <direct-declarator> [ {<constant-expression>}? ]
    //| <direct-declarator> ( <parameter-type-list> )
    //| <direct-declarator> ( {<identifier>}* )
    private Node directDeclarator() {
        return null;
    }

    // <constant-expression> ::= <conditional-expression>
    private Node constantExpression() {
        // TODO: Maybe it should be right to nest it, dont know yet we will see
        return conditionalExpression();
    }

    // <conditional-expression> ::= <logical-or-expression>
    //| <logical-or-expression> ? <expression> : <conditional-expression>
    private Node conditionalExpression() {
        Node left = logicalOrExpression();
        if (left == null) {
            error(peek(), "Expected <logical-or-expression> before '?'.");
            return null;
        }

        if (match(TokenType.QUESTION_MARK)) {
            Node middle = expression();
            if (middle == null) {
                error(peek(), "Expected <expression> after '?'.");
                return left;
            }

            if (!match(TokenType.COLON)) {
                error(peek(), "Expected ':' after <expression> in conditional expression.");
                return left;
            }

            Node right = conditionalExpression();
            if (right == null) {
                error(peek(), "Expected <conditional-expression> after ':'.");
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
                error(peek(), "Expected <logical-and-expression> after '||'.");
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
                error(peek(), "Expected <inclusive-or-expression> after '&&'.");
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
                error(peek(), "Expected <exclusive-or-expression> after '|'.");
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
                error(peek(), "Expected <and-expression> after '^'.");
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
                error(peek(), "Expected <equality-expression> after '&'.");
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
                error(peek(), "Expected <relational-expression> after '==' or '!='.");
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
                error(peek(), "Expected <shift-expression> after relational operator.");
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
                error(peek(), "Expected <additive-expression> after shift operator.");
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
                error(peek(), "Expected <multiplicative-expression> after " + operator.getLexeme());
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
                error(peek(), "Expected <cast-expression> after " + operator.getLexeme());
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
                error(peek(), "Expected expression inside parentheses.");
                return null;
            }

            if (!match(TokenType.RIGHT_PAREN)) {
                error(peek(), "Expected closing parenthesis ')'.");
                return null;
            }

            return new Node(NodeType.PRIMARY_EXPRESSION, List.of(expression));
        }

        return constant();
    }


    /**
     * <pre>{@code
     *  <constant> ::= <integer-constant> | <character-constant>
     * }</pre>
     */
    private Node constant() {
        if (match(TokenType.INTEGER_CONSTANT, TokenType.CHARACTER_CONSTANT)) {
            return new Node(NodeType.CONSTANT, previous());
        }
        error(peek(), "Expected <integer-constant> or <character-constant>.");
        return null;
    }

    // <expression> ::= <assignment-expression>
    //| <expression> , <assignment-expression>
    private Node expression() {
        Node left = assignmentExpression();
        if (left == null) return null;

        while (match(TokenType.COMMA)) {
            Node right = assignmentExpression();
            if (right == null) {
                error(peek(), "Expected <assignment-expression> after ','.");
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
            error(peek(), "Expected <unary-expression> in assignment expression.");
            return null;
        }

        Node middle = assignmentOperator();
        if (middle == null) {
            error(peek(), "Expected <assignment-operator> after <unary-expression>.");
            return null;
        }

        Node right = assignmentExpression();
        if (right == null) {
            error(peek(), "Expected <assignment-expression> after <assignment-operator>.");
            return null;
        }

        return new Node(NodeType.ASSIGNMENT_EXPRESSION, List.of(left, middle, right));
    }

    // <assignment-operator> ::= = | *= | /= | %= | += | -= | <<= | >>= | &= | ^= | |=
    private Node assignmentOperator() {
        if (match(TokenType.EQUALS)) {
            return new Node(NodeType.ASSIGNMENT_OPERATOR, previous());
        }
        error(peek(), "Expected '=' assignment operator.");
        // synchronize(); TODO: sync tbd
        return null;
    }

    // TODO: & and * is probably for the pointers and shit, tbd how
    // <unary-operator> ::= & | * | + | - | ~ | !
    private Node unaryOperator() {
        if (match(TokenType.PLUS, TokenType.MINUS, TokenType.BITWISE_NOT, TokenType.LOGICAL_NOT)) {
            return new Node(NodeType.UNARY_OPERATOR, previous());
        } else {
            error(peek(), "Expected unary operator: '+', '-', '~' or '!'.");
            return null;
        }
    }


    // <declaration> ::=
    //{<declaration-specifier>}+ {<init-declarator>}* ;
    private Node declaration() {
        return null;
    }


    // <compound-statement> ::= { {<declaration>}* {<statement>}* }
    private Node compoundStatement() {

        if (!match(TokenType.LEFT_BRACE)) return null;

        List<Node> children = new ArrayList<>();

        Node declaration;
        while ((declaration = declaration()) != null) children.add(declaration);

        Node statement;
        while ((statement = statement()) != null) children.add(statement);

        consume(TokenType.RIGHT_BRACE, "Expected '}' at end of compound statement.");
        return new Node(NodeType.COMPOUND_STATEMENT, children);
    }


    // <statement> ::= <labeled-statement>
    //| <expression-statement>
    //| <compound-statement>
    //| <selection-statement>
    //| <iteration-statement>
    //| <jump-statement>
    private Node statement() {

        Node selectionStatement = selectionStatement();
        if (selectionStatement != null) {
            return selectionStatement;
        }

        Node compoundStatement = compoundStatement();
        if (compoundStatement != null) {
            return compoundStatement;
        }

        Node jumpStatement = jumpStatement();
        if (jumpStatement != null) {
            return jumpStatement;
        }

        return expressionStatement();
    }

    // <labeled-statement> ::= <identifier> : <statement>
    //| case <constant-expression> : <statement>
    //| default : <statement>
    private Node labeledStatement() {
        return null;
    }

    // <expression-statement> ::= {<expression>}? ;
    private Node expressionStatement() {
        Node expr = null;
        if (peek().getType() != TokenType.SEMICOLON) {
            expr = expression();
            if (expr == null) {
                error(peek(), "Expected <expression>.");
                return null;
            }
        }
        consume(TokenType.SEMICOLON, "Expected ';' after expression.");
        return expr != null ? new Node(NodeType.EXPRESSION_STATEMENT, List.of(expr)) : new Node(NodeType.EXPRESSION_STATEMENT, List.of());
    }


    // <selection-statement> ::= if ( <expression> ) <statement>
    //| if ( <expression> ) <statement> else <statement>
    //| switch ( <expression> ) <statement>
    private Node selectionStatement() {
        // Match 'if' first
        if (match(TokenType.IF)) {
            consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'.");

            Node condition = expression();
            if (condition != null) {
                consume(TokenType.RIGHT_PAREN, "Expected ')' after 'if' condition.");

                Node trueBranch = statement();
                if (trueBranch != null) {
                    if (match(TokenType.ELSE)) {
                        Node falseBranch = statement();
                        if (falseBranch != null) {
                            return new Node(NodeType.SELECTION_STATEMENT, List.of(condition, trueBranch, falseBranch), "if");
                        } else {
                            error(peek(), "Expected <statement> after 'else'.");
                            return null;
                        }
                    } else {
                        return new Node(NodeType.SELECTION_STATEMENT, List.of(condition, trueBranch), "if");
                    }
                } else {
                    error(peek(), "Expected <statement> after 'if' condition.");
                    return null;
                }
            } else {
                error(peek(), "Expected <expression> inside 'if' parentheses.");
                return null;
            }
        }
        return null;
    }

    // <iteration-statement> ::= while ( <expression> ) <statement>
    //| do <statement> while ( <expression> ) ;
    //| for ( {<expression>}? ; {<expression>}? ; {<expression>}? ) <statement>
    private Node iterationStatement() {
        return null;
    }


    // <jump-statement> ::= goto <identifier> ;
    //| continue ;
    //| break ;
    //| return {<expression>}? ;
    private Node jumpStatement() {

        if (match(TokenType.GOTO)) {
            Token identifier = consume(TokenType.IDENTIFIER, "Expected identifier after 'goto'.");
            consume(TokenType.SEMICOLON, "Expected ';' after 'goto'.");
            return new Node(NodeType.JUMP_STATEMENT, identifier, "goto");
        }

        if (match(TokenType.CONTINUE)) {
            consume(TokenType.SEMICOLON, "Expected ';' after 'continue'.");
            return new Node(NodeType.JUMP_STATEMENT, List.of(), "continue");
        }

        if (match(TokenType.BREAK)) {
            consume(TokenType.SEMICOLON, "Expected ';' after 'break'.");
            return new Node(NodeType.JUMP_STATEMENT, List.of(), "break");
        }

        if (match(TokenType.RETURN)) {
            Node expression = null;
            if (!check(TokenType.SEMICOLON)) {
                expression = expression();
            }
            consume(TokenType.SEMICOLON, "Expected ';' after 'return'.");
            return new Node(NodeType.JUMP_STATEMENT, expression != null ? List.of(expression) : List.of(), "return");
        }

        error(peek(), "Expected 'goto', 'continue', 'break', or 'return' statement.");
        return null;
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
            error(peek(), message);
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
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) cursor++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(cursor);
    }

    private Token previous() {
        return tokens.get(cursor - 1);
    }

    public Node parse() {

        Node ast = primaryExpression();

        if (!peek().getType().equals(TokenType.EOF)) {
            error(previous(), "Expected end of input.");
        }
        return ast;
    }

}
