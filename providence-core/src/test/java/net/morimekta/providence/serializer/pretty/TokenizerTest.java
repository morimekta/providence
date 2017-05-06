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
    public void testFailures() throws IOException {
        assertFail_anything("",
                            "Expected anything, got end of file");
        assertFail_anything("12f",
                            "Error on line 1, pos 1: Invalid termination of number: '12f'\n" +
                            "12f\n" +
                            "^^^");
        assertFail_anything("      12f",
                            "Error on line 1, pos 7: Invalid termination of number: '12f'\n" +
                            "      12f\n" +
                            "------^^^");
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
