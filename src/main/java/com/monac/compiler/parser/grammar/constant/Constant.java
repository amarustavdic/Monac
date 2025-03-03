package com.monac.compiler.parser.grammar.constant;

import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.Parser;

/**
 * A utility class for parsing constant values in the source code.
 * <p>
 * This class attempts to parse different types of constants (integer, character,
 * floating-point, and enumeration constants) from the given parser instance.
 * It sequentially checks for each type and returns the corresponding AST node.
 * </p>
 */
public final class Constant {

    /**
     * Parses a constant value from the parser.
     * <p>
     * This method attempts to parse the token as an integer, character, floating-point,
     * or enumeration constant in that order. If a match is found, it returns the corresponding
     * {@link Node}. If no match is found, it defaults to parsing an enumeration constant.
     * </p>
     *
     * @param parser The parser instance used to analyze the token stream.
     * @return A {@link Node} representing the parsed constant in the AST,
     *         or {@code null} if no valid constant is found.
     */
    public static Node parse(Parser parser) {

        Node i = IntegerConstant.parse(parser);
        if (i != null) return i;

        Node c = CharacterConstant.parse(parser);
        if (c != null) return c;

        Node f = FloatingConstant.parse(parser);
        if (f != null) return f;

        return EnumerationConstant.parse(parser);
    }

}
