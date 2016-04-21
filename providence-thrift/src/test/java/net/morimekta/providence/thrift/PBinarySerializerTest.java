package net.morimekta.providence.thrift;

import org.junit.Before;
import org.junit.Test;

/**
 * Incidentally actually a test that the PBinarySerializer generates the same output as
 * TBinaryProtocolSerializer, and can also read back what TBinaryProtocolSerializer
 * generates.
 */
public class PBinarySerializerTest {
    @Before
    public void setUp() {

    }

    @Test
    public void testProvidenceToThrift() {
        // Providence client talks to thrift service.
    }

    @Test
    public void testThriftToProvidence() {
        // Thrift client talks to providence service.
    }
}
