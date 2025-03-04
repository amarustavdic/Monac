package com.monac.compiler.parser.rules.unit;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.declaration.ExternalDeclaration;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the root of a compilation unit, representing the entire source file.
 *
 * <p>A translation unit consists of zero or more external declarations, which can be:</p>
 * <ul>
 *     <li>Function definitions</li>
 *     <li>Global declarations (e.g., variables, type definitions)</li>
 * </ul>
 *
 * <p>Grammar rule:</p>
 * <pre>{@code
 * <translation-unit> ::= {<external-declaration>}*
 * }</pre>
 *
 * <p>This class serves as the entry point for parsing a full source file.</p>
 */
public final class TranslationUnit {

    /**
     * Parses a translation unit from the given parser.
     *
     * <p>Extracts all external declarations found in the source file.
     * If no valid external declarations are present, it returns {@code null}.</p>
     *
     * @param parser The parser instance used to extract tokens.
     * @return A {@link Node} representing the parsed translation unit,
     *         or {@code null} if no valid external declarations are found.
     */
    public static Node parse(Parser parser) {
        List<Node> children = new ArrayList<>();

        // Parse all external declarations in the file
        Node externalDeclaration;
        while ((externalDeclaration = ExternalDeclaration.parse(parser)) != null) {
            children.add(externalDeclaration);
        }

        // If no valid declarations are found, return null
        if (children.isEmpty()) return null;

        // Create a root node representing the translation unit
        Node result = new Node(
                NodeType.TRANSLATION_UNIT,
                children.getFirst().getLine(),
                children.getLast().getLine()
        );
        result.setChildren(children);
        result.setLiteral("<translation-unit>");

        return result;
    }

}
