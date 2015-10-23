package org.apache.thrift.j2.jax.rs;

import org.apache.test.calculator.Operand;
import org.apache.thrift.j2.descriptor.TMap;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 23.10.15.
 */
public class TCompactJsonMessageBodyReaderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testGetDescriptor() {
        TCompactJsonMessageBodyReader reader = new TCompactJsonMessageBodyReader();

        assertTrue(reader.isReadable(Operand.class, null, null, null));
        assertFalse(reader.isReadable(TMap.class, null, null, null));
        assertFalse(reader.isReadable(String.class, null, null, null));
    }
}
