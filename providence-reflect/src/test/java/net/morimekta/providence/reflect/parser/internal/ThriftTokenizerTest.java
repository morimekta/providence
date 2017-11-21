package net.morimekta.providence.reflect.parser.internal;

import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.serializer.pretty.Token;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ThriftTokenizerTest {
    @Test
    public void testJavaComments() throws IOException {
        try {
            tokenizer("/").next();
            fail("no exception");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 1, pos 1: Expected java-style comment, got end of file\n" +
                    "/\n" +
                    "^"));
        }

        try {
            tokenizer("/b").next();
            fail("no exception");
        } catch (ParseException e) {
            assertThat(e.asString(), is(
                    "Error on line 1, pos 1: Expected java-style comment, got 'b' after '/'\n" +
                    "/b\n" +
                    "^^"));
        }

        Token token = tokenizer("\n\n// b\n").next();
        assertThat(token, is(notNullValue()));
        assertThat(token.asString(), is("//"));
    }

    private ThriftTokenizer tokenizer(String data) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        return new ThriftTokenizer(in);
    }
}
