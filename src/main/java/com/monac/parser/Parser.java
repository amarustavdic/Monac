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

    /**
     * Parses a declaration specifier, which can be a storage class specifier,
     * type specifier, or type qualifier.
     *
     * <pre>{@code
     * <declaration-specifier> ::= <storage-class-specifier>
     *                           | <type-specifier>
     *                           | <type-qualifier>
     * }</pre>
     *
     * @return A Node representing the declaration specifier, or null if parsing fails.
     */
    private Node declarationSpecifier() {
        Node storageClass = storageClassSpecifier();
        if (storageClass != null) {
            return storageClass;
        }

        Node typeSpec = typeSpecifier();
        if (typeSpec != null) {
            return typeSpec;
        }

        return typeQualifier();
    }

    /**
     * Parses a storage class specifier, which can be one of: auto, register, static, extern, or typedef.
     *
     * <pre>{@code
     * <storage-class-specifier> ::= auto
     *                             | register
     *                             | static
     *                             | extern
     *                             | typedef
     * }</pre>
     *
     * @return A Node representing the storage class specifier, or null if no match is found.
     */
    private Node storageClassSpecifier() {
        if (match(TokenType.AUTO, TokenType.REGISTER, TokenType.STATIC, TokenType.EXTERN, TokenType.TYPEDEF)) {
            return new Node(NodeType.STORAGE_CLASS_SPECIFIER, previous());
        }
        return null;
    }

    /**
     * Parses a type specifier, which can be one of the basic types, a struct/union specifier,
     * an enum specifier, or a typedef name.
     *
     * <pre>{@code
     * <type-specifier> ::= void
     *                    | char
     *                    | short
     *                    | int
     *                    | long
     *                    | float
     *                    | double
     *                    | signed
     *                    | unsigned
     *                    | <struct-or-union-specifier>
     *                    | <enum-specifier>
     *                    | <typedef-name>
     * }</pre>
     *
     * @return A Node representing the type specifier, or null if no match is found.
     */
    private Node typeSpecifier() {
        if (match(TokenType.VOID, TokenType.CHAR, TokenType.SHORT, TokenType.KEYWORD_INT, TokenType.LONG,
                TokenType.FLOAT, TokenType.DOUBLE, TokenType.SIGNED, TokenType.UNSIGNED)) {
            return new Node(NodeType.TYPE_SPECIFIER, previous());
        }

        Node structOrUnionSpecifier = structOrUnionSpecifier();
        if (structOrUnionSpecifier != null) {
            return structOrUnionSpecifier;
        }

        Node enumSpecifier = enumSpecifier();
        if (enumSpecifier != null) {
            return enumSpecifier;
        }

        return typedefName();
    }

    /**
     * Parses a struct or union specifier, which can consist of:
     * - A struct or union followed by an identifier and struct declarations.
     * - A struct or union followed directly by struct declarations.
     * - A struct or union followed by an identifier.
     *
     * <pre>{@code
     * <struct-or-union-specifier> ::=
     *      <struct-or-union> <identifier> { {<struct-declaration>}+ }
     *    | <struct-or-union> { {<struct-declaration>}+ }
     *    | <struct-or-union> <identifier>
     * }</pre>
     *
     * @return A Node representing the struct or union specifier, or null if parsing fails.
     */
    private Node structOrUnionSpecifier() {
        List<Node> children = new ArrayList<>();

        // Parse the struct or union keyword (struct/union)
        Node structOrUnion = structOrUnion();
        if (structOrUnion == null) {
            return null;
        }

        children.add(structOrUnion);

        // Try to parse an identifier (optional)
        Node identifier = identifier();
        if (identifier != null) {
            children.add(identifier); // Include identifier

            Token leftBrace = consume(TokenType.LEFT_BRACE, "Expected '{' after identifier.");
            if (leftBrace == null) {
                return new Node(NodeType.STRUCT_OR_UNION_SPECIFIER, children);
            }

            if (parseStructDeclarations(children)) {
                return null;
            }

            Token rightBrace = consume(TokenType.RIGHT_BRACE, "Expected '}' after struct declarations.");
            if (rightBrace != null) {
                return new Node(NodeType.STRUCT_OR_UNION_SPECIFIER, children);
            } else {
                return null; // Missing right brace
            }
        }

        Token leftBrace = consume(TokenType.LEFT_BRACE, "Expected '{' after struct or union.");
        if (leftBrace == null) {
            return null; // Missing left brace
        }

        if (parseStructDeclarations(children)) {
            return null;
        }

        Token rightBrace = consume(TokenType.RIGHT_BRACE, "Expected '}' after struct declarations.");
        if (rightBrace != null) {
            return new Node(NodeType.STRUCT_OR_UNION_SPECIFIER, children);
        }

        return null;
    }

    /**
     * Helper method to parse one or more struct declarations and add them to the list of children.
     *
     * @param children The list to which the struct declarations will be added.
     * @return true if struct declarations are successfully parsed, false if missing.
     */
    private boolean parseStructDeclarations(List<Node> children) {
        Node structDeclaration = structDeclaration();
        if (structDeclaration == null) {
            error(peek(), "Missing struct declaration.");
            return true;
        }

        do {
            children.add(structDeclaration);
        } while ((structDeclaration = structDeclaration()) != null);

        return false;
    }

    /**
     * Parses a struct or union keyword, which can be either "struct" or "union".
     *
     * <pre>{@code
     * <struct-or-union> ::= struct | union
     * }</pre>
     *
     * @return A Node representing the struct or union keyword, or null if no match is found.
     */
    private Node structOrUnion() {
        if (match(TokenType.STRUCT, TokenType.UNION)) {
            return new Node(NodeType.STRUCT_OR_UNION, previous());
        }
        return null;
    }

    /**
     * Parses a struct declaration, which consists of:
     * - Zero or more specifier-qualifiers
     * - A struct declarator list
     *
     * <pre>{@code
     * <struct-declaration> ::= {<specifier-qualifier>}* <struct-declarator-list>
     * }</pre>
     *
     * @return A Node representing the struct declaration, or null if parsing fails.
     */
    private Node structDeclaration() {
        List<Node> children = new ArrayList<>();

        // Parse specifier-qualifiers (optional)
        Node specifierQualifier;
        while ((specifierQualifier = specifierQualifier()) != null) {
            children.add(specifierQualifier);
        }

        // Always expect a struct declarator list
        Node structDeclaratorList = structDeclaratorList();
        if (structDeclaratorList == null) {
            return null; // If no struct declarator list, return null
        }

        // Add struct declarator list to the children and return the struct declaration node
        children.add(structDeclaratorList);
        return new Node(NodeType.STRUCT_DECLARATION, children);
    }

    /**
     * Parses a specifier-qualifier, which can either be a type specifier or a type qualifier.
     *
     * <pre>{@code
     * <specifier-qualifier> ::= <type-specifier> | <type-qualifier>
     * }</pre>
     *
     * @return A Node representing the specifier-qualifier, or null if no match is found.
     */
    private Node specifierQualifier() {
        Node typeSpec = typeSpecifier();
        if (typeSpec != null) {
            return typeSpec;
        }
        return typeQualifier();
    }

    /**
     * Parses a list of struct declarators, which can either be a single struct declarator
     * or a comma-separated list of struct declarators.
     *
     * <pre>{@code
     * <struct-declarator-list> ::= <struct-declarator>
     *                            | <struct-declarator-list> , <struct-declarator>
     * }</pre>
     *
     * @return A Node representing the struct declarator list, or null if parsing fails.
     */
    private Node structDeclaratorList() {
        List<Node> children = new ArrayList<>();

        // Parse the first struct declarator
        Node structDeclarator = structDeclarator();
        if (structDeclarator == null) {
            return null; // If no struct declarator, return null
        }

        children.add(structDeclarator);

        // Handle additional struct declarators (comma-separated)
        while (match(TokenType.COMMA)) {
            structDeclarator = structDeclarator();
            if (structDeclarator != null) {
                children.add(structDeclarator);
            } else {
                error(peek(), "Expected struct declarator after comma.");
                return null;
            }
        }
        return new Node(NodeType.STRUCT_DECLARATOR_LIST, children);
    }

    /**
     * Parses a struct declarator, which can either be:
     * - A declarator, or
     * - A declarator followed by a colon and a constant expression, or
     * - A colon followed by a constant expression.
     *
     * <pre>{@code
     * <struct-declarator> ::= <declarator>
     *                       | <declarator> : <constant-expression>
     *                       | : <constant-expression>
     * }</pre>
     *
     * @return A Node representing the struct declarator, or null if parsing fails.
     */
    private Node structDeclarator() {
        // Handle the case where we start with a colon directly
        if (match(TokenType.COLON)) {
            return constantExpression(); // ": <constant-expression>"
        }

        Node declarator = declarator();
        if (declarator == null) {
            return null;
        }

        // If a colon follows the declarator, parse the constant expression
        if (match(TokenType.COLON)) {
            Node constantExpression = constantExpression();
            if (constantExpression != null) {
                return new Node(NodeType.STRUCT_DECLARATOR, List.of(declarator, constantExpression));
            } else {
                error(peek(), "Expected constant expression after ':'.");
                return null;
            }
        }

        // Return the declarator if no colon follows
        return declarator; // "<declarator>"
    }

    /**
     * Parses a declarator, which consists of an optional pointer followed by a direct declarator.
     *
     * <pre>{@code
     * <declarator> ::= {<pointer>}? <direct-declarator>
     * }</pre>
     *
     * @return A Node representing the declarator, or null if parsing fails.
     */
    private Node declarator() {
        List<Node> children = new ArrayList<>();

        // Parse the pointer (optional)
        Node pointer = pointer();
        if (pointer != null) {
            children.add(pointer);
        }

        // Parse the direct declarator
        Node directDeclarator = directDeclarator();
        if (directDeclarator == null) {
            return null;
        }

        // Add the direct declarator to the children list and return the declarator node
        children.add(directDeclarator);
        return new Node(NodeType.DECLARATOR, children);
    }

    /**
     * Parses a pointer, which consists of an optional sequence of type qualifiers
     * followed by an optional pointer.
     * A pointer begins with a '*' token, followed by zero or more type qualifiers,
     * and an optional nested pointer.
     *
     * <pre>{@code
     * <pointer> ::= * {<type-qualifier>}* {<pointer>}?
     * }</pre>
     *
     * @return A Node representing the pointer, or null if parsing fails.
     */
    private Node pointer() {
        if (match(TokenType.MULTIPLY)) {
            List<Node> children = new ArrayList<>();

            Node typeQualifier;
            while ((typeQualifier = typeQualifier()) != null) {
                children.add(typeQualifier);
            }

            Node nestedPointer = pointer();
            if (nestedPointer != null) {
                children.add(nestedPointer);
            }

            return new Node(NodeType.POINTER, children);
        }
        return null;
    }

    /**
     * Parses a type qualifier, which can be either "const" or "volatile".
     *
     * <pre>{@code
     * <type-qualifier> ::= const | volatile
     * }</pre>
     *
     * @return A Node representing the type qualifier, or null if parsing fails.
     */
    private Node typeQualifier() {
        if (match(TokenType.CONST, TokenType.VOLATILE)) {
            return new Node(NodeType.TYPE_QUALIFIER, previous());
        }
        return null;
    }

    /**
     * Parses a direct declarator, which can be one of the following:
     * - An identifier,
     * - A declarator in parentheses,
     * - A direct declarator followed by square brackets (with an optional constant expression),
     * - A direct declarator followed by a parameter type list in parentheses,
     * - A direct declarator followed by a list of identifiers in parentheses.
     *
     * <pre>{@code
     * <direct-declarator> ::= <identifier>
     *     | ( <declarator> )
     *     | <direct-declarator> [ {<constant-expression>}? ]
     *     | <direct-declarator> ( <parameter-type-list> )
     *     | <direct-declarator> ( {<identifier>}* )
     * }</pre>
     *
     * @return A Node representing the direct declarator, or null if parsing fails.
     */
    private Node directDeclarator() {
        Node identifier = identifier();
        if (identifier != null) {
            return identifier;
        }

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

        List<Node> children = new ArrayList<>();
        Node baseDeclarator = identifier();
        if (baseDeclarator == null) return null;

        children.add(baseDeclarator);

        while (true) {
            if (match(TokenType.LEFT_BRACKET)) {
                Node constantExpression = constantExpression();
                if (!match(TokenType.RIGHT_BRACKET)) {
                    error(peek(), "Expected ']'.");
                    return null;
                }
                if (constantExpression != null) {
                    children.add(constantExpression);
                }
            }
            else if (match(TokenType.LEFT_PAREN)) {
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
                break;
            }
        }
        return new Node(NodeType.DIRECT_DECLARATOR, children);
    }

    /**
     * Parses a constant expression, which is equivalent to a conditional expression.
     *
     * <pre>{@code
     * <constant-expression> ::= <conditional-expression>
     * }</pre>
     *
     * @return A Node representing the constant expression, or null if parsing fails.
     */
    private Node constantExpression() {
        return conditionalExpression();
    }

    /**
     * Parses a conditional expression, which can either be:
     * - A logical-or expression, or
     * - A ternary expression in the form of "logical-or-expression ? expression : conditional-expression".
     *
     * <pre>{@code
     * <conditional-expression> ::= <logical-or-expression>
     *     | <logical-or-expression> ? <expression> : <conditional-expression>
     * }</pre>
     *
     * @return A Node representing the conditional expression, or null if parsing fails.
     */
    private Node conditionalExpression() {
        Node logicalOrExpression = logicalOrExpression();
        if (logicalOrExpression == null) {
            return null;
        }

        if (match(TokenType.QUESTION_MARK)) {
            Node expression = expression();
            if (expression == null) {
                error(peek(), "Expected expression after '?'.");
                return null;
            }

            if (!match(TokenType.COLON)) {
                error(peek(), "Expected ':' after expression.");
                return null;
            }

            Node conditionalExpression = conditionalExpression();
            if (conditionalExpression == null) {
                error(peek(), "Expected conditional expression after ':'");
                return null;
            }
            return new Node(NodeType.CONDITIONAL_EXPRESSION, List.of(logicalOrExpression, expression, conditionalExpression));
        }
        return logicalOrExpression;
    }

    /**
     * Parses a logical OR expression, which can be a simple <logical-and-expression>
     * or a series of <logical-and-expression> joined by '||' operators.
     *
     * <pre>{@code
     * <logical-or-expression> ::= <logical-and-expression>
     *     | <logical-or-expression> || <logical-and-expression>
     * }</pre>
     *
     * @return A Node representing the logical OR expression, or null if parsing fails.
     */
    private Node logicalOrExpression() {
        Node left = logicalAndExpression();
        if (left == null) {
            return null;
        }

        while (match(TokenType.LOGICAL_OR)) {
            Token operator = previous();
            Node right = logicalAndExpression();

            if (right == null) {
                error(peek(), "Expected <logical-and-expression> after '||'.");
                return left;
            }
            left = new Node(NodeType.LOGICAL_OR_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses a logical AND expression, which can either be a single <inclusive-or-expression>
     * or a series of <inclusive-or-expression> joined by '&&' operators.
     *
     * <pre>{@code
     * <logical-and-expression> ::= <inclusive-or-expression>
     *     | <logical-and-expression> && <inclusive-or-expression>
     * }</pre>
     *
     * @return A Node representing the logical AND expression, or null if parsing fails.
     */
    private Node logicalAndExpression() {
        Node left = inclusiveOrExpression();
        if (left == null) {
            return null;
        }

        while (match(TokenType.LOGICAL_AND)) {
            Token operator = previous();
            Node right = inclusiveOrExpression();

            if (right == null) {
                error(peek(), "Expected <inclusive-or-expression> after '&&'.");
                return left;
            }
            left = new Node(NodeType.LOGICAL_AND_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses an inclusive OR expression, which can be a single <exclusive-or-expression>
     * or a sequence of <exclusive-or-expression> joined by '|' (bitwise OR) operators.
     *
     * <pre>{@code
     * <inclusive-or-expression> ::= <exclusive-or-expression>
     *     | <inclusive-or-expression> | <exclusive-or-expression>
     * }</pre>
     *
     * @return A Node representing the inclusive OR expression, or null if parsing fails.
     */
    private Node inclusiveOrExpression() {
        Node left = exclusiveOrExpression();
        if (left == null) {
            return null;
        }

        while (match(TokenType.BITWISE_OR)) {
            Token operator = previous();
            Node right = exclusiveOrExpression();

            if (right == null) {
                error(peek(), "Expected <exclusive-or-expression> after '|'.");
                return left;
            }
            left = new Node(NodeType.INCLUSIVE_OR_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses an exclusive OR expression, which can be a single <and-expression>
     * or a sequence of <and-expression> joined by '^' (bitwise XOR) operators.
     *
     * <pre>{@code
     * <exclusive-or-expression> ::= <and-expression>
     *     | <exclusive-or-expression> ^ <and-expression>
     * }</pre>
     *
     * @return A Node representing the exclusive OR expression, or null if parsing fails.
     */
    private Node exclusiveOrExpression() {
        Node left = andExpression();
        if (left == null) {
            return null;
        }

        while (match(TokenType.BITWISE_XOR)) {
            Token operator = previous();
            Node right = andExpression();

            if (right == null) {
                error(peek(), "Expected <and-expression> after '^'.");
                return left;
            }
            left = new Node(NodeType.EXCLUSIVE_OR_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses an AND expression, which consists of one or more <equality-expression>
     * joined by '&' (bitwise AND) operators.
     *
     * <pre>{@code
     * <and-expression> ::= <equality-expression>
     *     | <and-expression> & <equality-expression>
     * }</pre>
     *
     * @return A Node representing the AND expression, or null if parsing fails.
     */
    private Node andExpression() {
        Node left = equalityExpression();
        if (left == null) return null;

        while (match(TokenType.BITWISE_AND)) {
            Token operator = previous();
            Node right = equalityExpression();

            if (right == null) {
                error(peek(), "Expected <equality-expression> after '&'.");
                return left;
            }
            left = new Node(NodeType.AND_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses an equality expression, which consists of one or more <relational-expression>
     * joined by equality operators ('==' or '!=').
     *
     * <pre>{@code
     * <equality-expression> ::= <relational-expression>
     *     | <equality-expression> == <relational-expression>
     *     | <equality-expression> != <relational-expression>
     * }</pre>
     *
     * @return A Node representing the equality expression, or null if parsing fails.
     */
    private Node equalityExpression() {
        Node left = relationalExpression();
        if (left == null) return null;

        while (match(TokenType.EQUALS_EQUALS, TokenType.NOT_EQUALS)) {
            Token operator = previous();
            Node right = relationalExpression();

            if (right == null) {
                error(peek(), "Expected <relational-expression> after '==' or '!='.");
                return left;
            }
            left = new Node(NodeType.EQUALITY_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses a relational expression, which consists of one or more <shift-expression>
     * joined by relational operators ('<', '>', '<=', '>='), representing relational comparisons.
     *
     * <pre>{@code
     * <relational-expression> ::= <shift-expression>
     *     | <relational-expression> < <shift-expression>
     *     | <relational-expression> > <shift-expression>
     *     | <relational-expression> <= <shift-expression>
     *     | <relational-expression> >= <shift-expression>
     * }</pre>
     *
     * @return A Node representing the relational expression, or null if parsing fails.
     */
    private Node relationalExpression() {
        Node left = shiftExpression();
        if (left == null) return null;

        while (match(TokenType.LESS_THAN, TokenType.GREATER_THAN, TokenType.LESS_EQUALS, TokenType.GREATER_EQUALS)) {
            Token operator = previous();
            Node right = shiftExpression();

            if (right == null) {
                error(peek(), "Expected <shift-expression> after relational operator.");
                return left;
            }
            left = new Node(NodeType.RELATIONAL_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses a shift expression, which consists of one or more <additive-expression>
     * joined by shift operators ('<<', '>>').
     *
     * <pre>{@code
     * <shift-expression> ::= <additive-expression>
     *     | <shift-expression> << <additive-expression>
     *     | <shift-expression> >> <additive-expression>
     * }</pre>
     *
     * @return A Node representing the shift expression, or null if parsing fails.
     */
    private Node shiftExpression() {
        Node left = additiveExpression();
        if (left == null) return null;

        while (match(TokenType.LEFT_SHIFT, TokenType.RIGHT_SHIFT)) {
            Token operator = previous();
            Node right = additiveExpression();

            if (right == null) {
                error(peek(), "Expected <additive-expression> after shift operator.");
                return left;
            }
            left = new Node(NodeType.SHIFT_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses an additive expression, which consists of one or more <multiplicative-expression>
     * joined by addition or subtraction operators ('+' or '-').
     *
     * <pre>{@code
     * <additive-expression> ::= <multiplicative-expression>
     *     | <additive-expression> + <multiplicative-expression>
     *     | <additive-expression> - <multiplicative-expression>
     * }</pre>
     *
     * @return A Node representing the additive expression, or null if parsing fails.
     */
    private Node additiveExpression() {
        Node left = multiplicativeExpression();
        if (left == null) return null;

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Node right = multiplicativeExpression();

            if (right == null) {
                error(peek(), "Expected <multiplicative-expression> after " + operator.getLexeme());
                return left;
            }
            left = new Node(NodeType.ADDITIVE_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses a multiplicative expression, which consists of a cast-expression
     * optionally followed by one or more multiplication (*), division (/), or modulus (%) operators
     * joined with additional cast-expression elements.
     *
     * <pre>{@code
     * <multiplicative-expression> ::= <cast-expression>
     *     | <multiplicative-expression> * <cast-expression>
     *     | <multiplicative-expression> / <cast-expression>
     *     | <multiplicative-expression> % <cast-expression>
     * }</pre>
     *
     * @return A Node representing the multiplicative expression, or null if parsing fails.
     */
    private Node multiplicativeExpression() {
        Node left = castExpression();
        if (left == null) return null;

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            Token operator = previous();
            Node right = castExpression();

            if (right == null) {
                error(peek(), "Expected <cast-expression> after " + operator.getLexeme());
                return left;
            }
            left = new Node(NodeType.MULTIPLICATIVE_EXPRESSION, List.of(left, right), operator.getLexeme());
        }
        return left;
    }

    /**
     * Parses a cast expression, which can either be a unary expression or
     * a type-cast expression of the form ( <type-name> ) <cast-expression>.
     *
     * <pre>{@code
     * <cast-expression> ::= <unary-expression>
     *     | ( <type-name> ) <cast-expression>
     * }</pre>
     *
     * @return A Node representing the cast expression, or null if parsing fails.
     */
    private Node castExpression() {
        Node unary = unaryExpression();
        if (unary != null) {
            return unary;
        }

        if (match(TokenType.LEFT_PAREN)) {
            Node typeName = typeName();
            if (typeName != null) {
                if (match(TokenType.RIGHT_PAREN)) {
                    Node castExpr = castExpression();
                    if (castExpr != null) {
                        return new Node(NodeType.CAST_EXPRESSION, List.of(typeName, castExpr));
                    } else {
                        error(peek(), "Expected <cast-expression> after cast.");
                        return null;
                    }
                } else {
                    error(peek(), "Expected ')' after <type-name>.");
                    return null;
                }
            } else {
                error(peek(), "Expected <type-name> in cast expression.");
                return null;
            }
        }
        return null;
    }

    /**
     * Parses a unary expression, which can be:
     * - a postfix expression,
     * - a pre-increment or pre-decrement (e.g., ++<expr>, --<expr>),
     * - a unary operator applied to a cast expression,
     * - a sizeof expression applied to a unary expression or a type name.
     *
     * <pre>{@code
     * <unary-expression> ::= <postfix-expression>
     *     | ++ <unary-expression>
     *     | -- <unary-expression>
     *     | <unary-operator> <cast-expression>
     *     | sizeof <unary-expression>
     *     | sizeof <type-name>
     * }</pre>
     *
     * @return A Node representing the unary expression, or null if parsing fails.
     */
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
        }

        if (match(TokenType.SIZEOF)) {
            Node unaryExpr = unaryExpression();
            if (unaryExpr != null) {
                return new Node(NodeType.UNARY_EXPRESSION, List.of(unaryExpr));
            } else {
                error(peek(), "Expected <unary-expression> after 'sizeof'.");
                return null;
            }
        }

        if (match(TokenType.SIZEOF)) {
            Node typeName = typeName();
            if (typeName != null) {
                return new Node(NodeType.UNARY_EXPRESSION, List.of(typeName));
            } else {
                error(peek(), "Expected <type-name> after 'sizeof'.");
                return null;
            }
        }

        Node operator = unaryOperator();
        if (operator != null) {
            Node castExpression = castExpression();
            if (castExpression != null) {
                return new Node(NodeType.UNARY_EXPRESSION, List.of(operator, castExpression));
            } else {
                return null;
            }
        }
        return postfixExpression();
    }

    // <postfix-expression> ::= <primary-expression>
    //| <postfix-expression> [ <expression> ]
    //| <postfix-expression> ( {<assignment-expression>}* )
    //| <postfix-expression> . <identifier>
    //| <postfix-expression> -> <identifier>
    //| <postfix-expression> ++
    //| <postfix-expression> --
    private Node postfixExpression() {
        // First, parse the primary expression (base case)
        Node primaryExpression = primaryExpression();

        while (match(TokenType.LEFT_BRACE)) {
            // Parse the expression inside the brackets
            Node expression = expression();

            // TODO: Figure this out this is what is messing with the parser

            // Expect the closing bracket
            if (expression != null && match(TokenType.RIGHT_BRACE)) {
                // Build a new node representing the postfix operation (subscript)
                primaryExpression = new Node(NodeType.POSTFIX_EXPRESSION, List.of(primaryExpression, expression));
            } else {
                // If the expression is invalid or there's no closing bracket, handle error
                error(peek(),"Expected closing bracket ']' after expression.");
                return null;
            }
        }

        return primaryExpression;
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
