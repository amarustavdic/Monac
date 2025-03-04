package com.monac.compiler.parser.rules.specifier;

import com.monac.compiler.parser.Parser;
import com.monac.compiler.parser.rules.qualifier.TypeQualifier;
import com.monac.compiler.parser.tree.Node;
import com.monac.compiler.parser.tree.NodeType;

import java.util.List;

/**
 * Parses a declaration specifier, which defines properties of a declaration.
 *
 * <p>A declaration specifier determines aspects of a declaration, such as its storage class,
 * type, or qualifiers.</p>
 *
 * <p>Grammar rule:</p>
 * <pre>{@code
 * <declaration-specifier> ::= <storage-class-specifier>
 * | <type-specifier>
 * | <type-qualifier>
 * }</pre>
 *
 * <p>This class sequentially attempts to parse each possible specifier in order of precedence.</p>
 */
public final class DeclarationSpecifier {

    /**
     * Parses a declaration specifier from the provided parser.
     *
     * <p>Attempts to parse the following specifiers in order:</p>
     * <ol>
     *     <li>Storage class specifier</li>
     *     <li>Type specifier</li>
     *     <li>Type qualifier</li>
     * </ol>
     *
     * @param parser The parser instance used to extract tokens.
     * @return A {@link Node} representing the parsed declaration specifier,
     *         or {@code null} if no valid specifier is found.
     */
    public static Node parse(Parser parser) {

        Node result = new Node(NodeType.DECLARATION_SPECIFIER, 0, 0);

        Node storageClassSpecifier = StorageClassSpecifier.parse(parser);
        if (storageClassSpecifier != null) {
            result.setChildren(List.of(storageClassSpecifier));
            return result;
        };

        Node typeSpecifier = TypeSpecifier.parse(parser);
        if (typeSpecifier != null) {
            result.setChildren(List.of(typeSpecifier));
            return result;
        };

        Node typeQualifier = TypeQualifier.parse(parser);
        if (typeQualifier != null) {
            result.setChildren(List.of(typeQualifier));
            return result;
        }

        return null;
    }

}
