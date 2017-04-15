package net.morimekta.providence.serializer;

import net.morimekta.providence.test_internal.Containers;
import net.morimekta.providence.util.ProvidenceHelper;
import net.morimekta.providence.util.pretty.TokenizerException;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BinarySerializerTest {
    private static ArrayList<Containers> containers;

    @Before
    public void setUp() throws IOException {
        synchronized (SerializerTest.class) {
            // Since these are immutable, we don't need to read for each test.
            if (containers == null) {
                containers = ProvidenceHelper.arrayListFromJsonResource("/compat/compact.json", Containers.kDescriptor);
            }
        }
    }

    @Test
    public void testNonPrecompiled() throws IOException {
        Serializer serializer = new BinarySerializer();

        // Just a sanity check.
        assertTrue(containers.size() == 10);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais;
        int size;

        // complex message, one at a time.
        for (int i = 0; i < 10; ++i) {
            baos.reset();

            Containers expected = containers.get(i);
            size = serializer.serialize(baos, expected);
            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());
            Containers actual;
            try {
                actual = serializer.deserialize(bais, Containers.kDescriptor);
            } catch (TokenizerException e) {
                System.err.println(new String(baos.toByteArray(), StandardCharsets.UTF_8));
                System.err.println(e.asString());
                fail("oops");
                return;
            }

            assertEquals(actual, expected);
        }

        // complex message in stream.
        {
            baos.reset();
            size = 0;
            for (int i = 0; i < 10; ++i) {
                size += serializer.serialize(baos, containers.get(i));
            }

            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());

            for (int i = 0; i < 10; ++i) {
                Containers expected = containers.get(i);
                Containers actual = serializer.deserialize(bais, Containers.kDescriptor);

                assertEquals(actual, expected);
            }

            assertEquals(0, bais.available());
        }
    }
}
