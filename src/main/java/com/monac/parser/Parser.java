package com.monac.parser;

import com.monac.lexer.Token;
import com.monac.lexer.TokenType;
import com.monac.parser.ast.ASTNode;
import com.monac.parser.ast.nodes.Identifier;
import com.monac.parser.ast.nodes.JumpStatement;
import com.monac.parser.ast.nodes.UnaryOperator;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Retrieves the next token from the list and advances the cursor.
     *
     * @return The next {@code Token} in the sequence, or {@code null} if there are no more tokens.
     */
    private Token nextToken() {
        if (cursor >= tokens.size()) {
            System.out.println("No more tokens");
            return null;
        }
        var token = tokens.get(cursor);
        cursor++;
        return token;
    }

    /**
     * Peeks at the next token without advancing the cursor.
     * <p>
     * If there are no more tokens, it returns {@code null}.
     * </p>
     *
     * @return The next {@code Token} in the list, or {@code null} if at the end.
     */
    private Token peek() {
        return cursor < tokens.size() ? tokens.get(cursor) : null;
    }

    /**
     * Generates a detailed error message with line and column info.
     *
     * @param message The error description.
     * @param token   The token causing the error.
     * @return A formatted error message with line and column numbers.
     */
    private String errorMessage(String message, Token token) {
        return message + " (Line: " + token.getLine() + ", Column: " + token.getColumn() + ")";
    }

    /**
     * Parses an identifier.
     * <p>
     * An identifier must be a valid token of type {@code IDENTIFIER}.
     * If the next token is not an identifier, an exception is thrown.
     * </p>
     *
     * @return An {@code Identifier} AST node representing the parsed identifier.
     * @throws RuntimeException if the next token is not an identifier.
     */
    private ASTNode identifier() {
        var token = nextToken();
        if (token == null) {
            throw new RuntimeException("Unexpected end of input: Identifier expected.");
        }
        if (token.getType() != TokenType.IDENTIFIER) {
            throw new RuntimeException(errorMessage("Identifier expected, but found '" + token.getLexeme() + "'.", token));
        }
        return new Identifier(token.getLexeme());
    }

    /**
     * Parses a jump statement.
     *
     * <p>Grammar:</p>
     * <pre>
     * &lt;jump-statement&gt; ::= goto &lt;identifier&gt; ;
     *                        | continue ;
     *                        | break ;
     *                        | return {&lt;expression&gt;}? ;
     * </pre>
     *
     * <p>Handles different jump statements such as:</p>
     * <ul>
     *   <li><code>goto</code> followed by an identifier and a semicolon.</li>
     *   <li><code>continue</code> as a standalone statement.</li>
     *   <li><code>break</code> as a standalone statement.</li>
     *   <li><code>return</code> optionally followed by an expression.</li>
     * </ul>
     *
     * @return An {@code ASTNode} representing the parsed jump statement.
     * @throws RuntimeException if the syntax is invalid (e.g., missing semicolon).
     */
    private ASTNode jumpStatement() {
        var token = nextToken();
        if (token == null) {
            throw new RuntimeException("Unexpected end of input while parsing jump statement.");
        }
        switch (token.getType()) {
            case KEYWORD_GOTO -> {
                // Expect an identifier after 'goto'
                var identifier = identifier();
                var semi = nextToken();
                if (semi == null || semi.getType() != TokenType.SEMICOLON) {
                    throw new RuntimeException(errorMessage("Semicolon expected after 'goto' statement.", token));
                }
                return new JumpStatement(token.getLexeme(), identifier);
            }
            case KEYWORD_CONTINUE, KEYWORD_BREAK -> {
                // Continue and break must be followed by a semicolon
                var semi = nextToken();
                if (semi == null || semi.getType() != TokenType.SEMICOLON) {
                    throw new RuntimeException(errorMessage("Semicolon expected after '" + token.getLexeme() + "' statement.", token));
                }
                return new JumpStatement(token.getLexeme());
            }
            case KEYWORD_RETURN -> {
                // Return statement may be followed by an optional expression
                Token next = peek();
                ASTNode expression = null;

                if (next != null && next.getType() != TokenType.SEMICOLON) {
                    expression = expression(); // Parse the expression
                }

                // Ensure the return statement ends with a semicolon
                var semi = nextToken();
                if (semi == null || semi.getType() != TokenType.SEMICOLON) {
                    throw new RuntimeException(errorMessage("Semicolon expected after 'return' statement.", token));
                }

                return new JumpStatement(token.getLexeme(), expression);
            }
            default -> throw new RuntimeException(errorMessage("Unexpected token in jump statement: " + token.getLexeme(), token));
        }
    }

    // <iteration-statement> ::= while ( <expression> ) <statement>
    //| do <statement> while ( <expression> ) ;
    //| for ( {<expression>}? ; {<expression>}? ; {<expression>}? ) <statement>

    // <selection-statement> ::= if ( <expression> ) <statement>
    //| if ( <expression> ) <statement> else <statement>
    //| switch ( <expression> ) <statement>

    // <expression-statement> ::= {<expression>}? ;

    // <labeled-statement> ::= <identifier> : <statement>
    //| case <constant-expression> : <statement>
    //| default : <statement>

    // <statement> ::= <labeled-statement>
    //| <expression-statement>
    //| <compound-statement>
    //| <selection-statement>
    //| <iteration-statement>
    //| <jump-statement>

    // <compound-statement> ::= { {<declaration>}* {<statement>}* }

    // <initializer-list> ::= <initializer>
    //| <initializer-list> , <initializer>

    // <initializer> ::= <assignment-expression>
    //| { <initializer-list> }
    //| { <initializer-list> , }

    // <init-declarator> ::= <declarator>
    //| <declarator> = <initializer>

    // <declaration> ::=
    //{<declaration-specifier>}+ {<init-declarator>}* ;

    // <typedef-name> ::= <identifier>

    // <enumerator> ::= <identifier>
    //| <identifier> = <constant-expression>

    // <enumerator-list> ::= <enumerator>
    //| <enumerator-list> , <enumerator>

    // <enum-specifier> ::= enum <identifier> { <enumerator-list> }
    //| enum { <enumerator-list> }
    //| enum <identifier>

    // <direct-abstract-declarator> ::= ( <abstract-declarator> )
    //| {<direct-abstract-declarator>}? [ {<constant-expression>}? ]
    //| {<direct-abstract-declarator>}? ( {<parameter-type-list>}? )

    // <abstract-declarator> ::= <pointer>
    //| <pointer> <direct-abstract-declarator>
    //| <direct-abstract-declarator>

    // <parameter-declaration> ::= {<declaration-specifier>}+ <declarator>
    //| {<declaration-specifier>}+ <abstract-declarator>
    //| {<declaration-specifier>}+

    // <parameter-list> ::= <parameter-declaration>
    //| <parameter-list> , <parameter-declaration>

    // <parameter-type-list> ::= <parameter-list>
    //| <parameter-list> , ...

    // <type-name> ::= {<specifier-qualifier>}+ {<abstract-declarator>}?

    /**
     * Parses a unary operator.
     *
     * <p>Grammar:</p>
     * <pre>
     * &lt;unary-operator&gt; ::= &amp;  // Address-of
     *                       | *    // Dereference
     *                       | +    // Unary plus
     *                       | -    // Unary minus
     *                       | ~    // Bitwise NOT
     *                       | !    // Logical NOT
     * </pre>
     *
     * <p>This method will return an AST node representing the unary operator.</p>
     *
     * @return An {@code UnaryOperatorNode} representing the parsed unary operator.
     * @throws RuntimeException if the token is not a valid unary operator.
     */
    private ASTNode unaryOperator() {
        Token token = nextToken();

        switch (token.getType()) {
            case BITWISE_NOT: // ~
                return new UnaryOperator("~");
            case DEREFERENCE: // *
                return new UnaryOperator("*");
            case ADDRESS_OF: // &
                return new UnaryOperator("&");
            case PLUS: // +
                return new UnaryOperator("+");
            case MINUS: // -
                return new UnaryOperator("-");
            case LOGICAL_NOT: // !
                return new UnaryOperator("!");
            default:
                // If the token is not a valid unary operator, throw an error
                throw new RuntimeException(errorMessage("Unexpected token for unary operator: " + token.getLexeme(), token));
        }
    }

    // <assignment-operator> ::= =
    //| *=
    //| /=
    //| %=
    //| +=
    //| -=
    //| <<=
    //| >>=
    //| &=
    //| ^=
    //| |=
    private ASTNode assignmentOperator() {
        return null;
    }

    // <assignment-expression> ::= <conditional-expression>
    //| <unary-expression> <assignment-operator> <assignment-expression>
    private ASTNode assignmentExpression() {
        return null;
    }

    /**
     * Parses an expression.
     *
     * <p>Grammar:</p>
     * <pre>
     * &lt;expression&gt; ::= &lt;assignment-expression&gt;
     *                 | &lt;expression&gt; , &lt;assignment-expression&gt;
     * </pre>
     *
     * <p>An expression is either a single assignment expression or a comma-separated list
     * of assignment expressions.</p>
     *
     * @return An {@code ASTNode} representing the parsed expression.
     * @throws RuntimeException if the expression syntax is invalid.
     */
    private ASTNode expression() {
        // Parse the first assignment expression (base case)
        ASTNode left = assignmentExpression();
        if (left == null) {
            throw new RuntimeException("Expected an assignment expression");
        }

//        // Check for comma, indicating a sequence of expressions
//        while (peek() != null && peek().getType() == TokenType.COMMA) {
//            nextToken(); // Consume the comma
//            ASTNode right = assignmentExpression();
//            if (right == null) {
//                throw new RuntimeException(errorMessage("Expected an assignment expression after ','"));
//            }
//            left = new BinaryExpressionNode(TokenType.COMMA, left, right);
//        }
        return left;
    }

    // <constant> ::= <integer-constant>
    //| <character-constant>
    //| <floating-constant>
    //| <enumeration-constant>

    // <primary-expression> ::= <identifier>
    //| <constant>
    //| <string>
    //| ( <expression> )

    // <postfix-expression> ::= <primary-expression>
    //| <postfix-expression> [ <expression> ]
    //| <postfix-expression> ( {<assignment-expression>}* )
    //| <postfix-expression> . <identifier>
    //| <postfix-expression> -> <identifier>
    //| <postfix-expression> ++
    //| <postfix-expression> --

    // <unary-expression> ::= <postfix-expression>
    //| ++ <unary-expression>
    //| -- <unary-expression>
    //| <unary-operator> <cast-expression>
    //| sizeof <unary-expression>
    //| sizeof <type-name>

    // <cast-expression> ::= <unary-expression>
    //| ( <type-name> ) <cast-expression>

    // <multiplicative-expression> ::= <cast-expression>
    //| <multiplicative-expression> * <cast-expression>
    //| <multiplicative-expression> / <cast-expression>
    //| <multiplicative-expression> % <cast-expression>

    // <additive-expression> ::= <multiplicative-expression>
    //| <additive-expression> + <multiplicative-expression>
    //| <additive-expression> - <multiplicative-expression>

    // <shift-expression> ::= <additive-expression>
    //| <shift-expression> << <additive-expression>
    //| <shift-expression> >> <additive-expression>

    // <relational-expression> ::= <shift-expression>
    //| <relational-expression> < <shift-expression>
    //| <relational-expression> > <shift-expression>
    //| <relational-expression> <= <shift-expression>
    //| <relational-expression> >= <shift-expression>

    // <equality-expression> ::= <relational-expression>
    //| <equality-expression> == <relational-expression>
    //| <equality-expression> != <relational-expression>

    // <and-expression> ::= <equality-expression>
    //| <and-expression> & <equality-expression>

    // <exclusive-or-expression> ::= <and-expression>
    //| <exclusive-or-expression> ^ <and-expression>

    // <inclusive-or-expression> ::= <exclusive-or-expression>
    // | <inclusive-or-expression> | <exclusive-or-expression>

    // <logical-and-expression> ::= <inclusive-or-expression>
    //| <logical-and-expression> && <inclusive-or-expression>

    // <logical-or-expression> ::= <logical-and-expression>
    //| <logical-or-expression> || <logical-and-expression>

    // <conditional-expression> ::= <logical-or-expression>
    //| <logical-or-expression> ? <expression> : <conditional-expression>

    // <constant-expression> ::= <conditional-expression>

    // <direct-declarator> ::= <identifier>
    //| ( <declarator> )
    //| <direct-declarator> [ {<constant-expression>}? ]
    //| <direct-declarator> ( <parameter-type-list> )
    //| <direct-declarator> ( {<identifier>}* )

    // <type-qualifier> ::= const
    //| volatile

    // <pointer> ::= * {<type-qualifier>}* {<pointer>}?

    // <declarator> ::= {<pointer>}? <direct-declarator>

    // <struct-declarator> ::= <declarator>
    //| <declarator> : <constant-expression>
    //| : <constant-expression>

    // <struct-declarator-list> ::= <struct-declarator>
    //| <struct-declarator-list> , <struct-declarator>

    // <specifier-qualifier> ::= <type-specifier>
    //| <type-qualifier>

    // <struct-declaration> ::= {<specifier-qualifier>}* <struct-declarator-list>

    // <struct-or-union> ::= struct
    //                     | union
    private ASTNode structOrUnion() {
        return null;
    }

    // <struct-or-union-specifier> ::= <struct-or-union> <identifier> { {<struct-declaration>}+ }
    //                               | <struct-or-union> { {<struct-declaration>}+ }
    //                               | <struct-or-union> <identifier>
    private ASTNode structOrUnionSpecifier() {
        return null;
    }

    // <type-specifier> ::= void
    //                    | char
    //                    | short
    //                    | int
    //                    | long
    //                    | float
    //                    | double
    //                    | signed
    //                    | unsigned
    //                    | <struct-or-union-specifier>
    //                    | <enum-specifier>
    //                    | <typedef-name>
    private ASTNode typeSpecifier() {


        return null;
    }

    // <storage-class-specifier> ::= auto
    //                             | register
    //                             | static
    //                             | extern
    //                             | typedef
    private ASTNode storageClassSpecifier() {
        return null;
    }

    // <declaration-specifier> ::= <storage-class-specifier>
    //                           | <type-specifier>
    //                           | <type-qualifier>
    private ASTNode declarationSpecifier() {
        return null;
    }

    // <function-definition> ::= {<declaration-specifier>}* <declarator> {<declaration>}* <compound-statement>
    private ASTNode functionDefinition() {
        return null;
    }

    // <external-declaration> ::= <function-definition>
    //                          | <declaration>
    private ASTNode externalDeclaration() {
        return null;
    }

    // <translation-unit> ::= {<external-declaration>}*
    private ASTNode translationUnit() {
        return null;
    }

    // Returns constructed AST
    public ASTNode parse() {
        return translationUnit();
    }

}
