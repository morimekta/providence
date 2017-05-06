package net.morimekta.providence.serializer;

import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.util.json.JsonException;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JsonSerializerExceptionTest {
    @Test
    public void testException() {
        JsonException cause = new JsonException("foo", "bar bar bar", 1, 5, 3);
        JsonSerializerException ex = new JsonSerializerException(cause);
        ex.setMethodName("bar");
        ex.setSequenceNo(42);
        ex.setCallType(PServiceCallType.CALL);

        assertThat(ex.getMessage(), is("foo"));
        assertThat(ex.getLine(), is("bar bar bar"));
        assertThat(ex.getLineNo(), is(1));
        assertThat(ex.getLinePos(), is(5));
        assertThat(ex.getCause(), is(sameInstance(cause)));
        assertThat(ex.getLen(), is(3));


        assertThat(ex.asString(), is(
                "JSON Error in bar on line 1: foo\n" +
                "bar bar bar\n" +
                "----^^^"));
        assertThat(ex.toString(), is("JsonSerializerException{foo, line=1, pos=5, method=bar, type=CALL, seq=42}"));

        cause = new JsonException("foo", null, 0, 0, 0);
        ex = new JsonSerializerException(cause);
        ex.setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);

        assertThat(ex.getMessage(), is("foo"));
        assertThat(ex.getLine(), is(nullValue()));
        assertThat(ex.getCause(), is(sameInstance(cause)));

        assertThat(ex.asString(), is(
                "JSON Error: foo"));
        assertThat(ex.toString(), is("JsonSerializerException{foo, e=INVALID_MESSAGE_TYPE}"));

        try {
            ex.initCause(new IOException());
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }
}
