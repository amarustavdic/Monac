package com.monac.compiler.parser;

import com.monac.compiler.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConstantTest {

    @Test
    void testValidIntegerConstant() throws ParserException {
        Lexer lexer = new Lexer("32");
        Parser parser = new Parser(lexer.tokenize());

        Node result = parser.constant();

        assertNotNull(result);
        assertEquals(NodeType.CONSTANT, result.getType());
    }

    @Test
    void testInvalidIntegerConstant() {

    }

    @Test
    void testValidCharacterConstant() {

    }

    @Test
    void testInvalidCharacterConstant() {

    }

    @Test
    void testValidFloatingConstant() {

    }

    @Test
    void testInvalidFloatingConstant() {

    }

    @Test
    void testValidEnumerationConstant() {

    }

    @Test
    void testInvalidEnumerationConstant() {

    }

    @Test
    void testEmptyInput() {

    }

    @Test
    void testUnexpectedEndAfterConstant() {

    }

    @Test
    void testIntegerBoundary() {

    }

    @Test
    void testFloatingBoundary() {

    }

    @Test
    void testParserExceptionMessage() {

    }

}