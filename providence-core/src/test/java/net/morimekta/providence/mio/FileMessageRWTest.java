package net.morimekta.providence.mio;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.providence.core.CompactFields;
import net.morimekta.test.providence.core.OptionalFields;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileMessageRWTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();


    private CompactFields m1 = new CompactFields("name", 1234, "Message");
    private OptionalFields m2 = OptionalFields.builder()
                                      .setStringValue("string")
                                      .setIntegerValue(1234)
                                      .setDoubleValue(4321.1234)
                                      .build();

    @Test
    public void testBinary() throws IOException {
        File test = tmp.newFile();

        try (FileMessageWriter writer = new FileMessageWriter(test, new BinarySerializer())) {
            writer.write(m1);
            writer.separator();
            writer.write(m2);
        }

        try (FileMessageReader reader = new FileMessageReader(test, new BinarySerializer())) {
            assertThat(m1, is(equalTo(reader.read(CompactFields.kDescriptor))));
            assertThat(m2, is(equalTo(reader.read(OptionalFields.kDescriptor))));
        }
    }
    @Test
    public void testReadable() throws IOException {
        File test = tmp.newFile();

        try (FileMessageWriter writer = new FileMessageWriter(test, new JsonSerializer())) {
            writer.write(m1);
            writer.separator();
            writer.write(m2);
        }

        try (FileMessageReader reader = new FileMessageReader(test, new JsonSerializer())) {
            assertThat(m1, is(equalTo(reader.read(CompactFields.kDescriptor))));
            assertThat(m2, is(equalTo(reader.read(OptionalFields.kDescriptor))));
        }
    }
}
