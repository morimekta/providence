package net.morimekta.providence.jax.rs;

import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.calculator.Operand;

import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 23.10.15
 */
public class ProvidenceMessageBodyWriterTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testGetDescriptor() {
        ProvidenceMessageBodyWriter reader = new DefaultProvidenceMessageBodyWriter();
        Annotation[] annotations = new Annotation[0];

        // This should work.
        assertTrue(reader.isWriteable(Operand.class, null, annotations, MediaType.valueOf(JsonSerializer.MIME_TYPE)));
        assertTrue(reader.isWriteable(Operand.class, null, annotations, MediaType.valueOf(JsonSerializer.JSON_MIME_TYPE + "; encoding=utf-8")));
        assertTrue(reader.isWriteable(Operand.class, null, annotations, MediaType.valueOf(BinarySerializer.MIME_TYPE)));
        assertTrue(reader.isWriteable(Operand.class, null, annotations, MediaType.valueOf(BinarySerializer.ALT_MIME_TYPE)));

        // Wrong class, correct media type.
        assertFalse(reader.isWriteable(PMap.class, null, annotations, MediaType.valueOf(BinarySerializer.MIME_TYPE)));
        assertFalse(reader.isWriteable(String.class, null, annotations, MediaType.valueOf(BinarySerializer.ALT_MIME_TYPE)));

        // Correct class, wrong media type.
        assertFalse(reader.isWriteable(Operand.class, null, annotations, MediaType.WILDCARD_TYPE));
        assertFalse(reader.isWriteable(Operand.class, null, annotations, MediaType.TEXT_PLAIN_TYPE));
    }
}
