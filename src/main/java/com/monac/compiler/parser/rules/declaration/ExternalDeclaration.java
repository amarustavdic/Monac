package com.monac.compiler.parser.rules.declaration;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.definition.FunctionDefinition;
import com.monac.compiler.parser.tree.Node;

/**
 * Parses an external declaration in the source code.
 * <p>An external declaration represents a top-level declaration in a program.
 * It can be either:</p>
 * <ul>
 *      <li>A function definition</li>
 *      <li>A declaration (e.g., a variable or type definition)</li>
 * </ul>
 *
 * <p>Grammar rule:</p>
 * <pre>{@code
 * <external-declaration> ::= <function-definition>
 * | <declaration>
 * }</pre>
 *
 * <p>This rule is used to process global definitions in the program.</p>
 */
public final class ExternalDeclaration {

    /**
     * Parses an external declaration from the provided parser.
     *
     * <p>Attempts to parse a function definition first. If that fails, it tries to parse
     * a general declaration.</p>
     *
     * @param parser The parser instance used to extract tokens.
     * @return A {@link Node} representing the parsed external declaration,
     *         or {@code null} if neither a function definition nor a declaration is found.
     */
    public static Node parse(Parser parser) {

        // Attempt to parse a function definition
        Node functionDefinition = FunctionDefinition.parse(parser);
        if (functionDefinition != null) return functionDefinition;

        // If not a function definition, attempt to parse a declaration
        return Declaration.parse(parser);
    }

}
