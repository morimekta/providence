package net.morimekta.providence.serializer.pretty;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TokenizerTest {
    @Test
    public void testGood() throws IOException {
        assertGood(
                "#ignore \r\n" +
                "{\n" +
                "  # ignore\n" +
                "  boo\r\n" +
                "}\r", "{", "boo", "}");
        assertGood("12", "12");
        assertGood("012:", "012", ":");
        assertGood("0x44,", "0x44", ",");
        assertGood("0.1235;", "0.1235", ";");
        assertGood(".2345", ".2345");
        assertGood(".2345e-5", ".2345e-5");
        assertGood("55.2345e+5", "55.2345e+5");
    }

    @Test
    public void testFailures() throws IOException {
        assertFail_anything("",
                            "Error: Expected anything, got end of file");

        // --- literals
        assertFail_anything("\n\n\"\\\"",
                            "Error on line 3, pos 1: Unexpected end of stream in literal\n" +
                            "\"\\\"\n" +
                            "^^^");
        assertFail_anything("\n\n  \"\n\"",
                            "Error on line 3, pos 3: Unexpected line break in literal\n" +
                            "  \"\n" +
                            "--^");
        assertFail_anything("\n\n  \"\003\"",
                            "Error on line 3, pos 3: Unescaped non-printable char in literal: '\\003'\n" +
                            "  \"\u0003\"\n" +
                            "--^^");

        // --- numbers
        assertFail_anything("12f",
                            "Error on line 1, pos 1: Invalid termination of number: '12f'\n" +
                            "12f\n" +
                            "^^^");
        assertFail_anything("      12f",
                            "Error on line 1, pos 7: Invalid termination of number: '12f'\n" +
                            "      12f\n" +
                            "------^^^");
        assertFail_anything("-:",
                            "Error on line 1, pos 1: No decimal after negative indicator\n" +
                            "-:\n" +
                            "^");
        assertFail_anything("-",
                            "Error on line 1, pos 1: Unexpected end of stream after negative indicator\n" +
                            "-\n" +
                            "^");
        assertFail_anything(".5e:",
                            "Error on line 1, pos 1: Missing exponent value\n" +
                            ".5e:\n" +
                            "^^^^");
        assertFail_anything("\n  .5e",
                            "Error on line 2, pos 3: Unexpected end of stream after exponent indicator\n" +
                            "  .5e\n" +
                            "--^^^");

        // --- identifiers
        assertFail_anything("e..b",
                            "Error on line 1, pos 1: Identifier with double '.'\n" +
                            "e..b\n" +
                            "^^^");
        assertFail_anything("e.:",
                            "Error on line 1, pos 1: Identifier with trailing '.'\n" +
                            "e.:\n" +
                            "^^");
        assertFail_anything("e.7:",
                            "Error on line 1, pos 1: Identifier part starting with digit '7'\n" +
                            "e.7:\n" +
                            "^^^");
        assertFail_anything("e-",
                            "Error on line 1, pos 1: Wrongly terminated identifier: '-'\n" +
                            "e-\n" +
                            "^");

        // --- nothing at all

        assertFail_anything("\\4",
                            "Error on line 1, pos 1: Unknown token initiator '\\'\n" +
                            "\\4\n" +
                            "^");
    }

    @Test
    public void testExpectFails() throws IOException {
        try {
            tokenizer("").expectIdentifier("id");
            fail();
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Expected id, got end of file"));
        }

        try {
            tokenizer("123").expectIdentifier("id");
            fail();
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Expected id, but got '123'"));
        }

        try {
            tokenizer("").expectStringLiteral("id");
            fail();
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Expected id, got end of file"));
        }

        try {
            tokenizer("123").expectStringLiteral("id");
            fail();
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Expected id, but got '123'"));
        }

        try {
            tokenizer("").expectSymbol("id", '&', '%');
            fail();
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Expected id, one of ['&', '%'], got end of file"));
        }

        try {
            tokenizer("123").expectSymbol("id", '&', '%');
            fail();
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Expected id, one of ['&', '%'], but found '123'"));
        }

        try {
            tokenizer("").peek("id");
            fail();
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is("Expected id, got end of file"));
        }

    }

    private void assertGood(String text, String... tokens) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        Tokenizer tokenizer = new Tokenizer(in, true);

        for (String token : tokens) {
            assertThat(tokenizer.expect("anything").asString(), is(token));
        }
    }

    private Tokenizer tokenizer(String text) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        return new Tokenizer(in, false);
    }

    private void assertFail_anything(String text, String out) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        Tokenizer tokenizer = new Tokenizer(in, true);
        try {
            tokenizer.expect("anything");
            fail("no exception");
        } catch (TokenizerException e) {
            assertThat(e.asString(), is(out));
        }
    }
}
