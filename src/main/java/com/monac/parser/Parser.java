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

    /**
     * Parses a translation unit, which consists of multiple external declarations.
     * Continues parsing until no more external declarations are found.
     *
     * <pre>{@code
     * <translation-unit> ::= {<external-declaration>}*
     * }</pre>
     *
     * @return A Node representing the translation unit containing all parsed external declarations.
     */
    private Node translationUnit() {
        List<Node> children = new ArrayList<>();
        Node externalDeclaration = externalDeclaration();
        while (externalDeclaration != null) {
            children.add(externalDeclaration);
            externalDeclaration = externalDeclaration();
        }
        return new Node(NodeType.TRANSLATION_UNIT, children);
    }

    /**
     * Parses an external declaration, which can be either a function definition or a declaration.
     *
     * <pre>{@code
     * <external-declaration> ::= <function-definition> | <declaration>
     * }</pre>
     *
     * @return A Node representing a function definition or a declaration, or null if neither is found.
     */
    private Node externalDeclaration() {
        Node functionDef = functionDefinition();
        if (functionDef != null) return functionDef;
        return declaration();
    }

    /**
     * Parses a function definition.
     *
     * <pre>{@code
     * <function-definition> ::=
     *      {<declaration-specifier>}* <declarator> {<declaration>}* <compound-statement>
     * }</pre>
     *
     * @return A Node representing the function definition, or null if parsing fails.
     */
    private Node functionDefinition() {
        List<Node> children = new ArrayList<>();

        Node declarationSpecifier;
        while ((declarationSpecifier = declarationSpecifier()) != null) {
            children.add(declarationSpecifier);
        }

        Node declarator = declarator();
        if (declarator == null) {
            error(peek(), "Missing declarator.");
            return null;
        }
        children.add(declarator);

        Node declaration;
        while ((declaration = declaration()) != null) {
            children.add(declaration);
        }

        Node compoundStatement = compoundStatement();
        if (compoundStatement == null) {
            error(peek(), "Missing compound statement.");
            return null;
        }
        children.add(compoundStatement);

        return new Node(NodeType.FUNCTION_DEFINITION, children);
    }

    // <declaration-specifier> ::= <storage-class-specifier>
    //| <type-specifier>
    //| <type-qualifier>
    private Node declarationSpecifier() {
        return (storageClassSpecifier() != null) ? storageClassSpecifier() : (
                (typeSpecifier() != null) ? typeSpecifier() : typeQualifier()
        );
    }

    //<storage-class-specifier> ::= auto
    //| register
    //| static
    //| extern
    //| typedef
    private Node storageClassSpecifier() {
        if (match(TokenType.AUTO, TokenType.REGISTER, TokenType.STATIC, TokenType.EXTERN, TokenType.TYPEDEF)) {
            return new Node(NodeType.STORAGE_CLASS_SPECIFIER, previous());
        }
        return null;
    }

    // <type-specifier> ::= void
    //| char
    //| short
    //| int
    //| long
    //| float
    //| double
    //| signed
    //| unsigned
    //| <struct-or-union-specifier>
    //| <enum-specifier>
    //| <typedef-name>
    private Node typeSpecifier() {

        if (match(TokenType.VOID, TokenType.CHAR, TokenType.SHORT, TokenType.KEYWORD_INT, TokenType.LONG,
                TokenType.FLOAT, TokenType.DOUBLE, TokenType.SIGNED, TokenType.UNSIGNED)) {
            return new Node(NodeType.TYPE_SPECIFIER, previous());
        }

        Node structOrUnionSpecifier = structOrUnionSpecifier();
        if (structOrUnionSpecifier != null) return structOrUnionSpecifier;

        // TODO: enum-specifier
        // TODO: typedef-name

        return null;
    }

    // <struct-or-union-specifier> ::= <struct-or-union> <identifier> { {<struct-declaration>}+ }
    //| <struct-or-union> { {<struct-declaration>}+ }
    //| <struct-or-union> <identifier>
    private Node structOrUnionSpecifier() {

        List<Node> children = new ArrayList<>();

        Node structOrUnion = structOrUnion();
        if (structOrUnion != null) {
            children.add(structOrUnion); // include <struct-or-union>

            Node identifier = identifier();
            if (identifier != null) {
                children.add(identifier); // include <identifier>

                Token leftBrace = consume(TokenType.LEFT_BRACE, "Expected '{' after identifier.");
                if (leftBrace != null) {

                    Node structDeclaration = structDeclaration();
                    if (structDeclaration != null) {
                        do children.add(structDeclaration);
                        while ((structDeclaration = structDeclaration()) != null);
                        Token rightBrace = consume(TokenType.RIGHT_BRACE, "Expected '}' after structDeclaration.");
                        if (rightBrace != null) {
                            return new Node(NodeType.STRUCT_OR_UNION_SPECIFIER, children);
                        } else {
                            return null;
                        }
                    } else {
                        error(peek(), "Missing struct declaration.");
                        return null;
                    }
                } else {
                    return new Node(NodeType.STRUCT_OR_UNION_SPECIFIER, children);
                }

            } else {
                Token leftBrace = consume(TokenType.LEFT_BRACE, "Expected '{' after struct or union.");
                if (leftBrace != null) {
                    Node structDeclaration = structDeclaration();
                    if (structDeclaration != null) {
                        do children.add(structDeclaration);
                        while ((structDeclaration = structDeclaration()) != null);
                        Token rightBrace = consume(TokenType.RIGHT_BRACE, "Expected '}' after structDeclaration.");
                        if (rightBrace != null) {
                            return new Node(NodeType.STRUCT_OR_UNION_SPECIFIER, children);
                        } else {
                            return null;
                        }
                    } else {
                        error(peek(), "Missing struct declaration.");
                        return null;
                    }
                } else {
                    error(peek(), "Missing identifier.");
                    return null;
                }
            }
        }
        return null;
    }

    // <struct-or-union> ::= struct | union
    private Node structOrUnion() {
        if (match(TokenType.STRUCT, TokenType.UNION)) return new Node(NodeType.STRUCT_OR_UNION, previous());
        return null;
    }

    //<struct-declaration> ::= {<specifier-qualifier>}* <struct-declarator-list>
    private Node structDeclaration() {

        List<Node> children = new ArrayList<>();

        Node specifierQualifier;
        while ((specifierQualifier = specifierQualifier()) != null) children.add(specifierQualifier);

        if (children.isEmpty()) {
            return structDeclaratorList();
        } else {
            Node structDeclaratorList = structDeclaratorList();
            if (structDeclaratorList != null) {
                children.add(structDeclaratorList);
                return new Node(NodeType.STRUCT_DECLARATION, children);
            } else {
                return null;
            }
        }
    }

    // <specifier-qualifier> ::= <type-specifier> | <type-qualifier>
    private Node specifierQualifier() {
        return (typeSpecifier() != null) ? typeSpecifier() : typeQualifier();
    }

    //<struct-declarator-list> ::= <struct-declarator>
    // | <struct-declarator-list> , <struct-declarator>
    private Node structDeclaratorList() {

        Node structDeclarator = structDeclarator();

        if (structDeclarator != null) {
            return structDeclarator;
        } else {
            List<Node> children = new ArrayList<>();
            Node structDeclaratorList = structDeclaratorList();
            if (structDeclaratorList != null) {
                if (match(TokenType.COMMA)) {
                    children.add(structDeclaratorList);
                    structDeclarator = structDeclarator();
                    if (structDeclarator != null) {
                        children.add(structDeclarator);
                        return new Node(NodeType.STRUCT_DECLARATOR_LIST, children);
                    } else {
                        error(peek(), "Expected struct declarator.");
                        return null;
                    }
                } else {
                    error(peek(), "Missing ',' after struct declarator list.");
                    return null;
                }
            } else {
                return structDeclarator;
            }
        }
    }

    //<struct-declarator> ::= <declarator>
    //| <declarator> : <constant-expression>
    //| : <constant-expression>
    private Node structDeclarator() {
        if (match(TokenType.COLON)) {
            return constantExpression(); // : <constant-expression>
        } else {
            Node declarator = declarator();
            if (declarator != null) {
                if (match(TokenType.COLON)) {
                    // <declarator> : <constant-expression>
                    Node constantExpression = constantExpression();
                    if (constantExpression != null) {
                        return new Node(NodeType.STRUCT_DECLARATOR, List.of(declarator, constantExpression));
                    } else {
                        error(peek(), "Expected constant expression after ':'.");
                        return null;
                    }
                } else {
                    return declarator; // <declarator>
                }
            } else {
                return null;
            }
        }
    }

    //<declarator> ::= {<pointer>}? <direct-declarator>
    private Node declarator() {

        List<Node> children = new ArrayList<>();

        Node pointer = pointer();
        if (pointer != null) children.add(pointer);

        if (children.isEmpty()) {
            return directDeclarator();
        } else {
            Node directDeclarator = directDeclarator();
            if (directDeclarator != null) {
                children.add(directDeclarator);
                return new Node(NodeType.DECLARATOR, children);
            } else {
                return null;
            }
        }
    }

    // <pointer> ::= * {<type-qualifier>}* {<pointer>}?
    private Node pointer() {
        // TODO: Well * is overloaded, so figure out how to handle it

        if (match(TokenType.MULTIPLY)) {
            List<Node> children = new ArrayList<>();
            while (typeQualifier() != null) children.add(typeQualifier());

            Node pointer = pointer();
            if (pointer != null) children.add(pointer);
            return new Node(NodeType.POINTER, children);
        } else {
            return null;
        }
    }

    // <type-qualifier> ::= const | volatile
    private Node typeQualifier() {
        if (match(TokenType.CONST, TokenType.VOLATILE)) return new Node(NodeType.TYPE_QUALIFIER, previous());
        return null;
    }

    // <direct-declarator> ::= <identifier>
    //| ( <declarator> )
    //| <direct-declarator> [ {<constant-expression>}? ]
    //| <direct-declarator> ( <parameter-type-list> )
    //| <direct-declarator> ( {<identifier>}* )
    private Node directDeclarator() {
        Node identifier = identifier();
        if (identifier != null) return identifier;

        if (match(TokenType.LEFT_PAREN)) {
            Node declarator = declarator();
            if (declarator != null) {
                if (match(TokenType.RIGHT_PAREN)) {
                    return declarator;
                } else {
                    error(peek(), "Expected ')' after declarator.");
                    return null;
                }
            } else {
                error(peek(), "Expected declarator after '('.");
                return null;
            }
        }

        // If we reach here, the first two cases have failed; we need a valid directDeclarator to proceed.
        Node directDeclarator = identifier();  // Try to get an identifier first
        if (directDeclarator == null) return null; // Avoid infinite recursion

        List<Node> children = new ArrayList<>();
        children.add(directDeclarator);

        while (true) {
            if (match(TokenType.LEFT_BRACKET)) {
                Node constantExpression = constantExpression();
                if (!match(TokenType.RIGHT_BRACKET)) {
                    error(peek(), "Expected ']'.");
                    return null;
                }
                if (constantExpression != null) children.add(constantExpression);
                children.add(new Node(NodeType.DECLARATOR, children));

            } else if (match(TokenType.LEFT_PAREN)) {
                List<Node> paramChildren = new ArrayList<>();
                while ((identifier = identifier()) != null) {
                    paramChildren.add(identifier);
                }

                if (!match(TokenType.RIGHT_PAREN)) {
                    error(peek(), "Expected ')'.");
                    return null;
                }

                children.add(new Node(NodeType.DIRECT_DECLARATOR, paramChildren));
            } else {
                break;  // If no valid declarator, stop parsing
            }
        }

        return new Node(NodeType.DIRECT_DECLARATOR, children);
    }

    // <constant-expression> ::= <conditional-expression>
    private Node constantExpression() {
        return conditionalExpression();
    }

    // <conditional-expression> ::= <logical-or-expression>
    //| <logical-or-expression> ? <expression> : <conditional-expression>
    private Node conditionalExpression() {

        Node logicalOrExpression = logicalOrExpression();
        if (logicalOrExpression != null) {
            if (match(TokenType.QUESTION_MARK)) {
                Node expression = expression();
                if (expression != null) {
                    if (match(TokenType.COLON)) {
                        Node conditionalExpression = conditionalExpression();
                        if (conditionalExpression != null) {
                            return new Node(NodeType.CONDITIONAL_EXPRESSION, List.of(logicalOrExpression, expression, conditionalExpression));
                        } else {
                            error(peek(), "Expected conditional expression.");
                            return null;
                        }
                    } else {
                        error(peek(), "Expected ':'.");
                        return null;
                    }
                }
                error(peek(), "Expected expression after '?'.");
                return null;
            }
            return logicalOrExpression;
        }
        return null;
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

            left = new Node(NodeType.LOGICAL_OR_EXPRESSION, List.of(left, right), operator.getLexeme());
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

            left = new Node(NodeType.LOGICAL_AND_EXPRESSION, List.of(left, right), operator.getLexeme());
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

            left = new Node(NodeType.INCLUSIVE_OR_EXPRESSION, List.of(left, right), operator.getLexeme());
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

            left = new Node(NodeType.EXCLUSIVE_OR_EXPRESSION, List.of(left, right), operator.getLexeme());
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

            left = new Node(NodeType.AND_EXPRESSION, List.of(left, right), operator.getLexeme());
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
            left = new Node(NodeType.EQUALITY_EXPRESSION, List.of(left, right), operator.getLexeme());
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
            left = new Node(NodeType.RELATIONAL_EXPRESSION, List.of(left, right), operator.getLexeme());
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


    // <cast-expression> ::= <unary-expression>
    //| ( <type-name> ) <cast-expression>
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

    // <postfix-expression> ::= <primary-expression>
    //| <postfix-expression> [ <expression> ]
    //| <postfix-expression> ( {<assignment-expression>}* )
    //| <postfix-expression> . <identifier>
    //| <postfix-expression> -> <identifier>
    //| <postfix-expression> ++
    //| <postfix-expression> --
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

    // <type-name> ::= {<specifier-qualifier>}+ {<abstract-declarator>}?
    private Node typeName() {
        return null;
    }

    // <parameter-type-list> ::= <parameter-list>
    //| <parameter-list> , ...
    private Node parameterTypeList() {
        return null;
    }

    // <parameter-list> ::= <parameter-declaration>
    //| <parameter-list> , <parameter-declaration>
    private Node parameterList() {
        return null;
    }

    // <parameter-declaration> ::= {<declaration-specifier>}+ <declarator>
    //| {<declaration-specifier>}+ <abstract-declarator>
    //| {<declaration-specifier>}+
    private Node parameterDeclaration() {
        return null;
    }

    // <abstract-declarator> ::= <pointer>
    //| <pointer> <direct-abstract-declarator>
    //| <direct-abstract-declarator>
    private Node abstractDeclaration() {
        return null;
    }

    // <direct-abstract-declarator> ::= ( <abstract-declarator> )
    //| {<direct-abstract-declarator>}? [ {<constant-expression>}? ]
    //| {<direct-abstract-declarator>}? ( {<parameter-type-list>}? )
    private Node directAbstractDeclarator() {
        return null;
    }


    // <enum-specifier> ::= enum <identifier> { <enumerator-list> }
    //| enum { <enumerator-list> }
    //| enum <identifier>
    private Node enumSpecifier() {
        return null;
    }

    // <enumerator-list> ::= <enumerator>
    //| <enumerator-list> , <enumerator>
    private Node enumeratorList() {
        return null;
    }


    // <enumerator> ::= <identifier>
    //| <identifier> = <constant-expression>
    private Node enumerator() {
        return null;
    }

    // <typedef-name> ::= <identifier>
    private Node typedefName() {
        return identifier();
    }


    // <declaration> ::=
    //{<declaration-specifier>}+ {<init-declarator>}* ;
    private Node declaration() {
        return null;
    }

    // <init-declarator> ::= <declarator>
    //| <declarator> = <initializer>
    private Node initDeclarator() {
        return null;
    }


    // <initializer> ::= <assignment-expression>
    //| { <initializer-list> }
    //| { <initializer-list> , }
    private Node initializer() {
        return null;
    }

    // <initializer-list> ::= <initializer>
    //| <initializer-list> , <initializer>
    private Node initializerList() {
        return null;
    }



    // <compound-statement> ::= { {<declaration>}* {<statement>}* }
    private Node compoundStatement() {

        if (match(TokenType.LEFT_BRACE)) {

            List<Node> children = new ArrayList<>();

            Node declaration;
            while ((declaration = declaration()) != null) children.add(declaration);

            Node statement;
            while ((statement = statement()) != null) children.add(statement);

            consume(TokenType.RIGHT_BRACE, "Expected '}' at end of compound statement.");
            return new Node(NodeType.COMPOUND_STATEMENT, children);

        } else {
            return null;
        }
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


    public Node identifier() {
        if (match(TokenType.IDENTIFIER)) return new Node(NodeType.IDENTIFIER, previous());
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

        Node ast = translationUnit();

        if (!peek().getType().equals(TokenType.EOF)) {
            error(previous(), "Expected end of input.");
        }
        return ast;
    }

}
