package net.morimekta.providence.serializer;

import net.morimekta.providence.PServiceCallType;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SerializerExceptionTest {
    @Test
    public void testToString() {
        assertThat(new SerializerException("Message").toString(),
                   is("SerializerException{Message, exception=PROTOCOL_ERROR}"));
        assertThat(new SerializerException("Message").setMethodName("foo")
                                                     .setCallType(PServiceCallType.CALL)
                                                     .setSequenceNo(123)
                                                     .toString(),
                   is("SerializerException{Message, method=foo, type=CALL, seq=123, exception=PROTOCOL_ERROR}"));
    }

    @Test
    public void testAsString() {
        assertThat(new SerializerException("Message").asString(),
                   is("Error: Message"));
        assertThat(new SerializerException("Message").setMethodName("foo")
                                                     .setCallType(PServiceCallType.CALL)
                                                     .setSequenceNo(123)
                                                     .asString(),
                   is("Error in foo(): Message"));
    }
}
