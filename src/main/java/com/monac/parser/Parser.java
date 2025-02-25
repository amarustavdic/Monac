package com.monac.parser;

import com.monac.lexer.Token;
import com.monac.lexer.TokenType;
import com.monac.parser.ast.ASTNode;
import com.monac.parser.ast.nodes.Identifier;
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
        if (token == null || token.getType() != TokenType.IDENTIFIER) {
            throw new RuntimeException("Identifier expected");
        }
        return new Identifier(token.getLexeme());
    }

    // <jump-statement> ::= goto <identifier> ;
    //                    | continue ;
    //                    | break ;
    //                    | return {<expression>}? ;
    private ASTNode jumpStatement() {

        return null;
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

    // <unary-operator> ::= &
    //| *
    //| +
    //| -
    //| ~
    //| !
    private ASTNode unaryOperator() {
        return null;
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

    // <expression> ::= <assignment-expression>
    //| <expression> , <assignment-expression>

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
