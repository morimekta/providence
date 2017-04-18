package net.morimekta.providence.util_internal;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.test.providence.core.calculator.Calculator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestUtils {
    public static PServiceCall decode(byte[] bytes, Serializer serializer) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return serializer.deserialize(in, Calculator.kDescriptor);
    }

    @SuppressWarnings("unchecked")
    public static byte[] encode(PServiceCall call, Serializer serializer) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(out, call);
        return out.toByteArray();
    }
}
