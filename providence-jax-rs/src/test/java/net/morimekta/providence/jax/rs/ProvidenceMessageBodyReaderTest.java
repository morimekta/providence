package net.morimekta.providence.jax.rs;

import net.morimekta.providence.descriptor.PMap;
import net.morimekta.test.calculator.Operand;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 23.10.15
 */
public class ProvidenceMessageBodyReaderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testGetDescriptor() {
        ProvidenceMessageBodyReader reader = new DefaultProvidenceMessageBodyReader();

        assertTrue(reader.isReadable(Operand.class, null, null, null));
        assertFalse(reader.isReadable(PMap.class, null, null, null));
        assertFalse(reader.isReadable(String.class, null, null, null));
    }
}
