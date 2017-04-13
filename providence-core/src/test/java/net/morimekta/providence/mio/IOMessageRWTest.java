package net.morimekta.providence.mio;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.test.providence.core.OptionalFields;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    }}
