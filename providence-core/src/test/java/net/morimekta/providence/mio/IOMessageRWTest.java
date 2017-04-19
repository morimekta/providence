package net.morimekta.providence.mio;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.test.providence.core.OptionalFields;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Operation;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IOMessageRWTest {
    private CompactFields m1 = new CompactFields("name", 1234, "Message");
    private OptionalFields m2 = OptionalFields.builder()
                                      .setStringValue("string")
                                      .setIntegerValue(1234)
                                      .setDoubleValue(4321.1234)
                                      .build();

    @Test
    public void testBinary() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (MessageWriter writer = new IOMessageWriter(baos, new BinarySerializer())) {
            writer.write(m1);
            writer.separator();
            writer.write(m2);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (MessageReader reader = new IOMessageReader(bais, new BinarySerializer())) {
            assertThat(m1, is(equalTo(reader.read(CompactFields.kDescriptor))));
            assertThat(m2, is(equalTo(reader.read(OptionalFields.kDescriptor))));
        }
    }
    @Test
    public void testReadable() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (MessageWriter writer = new IOMessageWriter(baos, new JsonSerializer())) {
            writer.write(m1);
            writer.separator();
            writer.write(m2);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (MessageReader reader = new IOMessageReader(bais, new JsonSerializer())) {
            assertThat(m1, is(equalTo(reader.read(CompactFields.kDescriptor))));
            assertThat(m2, is(equalTo(reader.read(OptionalFields.kDescriptor))));
        }
    }

    @Test
    public void testService() throws IOException {
        String content = "[\"calculate\",\"call\",44,{\"op\":{\"operator\":\"ADD\",\"operands\":[]}}]";
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8));

        PServiceCall<Operation, Operation._Field> call = null;
        try (MessageReader reader = new IOMessageReader(in, new JsonSerializer())) {
            call = reader.read(Calculator.kDescriptor);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (MessageWriter writer = new IOMessageWriter(out, new JsonSerializer().named())) {
            writer.write(call);
        }

        assertThat(new String(out.toByteArray(), UTF_8),
                   is(equalTo(content)));
    }
}
