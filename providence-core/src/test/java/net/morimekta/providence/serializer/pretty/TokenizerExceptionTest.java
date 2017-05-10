package net.morimekta.providence.serializer.pretty;

import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PServiceCallType;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
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

        assertThat(e.toString(), is("TokenizerException{message, file=file}"));
        assertThat(e.asString(), is(
                "Error in file on line 1, pos 4: message"));

        e.setLine("   balls");

        assertThat(e.toString(), is("TokenizerException{message, file=file, line=1, pos=4, len=5}"));
        assertThat(e.asString(), is(
                "Error in file on line 1, pos 4: message\n" +
                "   balls\n" +
                "---^^^^^"));

        e.setMethodName("balle");
        e.setSequenceNo(44);
        e.setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
        e.setCallType(PServiceCallType.CALL);

        assertThat(e.toString(),
                   is("TokenizerException{message, file=file, e=INVALID_MESSAGE_TYPE, line=1, pos=4, len=5, method=balle, type=CALL, seq=44}"));
        assertThat(e.asString(),
                   is("Error in file on line 1, pos 4: message\n" +
                      "   balls\n" +
                      "---^^^^^"));
    }

    @Test
    public void testConstructor() {
        NumberFormatException nfe = new NumberFormatException("nfe");
        TokenizerException a = new TokenizerException(nfe, "message %s", "is");
        TokenizerException b = new TokenizerException(a, new File("file"));

        assertThat(a.getCause(), is(sameInstance(nfe)));
        assertThat(a.getMessage(), is("message is"));

        assertThat(b.getMessage(), is(a.getMessage()));
        assertThat(b.getFile(), is("file"));
        assertThat(b.getCause(), is(a));
        assertThat(b.getLength(), is(a.getLength()));
        assertThat(b.getLine(), is(a.getLine()));
        assertThat(b.getLineNo(), is(a.getLineNo()));
        assertThat(b.getLinePos(), is(a.getLinePos()));

        assertThat(b.asString(), is("Error: message is"));

        TokenizerException c = new TokenizerException(b, new File("other"));
        assertThat(c.getFile(), is(b.getFile()));
    }
}
