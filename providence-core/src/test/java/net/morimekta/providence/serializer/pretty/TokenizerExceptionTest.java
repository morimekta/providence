package net.morimekta.providence.serializer.pretty;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TokenizerExceptionTest {
    @Test
    public void testException() {
        TokenizerException e = new TokenizerException("message");
        assertThat(e.getMessage(), is("message"));
        assertThat(e.toString(), is("TokenizerException{message}"));
        e.setFile("file");
        e.setLineNo(1);
        e.setLinePos(4);
        e.setLength(5);
        e.setLine("   balls");

        assertThat(e.toString(), is("TokenizerException{message, file=file, line=1, pos=4, len=5}"));
        assertThat(e.asString(), is(
                "Error in file on line 1, pos 4: message\n" +
                "   balls\n" +
                "---^^^^^"));
    }
}
