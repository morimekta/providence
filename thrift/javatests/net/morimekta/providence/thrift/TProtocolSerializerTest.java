package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.test.providence.Containers;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static net.morimekta.providence.testing.MessageReader.arrayListFromJsonResource;

/**
 */
public class TProtocolSerializerTest {
    private static ArrayList<Containers> containers;

    @Before
    public void setUp() throws PSerializeException, IOException {
        synchronized (TProtocolSerializerTest.class) {
            // Since these are immutable, we don't need to read for each test.
            if (containers == null) {
                containers = arrayListFromJsonResource("/providence/test.json", Containers.kDescriptor);
            }
        }
    }

    public void testRecoding(TProtocolFactory factory, TProtocolSerializer serializer) {
        // TODO: Make tests.
    }

    @Test
    public void testTBinaryProtocol() {
        testRecoding(new TBinaryProtocol.Factory(), new TBinaryProtocolSerializer());
    }
}
